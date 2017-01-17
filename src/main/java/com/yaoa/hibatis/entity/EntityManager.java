/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.entity;

import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;

import com.yaoa.hibatis.entity.impl.EntityManagerImpl;
import com.yaoa.hibatis.ibatis.SqlMapper;
import com.yaoa.hibatis.lock.LockMode;
import com.yaoa.hibatis.metadata.EntityID;
import com.yaoa.hibatis.query.Criterion;
import com.yaoa.hibatis.query.Page;
import com.yaoa.hibatis.query.aggregate.Aggregate;

/**
 * 
 * 实体管理器
 * 
 * @author kingsy.lin
 * @version 1.0 , 2016年10月19日
 */
public abstract class EntityManager {
	
	protected SqlMapper sqlMapper;
	
	public EntityManager(SqlSessionFactory sqlSessionFactory){
		this.sqlMapper =  new SqlMapper(sqlSessionFactory);;
	}
	
	public SqlMapper getSqlMapper(){
		return sqlMapper;
	}

	/**
	 * 查询
	 * 
	 * @param criterion  查询条件
	 * @param <T> 类型
	 * @return 实体集合
	 */
	public abstract <T> List<T> find(Criterion criterion) ;
	
	/**
	 * 查询，返回一条数据
	 * @param criterion  查询条件
	 * @param <T> 类型
	 * @return 实体对象
	 */
	public abstract <T> T findOne(Criterion criterion) ;
	
	
	/**
	 * 获取主键
	 * @param criterion 查询条件
	 * @param resultType 结果类型
	 * @param <R> 类型
	 * @return 结果集
	 */
	public abstract <R> List<R> findIds(Criterion criterion, Class<?> resultType);
	
	/**
	 *  通过主键获取
	 * @param entityType 实体类型
	 * @param id 主键信息
	 * @param lockMode 锁模式
	 * @param <T> 类型
	 * @return 实体对象
	 */
	public abstract <T> T findById(Class<T> entityType , EntityID id , LockMode lockMode);
	
	/**
	 * 刷新对象
	 * @param entity 实体对象
	 * @param lockMode 锁模式
	 * @param <T> 类型
	 */
	public abstract <T> void refresh(T entity, LockMode lockMode) ;
	
	/**
	 * 通过多个主键获取
	 * @param entityType 实体类型
	 * @param idList 主键集合
	 * @param lockMode 锁模式
	 * @param <T> 类型
	 * @return 实体集合
	 */
	public abstract <T> List<T> findByIds(Class<T> entityType, List<EntityID> idList, LockMode lockMode);
	
	/**
	 * 统计数据记录
	 * @param criterion 查询条件
	 * @return 记录数
	 */
	public abstract long count(Criterion criterion);
	
	/**
	 * 分页
	 * @param criterion 查询条件
	 * @param <T> 类型
	 * @return 分页
	 */
	public abstract <T> Page<T> paging(Criterion criterion) ;
	
	
	/**
	 * 集合查询
	 * @param aggregate 聚合规则
	 * @param resultType 结果类型
	 * @param <R> 类型
	 * @return  实体集合
	 */
	public abstract <R> List<R> aggregate(Aggregate aggregate , Class<R> resultType);

	/**
	 * 新增
	 * @param obj 实体对象
	 * @param <T> 类型
	 * @return 实体对象
	 */
	public abstract <T> T insert(Object obj) ;

	/**
	 *  更新
	 * @param obj 实体对象
	 * @return 影响记录数
	 */
	public abstract int update(Object obj);
	

	/**
	 *  保存
	 * @param obj 实体对象
	 * @param <T> 类型
	 * @return 实体
	 */
	public abstract <T> T save(Object obj);
	
	/**
	 * 删除 
	 * @param obj 实体对象
	 * @return 影响记录数
	 */
	public abstract int delete(Object obj);
	
	/**
	 * 根据主键删除
	 * @param entityType 实体类型
	 * @param id 主键
	 * @return 影响行数
	 */
	public abstract int deletebyId(Class<?> entityType, EntityID id);
	

	/**
	 * 删除 
	 * @param criterion 条件
	 * @return 影响行数
	 */
	public abstract int delete(Criterion criterion);
	
	
	/**
	 * 插入数据
	 * @param entityType 实体类型
	 * @param statementId 命令id
	 * @param parameter 对象
	 * @param <T> 类型
	 * @return 影响记录数
	 */
	public abstract <T> int insert(Class<T> entityType , String statementId, T parameter);

	/**
	 * 插入数据
	 * @param entityType 实体类型
	 * @param statementId 命令id
	 * @param parameter 对象
	 * @param <T> 类型
	 * @return 实体对象
	 */
	public abstract <T> int update(Class<T> entityType , String statementId, Object parameter);
	
	/**
	 * 插入数据
	 * @param entityType 实体类型
	 * @param statementId 命令id
	 * @param parameter 对象
	 * @param <T> 类型
	 * @return 实体对象
	 */
	public abstract <T> int delete(Class<T> entityType , String statementId, Object parameter) ;
	
	
	///////////////////////////////////// 实例管理 ////////////////////////////////////////////

	private static EntityManager instance;
	
	public static EntityManager initialize(SqlSessionFactory sqlSessionFactory) {
		instance = new EntityManagerImpl(sqlSessionFactory);
		return instance;
	}

	public static EntityManager get() {
		return instance;
	}

}
