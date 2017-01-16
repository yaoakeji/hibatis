/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;

import com.yaoa.hibatis.entity.EntityManager;
import com.yaoa.hibatis.ibatis.SqlMapper;
import com.yaoa.hibatis.lock.LockMode;
import com.yaoa.hibatis.metadata.EntityID;
import com.yaoa.hibatis.metadata.Root;
import com.yaoa.hibatis.query.Criterion;
import com.yaoa.hibatis.query.Page;
import com.yaoa.hibatis.query.Sort;
import com.yaoa.hibatis.query.aggregate.Aggregate;
import com.yaoa.hibatis.query.build.CriterionBuilder;

/**
 * 
 * 数据映射操作模版
 * 
 * @author kingsy.lin
 * @version 1.0 , 2016年10月20日
 */
public class SqlMapperTemplate {

	private EntityManager em;

	public SqlMapperTemplate(EntityManager em) {
		this.em = em;
	}

	public SqlMapperTemplate(SqlSessionFactory sqlSessionFactory) {
		this.em = EntityManager.initialize(sqlSessionFactory);
	}

	public <T> List<T> find(Criterion criterion) {
		return em.find(criterion);
	}

	public <R> List<R> findIds(Criterion criterion, Class<?> resultType) {
		return em.findIds(criterion, resultType);
	}

	public <T> List<T> findAll(Class<T> entityType, Sort... sorts) {
		Criterion criterion = CriterionBuilder.create(entityType).orderBy(sorts).cache().build();
		return find(criterion);
	}

	public <T> T findById(Class<T> entityType, Serializable id) {
		return this.findById(entityType, id, LockMode.NONE);
	}

	public <T> T findById(Class<T> entityType, Serializable id, LockMode lockMode) {
		Root root = Root.get(entityType);
		EntityID idMap = root.toId(id);
		return em.findById(entityType, idMap, lockMode);
	}

	public <T> void refresh(T entity, LockMode lockMode) {
		em.refresh(entity, lockMode);
	}

	public <T> long count(Criterion criterion) {
		return em.count(criterion);
	}

	public <T> Page<T> paging(Criterion criterion) {
		return em.paging(criterion);
	}

	public <T> T aggregate(Aggregate aggregate, Class<T> resultType) {
		List<T> list = em.aggregate(aggregate, resultType);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public <T> List<T> aggregateList(Aggregate aggregate, Class<T> resultType) {
		return em.aggregate(aggregate, resultType);
	}

	public <T> T insert(Object entity) {
		return em.insert(entity);
	}

	public int update(Object entity) {
		return em.update(entity);
	}

	public <T> T save(Object entity) {
		return em.save(entity);
	}

	public int delete(Object entity) {
		return em.delete(entity);
	}

	public int deletebyId(Class<?> entityType, Serializable id) {
		EntityID idMap = Root.get(entityType).toId(id);
		return em.deletebyId(entityType, idMap);
	}

	public int delete(Criterion criterion) {
		return em.delete(criterion);
	}

	public boolean existsById(Class<?> entityType, Serializable id) {
		EntityID idMap = Root.get(entityType).toId(id);
		return em.findById(entityType, idMap, LockMode.NONE) != null;
	}

	public <T> List<T> findByIds(Class<T> entityType, Serializable[] ids) {
		return this.findByIds(entityType, ids, LockMode.NONE);
	}

	public <T> List<T> findByIds(Class<T> entityType, Serializable[] idArray, LockMode lockMode) {
		Root root = Root.get(entityType);
		List<EntityID> idList = new ArrayList<EntityID>(idArray.length);
		for (int i = 0; i < idArray.length; i++) {
			EntityID idMap = root.toId(idArray[i]);
			idList.add(idMap);
		}
		return em.findByIds(entityType, idList, lockMode);
	}

	//////////////////////////////// sqlMapper 命令操作 ////////////////////////////

	public <R> R selectOne(String statementId, Object parameter) {
		SqlMapper sqlMapper = em.getSqlMapper();
		return sqlMapper.selectOne(statementId, parameter);
	}

	public <R> List<R> selectList(String statementId, Object parameter) {
		SqlMapper sqlMapper = em.getSqlMapper();
		return sqlMapper.selectList(statementId, parameter);
	}

	public <T> int insert(Class<T> entityType, String statementId, T entity) {
		return em.insert(entityType, statementId, entity);
	}

	public <T> int update(Class<T> entityType, String statementId, Object parameter) {
		return em.update(entityType, statementId, parameter);
	}

	public <T> int delete(Class<T> entityType, String statementId, Object parameter) {
		return em.delete(entityType, statementId, parameter);
	}
}
