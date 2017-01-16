/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.query.build;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import com.yaoa.hibatis.metadata.Root;
import com.yaoa.hibatis.query.Criterion;
import com.yaoa.hibatis.query.Direction;
import com.yaoa.hibatis.query.Predicate;
import com.yaoa.hibatis.query.Sort;

/**
 * 
 * 条件转SQL命令
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月15日
 */
public class CriterionStatement {

	private Map<String, Object> parameters;

	private Criterion criterion;

	private String condition;

	private String orderBy;

	private static final Pattern PARAM_REGEX = Pattern.compile("#[{](\\w+)[}]", Pattern.MULTILINE);

	public CriterionStatement(Criterion criterion) {
		this.criterion = criterion;
		this.parameters = new HashMap<String, Object>();
		this.build();
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public String getCondition() {
		return condition;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public String getCacheName() {
		StringBuffer cacheKey = new StringBuffer();
		Matcher matcher = PARAM_REGEX.matcher(condition.trim());
		while (matcher.find()) {
			String name = matcher.group(1);
			Object value = parameters.get(name);
			String valueStr = value == null ? "null" : value.toString();
			matcher.appendReplacement(cacheKey, valueStr);
		}
		matcher.appendTail(cacheKey);
		if (!StringUtils.isEmpty(orderBy)) {
			cacheKey.append(">").append(orderBy.trim());
		}
		int maxResults = criterion.getMaxResults();
		if (maxResults > 0) {
			int firstResult = criterion.getFirstResult();
			cacheKey.append("|").append(firstResult).append(",").append(maxResults);
		}
		return cacheKey.toString();
	}

	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer("1");
		Matcher matcher = PARAM_REGEX.matcher("#{a},#{b}");
		while (matcher.find()) {
			matcher.appendReplacement(sb, "*");
			System.out.println(matcher.group(1));
		}
		matcher.appendTail(sb);
		System.out.println(sb);
	}

	private void build() {
		// 生成SQL语句和参数
		buildSqlAndParams();
		// 加入分页参数
		int firstResult = criterion.getFirstResult();
		int maxResults = criterion.getMaxResults();
		if (maxResults > 0) {
			parameters.put("firstResult", firstResult);
			parameters.put("maxResults", maxResults);
		}
		// 加入排序条件
		this.buildOrderBy();
	}

	private void buildSqlAndParams() {
		StringBuilder sb = new StringBuilder();
		buildSqlAndParams(criterion, sb);
		this.condition = sb.toString().replace("()", "");
	}

	private void buildSqlAndParams(Criterion criterion, StringBuilder sb) {
		if (!criterion.hasPredicates() && !criterion.hasChildren()) {
			return;
		}
		Root root = criterion.getRoot();
		Criterion parent = criterion.getParent();
		if (parent != null && parent.hasPredicates()) {
			sb.append(" ").append(criterion.getConnective()).append(" ");
			sb.append("(");
		}
		int i = 0;
		for (Predicate predicate : criterion.getPredicates()) {
			if (i > 0) {
				sb.append(" ").append(predicate.getConnective()).append(" ");
			}
			if (predicate.getPath() != null) {
				String path = predicate.getPath();
				String column = root.getPath(path).getColumn();
				sb.append(column);
				appendPredicate(sb, predicate);
			} else {
				sb.append(predicate.getValues()[0]);
			}
			i++;
		}
		for (Criterion child : criterion.getChildren()) {
			buildSqlAndParams(child, sb);
		}
		if (parent != null && parent.hasPredicates()) {
			sb.append(")");
		}
	}

	private void appendPredicate(StringBuilder sb, Predicate predicate) {
		Object[] values = predicate.getValues();
		String sqlPart = predicate.getOperator().getExpression();
		if (sqlPart.contains("{0}")) {
			String name = "p" + parameters.size();
			sqlPart = sqlPart.replace("{0}", "#{" + name + "}");
			this.parameters.put(name, values[0]);
		}
		if (sqlPart.contains("{1}")) {
			String name = "p" + parameters.size();
			sqlPart = sqlPart.replace("{1}", "#{" + name + "}");
			this.parameters.put(name, values[1]);
		}
		if (sqlPart.contains("{0...n}")) {
			StringBuilder paramBuf = new StringBuilder();
			for (int i = 0; i < values.length; i++) {
				String name = "p" + parameters.size();
				if (i > 0) {
					paramBuf.append(",");
				}
				paramBuf.append("#{").append(name).append("}");
				this.parameters.put(name, values[i]);
			}
			sqlPart = sqlPart.replace("{0...n}", paramBuf.toString());
		}
		sb.append(sqlPart);
	}

	public void buildOrderBy() {
		Root root = criterion.getRoot();
		Sort[] sorts = criterion.getSorts();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < sorts.length; i++) {
			Sort sort = sorts[i];
			String path = root.getPath(sort.getName()).getColumn();
			if (i > 0) {
				sb.append(",");
			}
			sb.append(path).append(" ").append(sort.getDirection() == Direction.DESC ? "DESC" : "ASC");
		}
		this.orderBy = sb.toString();
	}

}
