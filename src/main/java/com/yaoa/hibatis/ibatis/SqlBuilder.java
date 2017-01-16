/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.ibatis;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.scripting.xmltags.TextSqlNode;
import org.apache.ibatis.session.Configuration;
import org.springframework.util.StringUtils;

import com.yaoa.hibatis.annotation.FetchMode;
import com.yaoa.hibatis.lock.LockMode;
import com.yaoa.hibatis.metadata.AssociationProperty;
import com.yaoa.hibatis.metadata.ColumnProperty;
import com.yaoa.hibatis.metadata.EntityMetadata;
import com.yaoa.hibatis.metadata.IdProperty;
import com.yaoa.hibatis.metadata.Path;
import com.yaoa.hibatis.metadata.Root;
import com.yaoa.hibatis.util.ArrayUtils;

/**
 * 
 * SQL语句构建器
 * 
 * @author kingsy.lin
 * @version 1.0 , 2016年10月20日
 */
class SqlBuilder {

	private final static Pattern ARGUMENT_REGEX = Pattern.compile("#[{](\\w+)[}]");

	private EntityMetadata metadata;

	private Root root;

	public SqlBuilder(Class<?> entityType) {
		this.metadata = EntityMetadata.get(entityType);
		this.root = Root.get(entityType);
	}

	public String select(String conditions, String orderBy, LockMode lockMode) {
		SQL sql = select(conditions, orderBy);
		if (lockMode == LockMode.UPGRADE || lockMode == LockMode.UPGRADE_NOWAIT) {
			return sql + " FOR UPDATE";
		} else if (lockMode == LockMode.UPGRADE_NOWAIT) {
			return sql + " FOR UPDATE NOWAIT";
		} else {
			return sql.toString();
		}
	}

	public String paging(String conditions, String orderBy, LockMode lockMode) {
		SQL sql = select(conditions, orderBy);
		StringBuilder sqlSb = new StringBuilder();
		sqlSb.append(sql);
		sqlSb.append(" LIMIT #{firstResult},#{maxResults}");
		if (lockMode == LockMode.UPGRADE) {
			sqlSb.append(" FOR UPDATE");
		}else if (lockMode == LockMode.UPGRADE_NOWAIT) {
			sqlSb.append(" FOR UPDATE NOWAIT");
		}
		return sqlSb.toString();
	}

	private SQL select(String conditions, String orderBy) {
		String tableName = metadata.getTableName();
		SQL sql = new SQL().FROM(tableName + " _this");
		List<ColumnProperty> properties = metadata.getSelectProperties();
		for (ColumnProperty property : properties) {
			Path path = root.getPath(property);
			String column = path.getColumn() + " AS " + path.getMapping();
			sql.SELECT(column);
		}
		// 外连接其他表
		for (AssociationProperty association : metadata.getAssociations()) {
			// 非Join方式不处理
			if (association.getFetchMode() != FetchMode.JOIN) {
				continue;
			}
			// 是否使用懒加载
			if (association.isLazy()) {
				continue;
			}
			Class<?> referenceType = association.getReferenceType();
			EntityMetadata refMetadata = EntityMetadata.get(referenceType);
			// 判断时关联类是否使用缓存，是则忽略
			if (refMetadata.cacheable()) {
				continue;
			}
			for (ColumnProperty property : refMetadata.getSelectProperties()) {
				Path path = root.getPath(association, property);
				String column = path.getColumn() + " AS " + path.getMapping();
				sql.SELECT(column);
			}
		}
		// 加入查询条件
		where(sql, conditions);
		// 加入排序条件
		if (!StringUtils.isEmpty(orderBy)) {
			sql.ORDER_BY(orderBy);
		}
		return sql;
	}

	public String select(String columns, String conditions, String groupBy, String orderBy) {
		String tableName = metadata.getTableName();
		SQL sql = new SQL().FROM(tableName + " _this");
		sql.SELECT(columns);
		// 加入查询条件
		if (!StringUtils.isEmpty(conditions)) {
			where(sql, conditions);
		}
		// 加入分组依据
		if (!StringUtils.isEmpty(groupBy)) {
			sql.GROUP_BY(groupBy);
		}
		// 加入排序条件
		if (!StringUtils.isEmpty(orderBy)) {
			sql.ORDER_BY(orderBy);
		}
		return sql.toString();
	}

	public String count(String conditions) {
		String tableName = metadata.getTableName();
		SQL sql = new SQL().FROM(tableName + " _this");
		sql.SELECT("count(1)");
		// 加入查询条件
		where(sql, conditions);
		// 返回SQL语句
		return sql.toString();
	}

