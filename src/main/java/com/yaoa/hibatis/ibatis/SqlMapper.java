/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.ibatis;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;

import com.yaoa.hibatis.lock.LockMode;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月20日
 */
public class SqlMapper {

	private SqlSession sqlSession;
	
	private MappedStatementBuilder statementBuilder;
	
	public SqlMapper(SqlSessionFactory sqlSessionFactory) {
		this.sqlSession = new SqlSessionTemplate(sqlSessionFactory);
		this.statementBuilder = new MappedStatementBuilder(sqlSession.getConfiguration());
	}

	public <T> List<T> select(Class<?> entityType , String condition, String orderBy, Object parameter , LockMode lockMode){
		String statement = statementBuilder.select(entityType, condition, orderBy , lockMode);
		return sqlSession.selectList(statement, parameter);
	}
	
	public <R> List<R> selectId(Class<?> entityType , String condition, String orderBy, Object parameter, Class<?> resultType){
		String statement = statementBuilder.selectId(entityType, condition, orderBy , resultType);
		return sqlSession.selectList(statement, parameter);
	}
	
	public <T> T selectOne(Class<?> entityType , String condition, String orderBy, Object parameter, LockMode lockMode){
		String statement = statementBuilder.select(entityType, condition, orderBy , lockMode);
		return sqlSession.selectOne(statement, parameter);
	}
	
	public <T> List<T> selectUnionAll(Class<?> entityType , String condition, String orderBy, List<?> parameters, LockMode lockMode){
		String statement = statementBuilder.selectUnionAll(entityType, condition, orderBy , lockMode);
		return sqlSession.selectList(statement, parameters);
	}
	
	public <T> List<T> select(Class<?> entityType , String columns, String conditions,String groupBy, String orderBy, Object parameter , Class<T> resultType) {
		String statement = statementBuilder.select(entityType, columns, conditions, groupBy, orderBy, resultType);
		return sqlSession.selectList(statement, parameter);
	}
	
	public long count(Class<?> entityType , String condition , Object parameter){
		String statement = statementBuilder.count(entityType, condition);
		return sqlSession.selectOne(statement, parameter);
	}
	
	public <T> List<T> paging(Class<?> entityType , String condition, String orderBy, Object parameter , LockMode lockMode) {
		String statement = statementBuilder.paging(entityType, condition, orderBy , lockMode);
		return sqlSession.selectList(statement, parameter);
	}
	
	public int insert(Class<?> entityType ,  Object parameter){
		String statement = statementBuilder.insert(entityType);
		return sqlSession.insert(statement, parameter);
	}

	public int update(Class<?> entityType  , String[] fields , String condition, Object parameter) {
		String statement = statementBuilder.update(entityType , fields , condition);
		return sqlSession.update(statement, parameter);
	}
	
	public int delete(Class<?> entityType  , String condition, Object parameter) {
		String statement = statementBuilder.delete(entityType , condition);
		return sqlSession.delete(statement, parameter);
	}
	
	public <R> R selectOne(String statementId, Object parameter) {
		return sqlSession.selectOne(statementId, parameter);
	}

	public <R> List<R> selectList(String statementId, Object parameter) {
		return sqlSession.selectList(statementId, parameter);
	}

	public <T> int insert(String statementId, T entity){
		int effects = sqlSession.insert(statementId, entity);
		return effects;
	}

	public <T> int update(String statementId, Object parameter) {
		return sqlSession.update(statementId, parameter);
	}
	
	public <T> int delete(String statementId, Object parameter) {
		return sqlSession.delete(statementId, parameter);
	}
	
}
