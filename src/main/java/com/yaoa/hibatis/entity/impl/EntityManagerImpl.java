/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.entity.impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.yaoa.hibatis.cache.CacheCollection;
import com.yaoa.hibatis.cache.EntityCacheManager;
import com.yaoa.hibatis.cache.impl.EntityLockKeyGenerator;
import com.yaoa.hibatis.entity.EntityEnhancer;
import com.yaoa.hibatis.entity.EntityManager;
import com.yaoa.hibatis.entity.EntityState;
import com.yaoa.hibatis.entity.HibatisEntity;
import com.yaoa.hibatis.exception.HibatisException;
import com.yaoa.hibatis.exception.HibatisValidateException;
import com.yaoa.hibatis.lock.Lock;
import com.yaoa.hibatis.lock.LockManager;
import com.yaoa.hibatis.lock.LockMode;
import com.yaoa.hibatis.metadata.AssociationProperty;
import com.yaoa.hibatis.metadata.CollectionProperty;
import com.yaoa.hibatis.metadata.ColumnProperty;
import com.yaoa.hibatis.metadata.EntityID;
import com.yaoa.hibatis.metadata.EntityMetadata;
import com.yaoa.hibatis.metadata.IdProperty;
import com.yaoa.hibatis.metadata.Root;
import com.yaoa.hibatis.query.Criterion;
import com.yaoa.hibatis.query.Page;
import com.yaoa.hibatis.query.aggregate.Aggregate;
import com.yaoa.hibatis.query.build.AggregateStatement;
import com.yaoa.hibatis.query.build.CriterionStatement;
import com.yaoa.hibatis.query.impl.PageImpl;
import com.yaoa.hibatis.transaction.TransactionCacheContext;
import com.yaoa.hibatis.transaction.TransactionCacheContextHolder;

/**
 * 
 * 实体管理器实现类
 * 
 * @author kingsy.lin
 * @version 1.0 , 2016年10月19日
 */
@SuppressWarnings("unchecked")
public class EntityManagerImpl extends EntityManager {

	private EntityCacheManager cacheManager = EntityCacheManager.getInstance();

	private Validator validator;

	private final static Log logger = LogFactory.getLog(EntityManager.class);

	public EntityManagerImpl(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
		try {
			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			this.validator = factory.getValidator();
		} catch (Exception e) {
			logger.warn("Unable to create  validator");
		}
	}