	public String selectId(String conditions, String orderBy) {
		String tableName = metadata.getTableName();
		SQL sql = new SQL().FROM(tableName + " _this");
		List<IdProperty> properties = metadata.getIdProperties();
		for (IdProperty property : properties) {
			Path path = root.getPath(property);
			String column = path.getColumn() + " AS " + path.getMapping();
			sql.SELECT(column);
		}
		// 加入查询条件
		where(sql, conditions);
		// 加入排序条件
		if (!StringUtils.isEmpty(orderBy)) {
			sql.ORDER_BY(orderBy);
		}
		return sql.toString();
	}

	public SqlNode selectUnionAll(Configuration configuration, String conditions, String orderBy, LockMode lockMode) {
		String sql = select(conditions, orderBy).toString();
		StringBuffer sqlSb = new StringBuffer();
		Matcher matcher = ARGUMENT_REGEX.matcher(sql);
		while (matcher.find()) {
			String name = matcher.group(1);
			matcher.appendReplacement(sqlSb, "#{item." + name + "}");
		}
		matcher.appendTail(sqlSb);
		if (lockMode == LockMode.UPGRADE) {
			sqlSb.append(" FOR UPDATE");
		} else if (lockMode == LockMode.UPGRADE_NOWAIT) {
			sqlSb.append(" FOR UPDATE NOWAIT");
		}
		SqlNode contents = new TextSqlNode(sqlSb.toString());
		String collectionExpression = "list";
		String index = "index";
		String item = "item";
		String open = "";
		String close = "";
		String separator = " UNION ALL ";
		return new ForEachSqlNode(configuration, contents, collectionExpression, index, item, open, close, separator);
	}

	private void where(SQL sql, String conditions) {
		// 外连接其他表
		for (AssociationProperty association : metadata.getAssociations()) {
			// 非Join方式不处理
			if (association.getFetchMode() != FetchMode.JOIN) {
				continue;
			}
			Class<?> referenceType = association.getReferenceType();
			EntityMetadata refMetadata = EntityMetadata.get(referenceType);
			// 获取主表和从表的关联属性
			StringBuilder joinBuilder = new StringBuilder();
			joinBuilder.append(refMetadata.getTableName());
			joinBuilder.append(" AS ");
			joinBuilder.append("_").append(association.getName());
			joinBuilder.append(" ON ");
			List<ColumnProperty> assocProperties = association.getProperties();
			List<ColumnProperty> refReferences = association.getReferences();
			for (int i = 0; i < assocProperties.size(); i++) {
				if (i > 0) {
					joinBuilder.append(" AND ");
				}
				ColumnProperty property = assocProperties.get(i);
				ColumnProperty reference = refReferences.get(i);
				joinBuilder.append(root.getPath(property).getColumn());
				joinBuilder.append("=");
				joinBuilder.append(root.getPath(association, reference).getColumn());
			}
			sql.LEFT_OUTER_JOIN(joinBuilder.toString());
		}
		if (!StringUtils.isEmpty(conditions)) {
			sql.WHERE(conditions);
		}
	}

	public String insert() {
		String tableName = metadata.getTableName();
		List<ColumnProperty> properties = metadata.getInsertProperties();
		SQL sql = new SQL().INSERT_INTO(tableName);
		for (ColumnProperty property : properties) {
			// 是否为主键列
			if (property instanceof IdProperty) {
				IdProperty idProperty = (IdProperty) property;
				if (idProperty.keyGenerator != null) {
					continue;
				}
			}
			String name = property.getName();
			String columnName = property.getColumnName();
			sql.VALUES(columnName, "#{" + name + "}");
		}
		return sql.toString();
	}

	public String update(String[] fields, String conditions) {
		SQL sql = new SQL().UPDATE(metadata.getTableName() + " AS _this");
		for (ColumnProperty property : metadata.getUpdateProperties()) {
			String propertyName = property.getName();
			// 不包含字段
			if (fields.length > 0 && !ArrayUtils.contains(fields, propertyName)) {
				continue;
			}
			Path path = root.getPath(property);
			sql.SET(path.getColumn() + "=#{" + path.getPropertyName() + "}");
		}
		if (conditions != null && conditions.length() > 0) {
			sql.WHERE(conditions);
		}
		return sql.toString();
	}

	public String delete(String conditions) {
		StringBuilder sqlBuff = new StringBuilder();
		sqlBuff.append("DELETE ").append(" _this FROM ").append(metadata.getTableName()).append(" _this");
		if (conditions != null && conditions.length() > 0) {
			sqlBuff.append(" WHERE ").append(conditions);
		}
		return sqlBuff.toString();
	}

}
