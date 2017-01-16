/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cglib.beans.BeanCopier;

import com.yaoa.hibatis.cache.EntityCacheManager;
import com.yaoa.hibatis.entity.impl.ProxyClassGenerator;
import com.yaoa.hibatis.exception.HibatisException;
import com.yaoa.hibatis.lock.LockMode;
import com.yaoa.hibatis.metadata.AssociationProperty;
import com.yaoa.hibatis.metadata.CollectionProperty;
import com.yaoa.hibatis.metadata.ColumnProperty;
import com.yaoa.hibatis.metadata.EntityID;
import com.yaoa.hibatis.metadata.EntityMetadata;
import com.yaoa.hibatis.metadata.Property;
import com.yaoa.hibatis.query.Criterion;
import com.yaoa.hibatis.query.build.CriterionBuilder;
import com.yaoa.hibatis.query.build.CriterionBuilder.CriterionBuilderLink;

/**
 * 实体增强器
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年8月5日
 */
@SuppressWarnings("unchecked")
public class EntityEnhancer {
	
	private Class<?> entityType;
	
	private Class<?> proxyClass;
	
	private EntityMetadata metadata;
	
	private BeanCopier beanCopier;
	
	private final static Log logger = LogFactory.getLog(EntityEnhancer.class);

	private EntityEnhancer(Class<?> entityType) {
		this.entityType = entityType;
		this.metadata = EntityMetadata.get(entityType);
		this.beanCopier = BeanCopier.create(entityType, entityType, false);
	}
	
	public void copy(Object source, Object target ){
		this.beanCopier.copy(source , target , null);;
	}

	public Class<?> getProxyClass() {
		if (proxyClass == null) {
			generateProxyClass();
			// 生成关联属性的代理类
			for (AssociationProperty association : metadata.getAssociations()) {
				Class<?> referenceType =  association.getReferenceType();
				if(referenceType != entityType){
					EntityEnhancer.getEnhancer(referenceType).getProxyClass();
				}
			}
			// 生成(关联)集合属性的代理类
			for (CollectionProperty collection : metadata.getCollections()) {
				Class<?> referenceType =  collection.getReferenceType();
				if(referenceType != entityType){
					EntityEnhancer.getEnhancer(referenceType).getProxyClass();
				}
			}
		}
		return proxyClass;
	}