	@Override
	public <T> T findOne(Criterion criterion) {
		criterion.setFirstResult(0);
		criterion.setMaxResults(1);
		List<T> list = this.find(criterion);
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@Override
	public <T> T findById(Class<T> entityType, EntityID id, LockMode lockMode) {
		// 判断是否使用缓存并且非锁模式，则检查缓存
		boolean cacheable = cacheManager.cacheable(entityType);
		if (cacheable && lockMode == LockMode.NONE) {
			T entity = cacheManager.get(entityType, id);
			if (entity != null) {
				return entity;
			}
		}
		Lock lock = null;
		try {
			if (cacheable && lockMode == LockMode.NONE) {
				// 加锁，防止缓存穿透
				lock = LockManager.getEntityLock(entityType, id);
				lock.lock();
				T entity = cacheManager.get(entityType, id);
				if (entity != null) {
					return entity;
				}
			}
			// 是否使用读取锁
			if (lockMode == LockMode.READ) {
				lock = LockManager.getEntityLock(entityType, id);
				lock.lock();
			}
			String condition = ConditionBuilder.byId(entityType);
			T entity;
			// 是否使用读取锁
			if (lockMode == LockMode.READ) {
				// 防止胀读，把事务隔离级别设置为READ_COMMITTED
				Integer isolationLevel = TransactionSynchronizationManager.getCurrentTransactionIsolationLevel();
				if (isolationLevel == null || isolationLevel != Connection.TRANSACTION_READ_COMMITTED) {
					throw new HibatisException("锁模式为READ，当前事务隔离级别必须是READ_COMMITTED");
				}
				entity = sqlMapper.selectOne(entityType, condition, "", id, LockMode.NONE);
			} else {
				entity = sqlMapper.selectOne(entityType, condition, "", id, lockMode);
			}
			if (entity == null) {
				return null;
			}
			// 修改对象为持久状态
			this.setPersient((HibatisEntity) entity);
			// 判断是否使用缓存
			if (cacheable) {
				cacheManager.put((HibatisEntity) entity);
			}
			return entity;
		} finally {
			if (lock != null) {
				// 不使用锁则直接释放
				if (lockMode == LockMode.NONE) {
					lock.unlock();
				} else {
					// 判断是否在事务上下文环境
					TransactionCacheContext tx = TransactionCacheContextHolder.getContext();
					if (tx != null) {
						tx.realseLock(lock); // 锁托管到事务上下文
					} else {
						lock.unlock();
					}
				}
			}
		}
	}

	@Override
	public <T> void refresh(T entity, LockMode lockMode) {
		Class<T> entityType = (Class<T>) EntityEnhancer.getEntityType(entity);
		Root root = Root.get(entityType);
		EntityMetadata metadata = root.getMetadata();
		EntityID id = root.toId(entity);
		T newObject = findById(entityType, id, lockMode);
		//  复制属性
		Collection<ColumnProperty> properties = metadata.getSelectProperties();
		for (ColumnProperty property : properties) {
			Object value = property.getValue(newObject);
			property.setValue(entity, value);
		}
		// 丢失记录的属性
		if (entity instanceof HibatisEntity) {
			EntityEnhancer.discardPropertyChanges((HibatisEntity) entity);
		}
	}

	@Override
	public <T> List<T> findByIds(Class<T> entityType, List<EntityID> idList, LockMode lockMode) {
		// 使用缓存并且不加锁
		if (cacheManager.cacheable(entityType) && lockMode == LockMode.NONE) {
			List<T> result = new ArrayList<T>();
			for (EntityID id : idList) {
				T entity = this.findById(entityType, id, lockMode);
				result.add(entity);
			}
			return result;
		}
		String condition = ConditionBuilder.byId(entityType);
		List<?> parameters = idList;
		List<T> result = sqlMapper.selectUnionAll(entityType, condition, "", parameters, lockMode);
		for (T entity : result) {
			this.setPersient((HibatisEntity) entity);
		}
		return result;
	}

	@Override
	public long count(Criterion criterion) {
		Root root = criterion.getRoot();
		Class<?> entityType = root.getEntityType();
		CriterionStatement statement = new CriterionStatement(criterion);
		String condition = statement.getCondition();
		Object parameter = statement.getParameters();
		return sqlMapper.count(entityType, condition, parameter);
	}

	@Override
	public <T> List<T> find(Criterion criterion) {
		Root root = criterion.getRoot();
		Class<T> entityType = (Class<T>) root.getEntityType();
		CriterionStatement statement = new CriterionStatement(criterion);
		String condition = statement.getCondition();
		Object parameter = statement.getParameters();
		String orderBy = statement.getOrderBy();
		String cacheName = statement.getCacheName();
		LockMode lockMode = criterion.getLockMode();
		if (lockMode == LockMode.READ) {
			throw new HibatisException("当前查询暂不支持锁模式为READ");
		}
		boolean cacheable = criterion.isCacheable();
		if (cacheable && lockMode == LockMode.NONE) {
			CacheCollection<T> collection = cacheManager.getCollection(entityType, cacheName);
			if (collection != null) {
				return collection.getList();
			}
		}
		Lock lock = null;
		try {
			// 判断是否使用缓存
			if (cacheable && lockMode == LockMode.NONE) {
				// 加锁，防止缓存穿透
				String lockKey = EntityLockKeyGenerator.getInstance().generate(entityType, cacheName);
				lock = LockManager.getLock(lockKey);
				lock.lock();
				CacheCollection<T> collection = cacheManager.getCollection(entityType, cacheName);
				if (collection != null) {
					return collection.getList();
				}
			}
			// 进行查询
			List<T> list = sqlMapper.select(entityType, condition, orderBy, parameter, lockMode);
			for (T entity : list) {
				this.setPersient((HibatisEntity) entity);
			}
			// 判断是否使用缓存
			if (criterion.isCacheable()) {
				CacheCollection<T> collection = new CacheCollection<T>(list, -1);
				cacheManager.putCollection(entityType, cacheName, collection);
			}
			return list;
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}
	}

	@Override
	public <T> List<T> findIds(Criterion criterion, Class<?> resultType) {
		// 查询条件、参数
		Class<?> entityType = criterion.getRoot().getEntityType();
		CriterionStatement statement = new CriterionStatement(criterion);
		String condition = statement.getCondition();
		Object parameter = statement.getParameters();
		String orderBy = statement.getOrderBy();
		return sqlMapper.selectId(entityType, condition, orderBy, parameter, resultType);
	}

	@Override
	public <T> Page<T> paging(Criterion criterion) {
		Root root = criterion.getRoot();
		Class<T> entityType = (Class<T>) root.getEntityType();
		// 分页参数
		int firstResult = criterion.getFirstResult();
		if (firstResult < 0) {
			throw new IllegalArgumentException("查询条件未设置firstResult。");
		}
		int maxResults = criterion.getMaxResults();
		if (maxResults <= 0) {
			throw new IllegalArgumentException("查询条件未设置maxResult。");
		}
		// 查询条件、参数
		CriterionStatement statement = new CriterionStatement(criterion);
		String condition = statement.getCondition();
		Object parameter = statement.getParameters();
		String orderBy = statement.getOrderBy();
		String cacheName = statement.getCacheName();
		LockMode lockMode = criterion.getLockMode();
		if (lockMode == LockMode.READ) {
			throw new HibatisException("当前查询暂不支持锁模式为READ");
		}
		boolean cacheable = criterion.isCacheable();
		if (cacheable && lockMode == LockMode.NONE) {
			CacheCollection<T> collection = cacheManager.getCollection(entityType, cacheName);
			if (collection != null) {
				long total = collection.getTotal();
				List<T> list = collection.getList();
				return new PageImpl<T>(firstResult, maxResults, total, list);
			}
		}
		Lock lock = null;
		try {
			// 判断是否使用缓存
			if (cacheable && lockMode == LockMode.NONE) {
				// 加锁，防止缓存穿透
				String lockKey = EntityLockKeyGenerator.getInstance().generate(entityType, cacheName);
				lock = LockManager.getLock(lockKey);
				lock.lock();
				CacheCollection<T> collection = cacheManager.getCollection(entityType, cacheName);
				if (collection != null) {
					long total = collection.getTotal();
					List<T> list = collection.getList();
					return new PageImpl<T>(firstResult, maxResults, total, list);
				}
			}
			long total = sqlMapper.count(entityType, condition, parameter); // 统计总记录数
			List<T> list = sqlMapper.paging(entityType, condition, orderBy, parameter, lockMode);
			for (T entity : list) {
				this.setPersient((HibatisEntity) entity);
			}
			// 判断是否使用缓存
			if (cacheable) {
				CacheCollection<T> collection = new CacheCollection<T>(list, total);
				cacheManager.putCollection(entityType, cacheName, collection);
			}
			// 返回结果
			return new PageImpl<T>(firstResult, maxResults, total, list);
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}
	}

	@Override
	public <R> List<R> aggregate(Aggregate aggregate, Class<R> resultType) {
		Class<?> entityType = aggregate.getRoot().getEntityType();
		AggregateStatement statement = new AggregateStatement(aggregate);
		String conditions = statement.getCondition();
		Object parameter = statement.getParameters();
		String orderBy = statement.getOrderBy();
		String select = statement.getSelect();
		String groupBy = statement.getGroupBy();
		return sqlMapper.select(entityType, select, conditions, groupBy, orderBy, parameter, resultType);
	}

	@Override
	public <T> T insert(Object obj) {
		// 验证实体数据
		validate(obj);
		// 转换Hibatis实体类型
		HibatisEntity entity = EntityEnhancer.enhance(obj);
		if (EntityEnhancer.getState(entity) != EntityState.Transient) {
			throw new HibatisException("The entity object is not transient");
		}
		Class<?> entityType = EntityEnhancer.getEntityType(entity);
		// 新增到数据库
		sqlMapper.insert(entityType, entity);
		// 设置为持久对象
		this.setPersient(entity);
		// 同步自增主键到临时对象
		EntityMetadata metadata = EntityMetadata.get(entityType);
		List<IdProperty> properties = metadata.getIdProperties();
		for (IdProperty idProperty : properties) {
			Object value = idProperty.getValue(entity);
			idProperty.setValue(obj, value);
		}
		// 同步关联属性
		for (AssociationProperty association : metadata.getAssociations()) {
			Object value = LazyEntityEnhancer.getEnhancer(association.getReferenceType()).newInstance(entity,
					association);
			association.setValue(obj, value);
		}
		for (CollectionProperty collection : metadata.getCollections()) {
			Object value = new LazyEntityList<Object>(entity, collection);
			collection.setValue(obj, value);
		}
		// 判断是否使用缓存
		if (cacheManager.cacheable(entityType)) {
			cacheManager.put(entity); // 更新缓存
			cacheManager.cleanCollection(entityType);// 清空所有的集合缓存
		}
		return (T) entity;
	}

	@Override
	public int update(Object obj) {
		// 验证实体数据
		validate(obj);
		// 转换Hibatis实体类型
		Class<?> entityType = EntityEnhancer.getEntityType(obj);
		HibatisEntity entity;
		if (obj instanceof HibatisEntity) {
			entity = (HibatisEntity) obj;
		} else {
			// 如果是临时实体, 从上下文中加载实体对象
			Root root = Root.get(obj.getClass());
			EntityID id = root.toId(obj);
			entity = (HibatisEntity) findById(entityType, id, LockMode.NONE);
			if (entity == null) {
				throw new HibatisException("The entity object no exists");
			}
			// 把临时对象的属性复制到持久对象
			EntityMetadata metadata = root.getMetadata();
			for (ColumnProperty property : metadata.getUpdateProperties()) {
				Object value = property.getValue(obj);
				property.setValue(entity, value);
			}
			for (ColumnProperty property : metadata.getIdProperties()) {
				Object value = property.getValue(obj);
				property.setValue(entity, value);
			}
		}
		// 非持久状态
		if (EntityEnhancer.getState(entity) != EntityState.Persient) {
			throw new HibatisException("The entity object status is not persient");
		}
		// 获取已改变的属性
		String[] propertyChanges = EntityEnhancer.getPropertyChanges(entity);
		if (propertyChanges.length == 0) {
			return 0;
		}
		String condition = ConditionBuilder.byId(entityType);
		int effets = sqlMapper.update(entityType, propertyChanges, condition, entity);
		// 判断是否使用缓存
		if (cacheManager.cacheable(entityType)) {
			cacheManager.merge(entity, propertyChanges); // 更新缓存
			cacheManager.cleanCollection(entityType);// 清空所有的集合缓存
		}
		// 丢弃已记录修改的属性
		EntityEnhancer.discardPropertyChanges(entity);
		return effets;
	}

	@Override
	public <T> T save(Object obj) {
		if (obj instanceof HibatisEntity) {
			this.update(obj);
			return (T) obj;
		} else {
			return this.insert(obj);
		}
	}

	@Override
	public int delete(Object obj) {
		int effects;
		Class<?> entityType = EntityEnhancer.getEntityType(obj);
		if (obj instanceof HibatisEntity) {
			HibatisEntity entity = (HibatisEntity) obj;
			String condition = ConditionBuilder.byId(entityType);
			effects = sqlMapper.delete(entityType, condition, entity);
			// 更新为游离状态
			EntityEnhancer.setState(entity, EntityState.Detached);
			// 判断是否使用缓存
			if (cacheManager.cacheable(entityType)) {
				cacheManager.remove(entity); // 移除对象缓存
			}
		} else {
			Root root = Root.get(obj.getClass());
			EntityID id = root.toId(obj);
			String condition = ConditionBuilder.byId(entityType);
			effects = sqlMapper.delete(entityType, condition, id);
			// 判断是否使用缓存
			if (cacheManager.cacheable(entityType)) {
				cacheManager.remove(entityType, id); // 移除对象缓存
				cacheManager.cleanCollection(entityType);// 清空所有的集合缓存
			}
		}
		return effects;
	}

	@Override
	public int deletebyId(Class<?> entityType, EntityID id) {
		String condition = ConditionBuilder.byId(entityType);
		int effects = sqlMapper.delete(entityType, condition, id);
		// 判断是否使用缓存
		if (cacheManager.cacheable(entityType)) {
			cacheManager.remove(entityType, id); // 移除对象缓存
			cacheManager.cleanCollection(entityType);// 清空所有的集合缓存
		}
		return effects;
	}

	@Override
	public int delete(Criterion criterion) {
		int effects = 0;
		Root root = criterion.getRoot();
		Class<?> entityType = root.getEntityType();
		// 判断是否使用缓存
		if (cacheManager.cacheable(entityType)) {
			String condition = ConditionBuilder.byId(entityType);
			for (EntityID id : selectId(criterion)) {
				effects += sqlMapper.delete(entityType, condition, id);
				cacheManager.remove(entityType, id);
			}
			cacheManager.cleanCollection(entityType);// 清空所有的集合缓存
		} else {
			CriterionStatement statement = new CriterionStatement(criterion);
			String condition = statement.getCondition();
			Object parameter = statement.getParameters();
			effects = sqlMapper.delete(entityType, condition, parameter);
		}
		return effects;
	}

	@Override
	public <T> int insert(Class<T> entityType, String statementId, T parameter) {
		int effects = sqlMapper.insert(statementId, parameter);
		// 判断是否使用缓存
		if (entityType != null && cacheManager.cacheable(entityType)) {
			cacheManager.cleanCollection(entityType); // 清除集合缓存
		}
		return effects;
	}

	@Override
	public <T> int update(Class<T> entityType, String statementId, Object parameter) {
		int effects = sqlMapper.update(statementId, parameter);
		// 判断是否使用缓存
		if (entityType != null && cacheManager.cacheable(entityType)) {
			cacheManager.cleanCollection(entityType); // 清除集合缓存
		}
		return effects;
	}

	@Override
	public <T> int delete(Class<T> entityType, String statementId, Object parameter) {
		int effects = sqlMapper.delete(statementId, parameter);
		// 判断是否使用缓存
		if (entityType != null && cacheManager.cacheable(entityType)) {
			cacheManager.cleanCollection(entityType); // 清除集合缓存
		}
		return effects;
	}

	///////////////////////////// 私有方法 ////////////////////////////////

	private List<EntityID> selectId(Criterion criterion) {
		Class<?> entityType = criterion.getRoot().getEntityType();
		CriterionStatement statement = new CriterionStatement(criterion);
		String condition = statement.getCondition();
		Object parameter = statement.getParameters();
		String orderBy = statement.getOrderBy();
		return sqlMapper.selectId(entityType, condition, orderBy, parameter, Map.class);
	}

	private void validate(Object entity) {
		// 验证信息
		Set<ConstraintViolation<Object>> constraints = validator.validate(entity);
		if (constraints.size() > 0) {
			StringBuilder errors = new StringBuilder();
			for (ConstraintViolation<Object> constraint : constraints) {
				errors.append(constraint.getPropertyPath()).append(constraint.getMessage());
				errors.append(System.lineSeparator());
			}
			throw new HibatisValidateException(errors.toString());
		}
	}

	private void setPersient(HibatisEntity entity) {
		EntityEnhancer.setState(entity, EntityState.Persient);
		EntityMetadata metadata = EntityEnhancer.getEntityMetadata(entity);
		List<AssociationProperty> associations = metadata.getAssociations();
		for (AssociationProperty association : associations) {
			Object value = association.getFieldValue(entity);
			if (value != null) {
				setPersient((HibatisEntity) value);
			}
		}
		// 把关联对象也设置持久状态
		List<CollectionProperty> collections = metadata.getCollections();
		for (CollectionProperty collection : collections) {
			// 不使用缓存并且非懒加载则立即获取（触发加载）
			if (!collection.cacheable() && !collection.isLazy()) {
				collection.getValue(entity);
			}
		}
	}
}
