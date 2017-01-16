/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.ibatis;

import java.util.Arrays;

import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.session.Configuration;

import com.yaoa.hibatis.lock.LockMode;
import com.yaoa.hibatis.metadata.EntityMetadata;
import com.yaoa.hibatis.metadata.IdProperty;
import com.yaoa.hibatis.util.ArrayUtils;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月20日
 */
class MappedStatementBuilder {

	private Configuration configuration;

	private ResultMapBuilder resultMapBuilder;

	public MappedStatementBuilder(Configuration configuration) {
		this.configuration = configuration;
		this.resultMapBuilder = new ResultMapBuilder(configuration);
	}

	// 生成命令id
	private String buildStatementId(Class<?> entityType, String name) {
		return "hibatis:" + entityType.getName() + "." + name;
	}

	// 生成查询命令
	public String select(Class<?> entityType, String condition, String orderBy, LockMode lockMode) {
		String statementId = this.buildStatementId(entityType, "select@" + lockMode + "|" + condition + "|" + orderBy);
		if (configuration.hasStatement(statementId)) {
			return statementId;
		}
		ResultMap resultMap = resultMapBuilder.getDefault(entityType);
		String sql = new SqlBuilder(entityType).select(condition, orderBy, lockMode);
		SqlSource sqlSource = new RawSqlSource(configuration, sql, null);
		MappedStatement statement = new MappedStatement.Builder(configuration, statementId, sqlSource,
				SqlCommandType.SELECT).resultMaps(Arrays.asList(resultMap)).build();
		StatementResultMapHelper.addMappedStatement(configuration, statement);
		return statementId;
	}

	// 生成查询命令
	public String count(Class<?> entityType, String condition) {
		String statementId = this.buildStatementId(entityType, "count@" + condition);
		if (configuration.hasStatement(statementId)) {
			return statementId;
		}
		ResultMap resultMap = resultMapBuilder.count(entityType);
		String sql = new SqlBuilder(entityType).count(condition);
		SqlSource sqlSource = new RawSqlSource(configuration, sql, null);
		MappedStatement statement = new MappedStatement.Builder(configuration, statementId, sqlSource,
				SqlCommandType.SELECT).resultMaps(Arrays.asList(resultMap)).build();
		StatementResultMapHelper.addMappedStatement(configuration, statement);
		return statementId;
	}

	// 生成查询命令
	public String paging(Class<?> entityType, String condition, String orderBy, LockMode lockMode) {
		String statementId = this.buildStatementId(entityType, "paging@" + lockMode + "|" + condition + "|" + orderBy);
		if (configuration.hasStatement(statementId)) {
			return statementId;
		}
		ResultMap resultMap = resultMapBuilder.getDefault(entityType);
		String sql = new SqlBuilder(entityType).paging(condition, orderBy, lockMode);
		SqlSource sqlSource = new RawSqlSource(configuration, sql, null);
		MappedStatement statement = new MappedStatement.Builder(configuration, statementId, sqlSource,
				SqlCommandType.SELECT).resultMaps(Arrays.asList(resultMap)).build();
		StatementResultMapHelper.addMappedStatement(configuration, statement);
		return statementId;
	}

	// 生成查询主键命令
	public String selectId(Class<?> entityType, String condition, String orderBy , Class<?> resultType) {
		String statementId = this.buildStatementId(entityType, "selectId@" + resultType.getName() + "|" + condition + "|" + orderBy);
		if (configuration.hasStatement(statementId)) {
			return statementId;
		}
		ResultMap resultMap = resultMapBuilder.getId(entityType , resultType);
		String sql = new SqlBuilder(entityType).selectId(condition, orderBy);
		SqlSource sqlSource = new RawSqlSource(configuration, sql, null);
		MappedStatement statement = new MappedStatement.Builder(configuration, statementId, sqlSource,
				SqlCommandType.SELECT).resultMaps(Arrays.asList(resultMap)).build();
		StatementResultMapHelper.addMappedStatement(configuration, statement);
		return statementId;
	}