	private synchronized void generateProxyClass() {
		if (proxyClass != null) {
			return;
		}
		try {
			proxyClass = new ProxyClassGenerator(entityType , true).generateClass();
			logger.info("Build hibatis entity type:" + proxyClass);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public EntityMetadata getEntityMetadata() {
		return metadata;
	}

	public static String getVersion(HibatisEntity entity) {
		try {
			Field field = entity.getClass().getDeclaredField("$version");
			field.setAccessible(true);
			return (String) field.get(entity);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static EntityState getState(HibatisEntity entity) {
		try {
			Field field = entity.getClass().getDeclaredField("$state");
			field.setAccessible(true);
			return (EntityState) field.get(entity);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void setState(HibatisEntity entity , EntityState state) {
		try {
			Field field = entity.getClass().getDeclaredField("$state");
			field.setAccessible(true);
			field.set(entity , state);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Class<?> getEntityType(Object obj) {
		if(obj instanceof HibatisEntity){
			try {
				HibatisEntity entity = (HibatisEntity)obj;
				Field field = entity.getClass().getDeclaredField("$type");
				field.setAccessible(true);
				return (Class<?>) field.get(entity);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}else{
			Class<?> type = obj.getClass();
			if(!EntityMetadata.isEntity(type)){
				throw new HibatisException(type.getName() + " is not entity");
			} 
			return type;
		}
	}
	
	public static EntityMetadata getEntityMetadata(Object obj) {
		return EntityMetadata.get(getEntityType(obj));
	}
	
	public static String[] getPropertyChanges(HibatisEntity entity) {
		List<String> propertyChanges = getPropertyChangesList(entity);
		String[] array = new String[propertyChanges.size()];
		propertyChanges.toArray(array);
		return array;
	}
	
	public static HibatisEntity enhance(Object obj) {
		try {
			Class<?> entityType = obj.getClass();
			EntityEnhancer enhancer = EntityEnhancer.getEnhancer(entityType);
			Class<?> proxyClass = enhancer.getProxyClass();
			HibatisEntity entity = (HibatisEntity) proxyClass.newInstance();
			// 复制属性
			Collection<ColumnProperty> properties = enhancer.metadata.getSelectProperties();
			for (ColumnProperty property : properties) {
				Object value = property.getValue(obj);
				property.setValue(entity, value);
			}
			return entity;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	// 调用父类方法
	private static Object invokeSupperMethod(Object obj, String name, Class<?>[] parameterTypes , Object[] parameters) {
		try {
			Method method = obj.getClass().getDeclaredMethod("$super_" + name, parameterTypes);
			method.setAccessible(true);
			return method.invoke(obj, parameters);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// Get方法拦截
	public static Object onPropertyGetterInvoke(String methodName , String propertyName , HibatisEntity entity) {
		Object value = invokeSupperMethod(entity, methodName , new Class[0] , new Object[0]);
		if(value != null){
			return value;
		}
		//判断状态为已持久化
		if(EntityEnhancer.getState(entity) == EntityState.Persient){
			EntityMetadata metadata = getEntityMetadata(entity);
			Property property = metadata.findProperty(propertyName);
			// 如果为关联对象
			if(property.getClass() == AssociationProperty.class){
				AssociationProperty association = (AssociationProperty) property;
				return loadAssociation(association , entity);
			}
			// 如果为集合对象
			if(property.getClass() == CollectionProperty.class){
				CollectionProperty collection = (CollectionProperty) property;
				return loadCollection(collection , entity);
			}
		}
		return value;
	}

	// Set方法拦截
	public static void onPropertySetterInvoke(String methodName , String propertyName, Class<?> valueType, HibatisEntity entity , Object value) {
		// 当前状态为持久对象
		if(getState(entity) == EntityState.Persient){
			EntityMetadata metadata = EntityEnhancer.getEntityMetadata(entity);
			Property property = metadata.findProperty(propertyName);
			Object oldValue = property.getValue(entity);
			boolean isEqual;
			if(oldValue != null && oldValue.equals(value)){
				isEqual = true;
			}else {
				isEqual = oldValue == null && value == null;
			}
			if(!isEqual){
				List<String> propertyChanges = getPropertyChangesList(entity);
				if(!propertyChanges.contains(propertyName)){
					propertyChanges.add(propertyName);
				}
			}
		}
		invokeSupperMethod(entity, methodName,  new Class[]{ valueType } , new Object[]{ value });
	}
	
	// 放弃改变的属性
	public static void discardPropertyChanges(HibatisEntity entity) {
		List<String> propertyChanges = getPropertyChangesList(entity);
		propertyChanges.clear();
	}
	
	/////////////////////////////// 私有方法 ////////////////////////////////////
	

	private static List<String> getPropertyChangesList(HibatisEntity entity) {
		try {
			Field field = entity.getClass().getDeclaredField("$propertyChanges");
			field.setAccessible(true);
			List<String> propertyChanges =  (List<String>) field.get(entity);
			return propertyChanges;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Object loadAssociation(AssociationProperty association , Object entity){
		EntityManager em = EntityManager.get();
		//如果引用的其他表的主键
		if(association.isPKReferences()){
			Class<?> referenceType = association.getReferenceType();
			EntityMetadata refMetadata = EntityMetadata.get(referenceType);
			EntityID id = new EntityID(refMetadata);
			List<ColumnProperty> properties = association.getProperties();
			List<ColumnProperty> references = association.getReferences();
			for (int i = 0; i < properties.size() ; i++) {
				ColumnProperty thisProperty = properties.get(i);
				ColumnProperty refProperty = references.get(i);
				String name = refProperty.getName();
				Object value = thisProperty.getValue(entity);
				id.put(name, value);
			}
			return em.findById(referenceType, id , LockMode.NONE);
		}else{
			Class<?> referenceType = association.getReferenceType();
			CriterionBuilderLink builder = CriterionBuilder.create(referenceType).cache();
			// 通过关联字段查询
			List<ColumnProperty> properties = association.getProperties();
			List<ColumnProperty> references = association.getReferences();
			for (int i = 0; i < references.size(); i++) {
				ColumnProperty refColumn = references.get(i);
				ColumnProperty thisColumn = properties.get(i);
				String refPropName = refColumn.getName();
				Object thisPropValues = thisColumn.getValue(entity);
				builder.equal(refPropName, thisPropValues);
			}
			Criterion criterion = builder.build();
			return em.findOne(criterion);
		}
	}
	
	public static Object loadCollection(CollectionProperty collection , Object entity){
		Class<?> referenceType = collection.getReferenceType();
		// 通过关联字段查询
		List<ColumnProperty> properties = collection.getProperties();
		List<ColumnProperty> references = collection.getReferences();
		boolean cacheable = collection.cacheable();
		EntityManager em = EntityManager.get();
		CriterionBuilderLink builder = CriterionBuilder.create(referenceType).cache(cacheable);
		for (int i = 0; i < references.size(); i++) {
			ColumnProperty refProperty = references.get(i);
			ColumnProperty thisProperty = properties.get(i);
			String refPropName = refProperty.getName();
			Object thisPropValue = thisProperty.getValue(entity);
			if(thisPropValue == null){
				return null;
			}
			if(collection.isIn()){
				builder.in(refPropName, thisPropValue.toString().split(","));
			}else{
				builder.equal(refPropName, thisPropValue);
			}
		}
		builder.orderBy(collection.getSorts());
		Criterion criterion = builder.build();
		Object result = em.find(criterion);
		//如果未启用缓存
		if(!collection.cacheable()){
			// 赋值,避免重复获取
			collection.setValue(entity, result);
			// 需要刷新缓存
			EntityMetadata metadata = getEntityMetadata(entity);
			if(metadata.cacheable()){
				EntityCacheManager cacheManager = EntityCacheManager.getInstance();
				cacheManager.put((HibatisEntity)entity);
			}
		}
		return result;
	}
	/////////////////////////////// 实例管理 ///////////////////////////////////
	
	private static ConcurrentHashMap<Class<?>, EntityEnhancer> instancePool;

	static {
		instancePool = new ConcurrentHashMap<Class<?>, EntityEnhancer>();
	}

	public static EntityEnhancer getEnhancer(Class<?> entityType) {
		EntityEnhancer enhancer = instancePool.get(entityType);
		if (enhancer != null) {
			return enhancer;
		}
		enhancer = createEntityEnhancer(entityType);
		return enhancer;
	}

	private static synchronized EntityEnhancer createEntityEnhancer(Class<?> entityClass) {
		EntityEnhancer enhancer = instancePool.get(entityClass);
		if (enhancer != null) {
			return enhancer;
		}
		enhancer = new EntityEnhancer(entityClass);
		instancePool.put(entityClass, enhancer);
		return enhancer;
	}

}