	public String selectUnionAll(Class<?> entityType, String condition, String orderBy, LockMode lockMode) {
		String statementId = this.buildStatementId(entityType, "selectUnionAll@" + condition + "|" + orderBy);
		if (configuration.hasStatement(statementId)) {
			return statementId;
		}
		ResultMap resultMap = resultMapBuilder.getDefault(entityType);
		SqlNode rootSqlNode = new SqlBuilder(entityType).selectUnionAll(configuration, condition, orderBy, lockMode);
		DynamicSqlSource sqlSource = new DynamicSqlSource(configuration, rootSqlNode);
		MappedStatement statement = new MappedStatement.Builder(configuration, statementId, sqlSource,
				SqlCommandType.SELECT).resultMaps(Arrays.asList(resultMap)).build();
		StatementResultMapHelper.addMappedStatement(configuration, statement);
		return statementId;
	}

	public String select(Class<?> entityType, String columns, String conditions, String groupBy, String orderBy,
			Class<?> resultType) {
		String statementId = this.buildStatementId(entityType,
				"aggregate@" + resultType.getName() + "|" + columns + "|" + conditions + "|" + groupBy + "|" + orderBy);
		if (configuration.hasStatement(statementId)) {
			return statementId;
		}
		String sql = new SqlBuilder(entityType).select(columns, conditions, groupBy, orderBy);
		ResultMap resultMap = resultMapBuilder.autoMapping(resultType);
		SqlSource sqlSource = new RawSqlSource(configuration, sql, null);
		MappedStatement statement = new MappedStatement.Builder(configuration, statementId, sqlSource,
				SqlCommandType.SELECT).resultMaps(Arrays.asList(resultMap)).build();
		StatementResultMapHelper.addMappedStatement(configuration, statement);
		return statementId;
	}

	public String insert(Class<?> entityType) {
		// 命令名称
		String statementId = buildStatementId(entityType, "insert");
		if (configuration.hasStatement(statementId)) {
			return statementId;
		}
		// 创建命令
		String sql = new SqlBuilder(entityType).insert();
		SqlSource sqlSource = new RawSqlSource(configuration, sql, null);
		MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, statementId, sqlSource,
				SqlCommandType.INSERT);
		// 判断是否需要自动生成主键
		EntityMetadata metadata = EntityMetadata.get(entityType);
		for (IdProperty idProperty : metadata.getIdProperties()) {
			KeyGenerator keyGenerator = idProperty.getKeyGenerator();
			if (keyGenerator == null) {
				continue;
			}
			String propertyName = idProperty.getName();
			String columnName = idProperty.getColumnName();
			statementBuilder.keyProperty(propertyName).keyColumn(columnName).keyGenerator(keyGenerator);
		}
		MappedStatement statement = statementBuilder.build();
		StatementResultMapHelper.addMappedStatement(configuration, statement);
		return statementId;
	}

	public String update(Class<?> entityType, String[] fields, String condition) {
		// 命令名称
		String statementId = buildStatementId(entityType, "update@" + ArrayUtils.join(fields, ",") + ">" + condition);
		if (configuration.hasStatement(statementId)) {
			return statementId;
		}
		String sql = new SqlBuilder(entityType).update(fields, condition);
		// 创建命令
		SqlSource sqlSource = new RawSqlSource(configuration, sql, null);
		MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, statementId, sqlSource,
				SqlCommandType.UPDATE);
		MappedStatement statement = statementBuilder.build();
		StatementResultMapHelper.addMappedStatement(configuration, statement);
		return statementId;
	}

	public static void main(String[] args) {
		System.out.println(new String[] { "a" });
	}

	public String delete(Class<?> entityType, String condition) {
		// 命令名称
		String statementId = buildStatementId(entityType, "delete@" + condition);
		if (configuration.hasStatement(statementId)) {
			return statementId;
		}
		String sql = new SqlBuilder(entityType).delete(condition);
		// 创建命令
		SqlSource sqlSource = new RawSqlSource(configuration, sql, null);
		MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, statementId, sqlSource,
				SqlCommandType.DELETE);
		MappedStatement statement = statementBuilder.build();
		StatementResultMapHelper.addMappedStatement(configuration, statement);
		return statementId;
	}
}
