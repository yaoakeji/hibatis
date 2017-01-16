/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.query.build;

import java.util.HashMap;
import java.util.Map;

import com.yaoa.hibatis.exception.HibatisException;
import com.yaoa.hibatis.metadata.Path;
import com.yaoa.hibatis.metadata.Root;
import com.yaoa.hibatis.query.Criterion;
import com.yaoa.hibatis.query.aggregate.Aggregate;
import com.yaoa.hibatis.query.aggregate.AggregateType;
import com.yaoa.hibatis.query.aggregate.Specification;

/**
 * 
 * 聚合命令生成器
 * @author kingsy.lin
 * @version 1.0 , 2016年10月31日
 */
public class AggregateStatement {

	private Aggregate aggregate;

	private Map<String, Object> parameters;

	private String select;

	private String condition;

	private String groupBy;

	private String orderBy;
	
	public AggregateStatement(Aggregate aggregate) {
		this.aggregate = aggregate;
		this.parameters = new HashMap<String, Object>();
		this.build();
	}

	private void build() {
		// 构建条件
		Criterion criterion = aggregate.getCriterion();
		if (criterion == null) {
			criterion = CriterionBuilder.create(aggregate.getRoot()).build();
		}
		CriterionStatement criterionStatement = new CriterionStatement(criterion);
		this.parameters = criterionStatement.getParameters();
		this.condition = criterionStatement.getCondition();
		this.orderBy = criterionStatement.getOrderBy();
		// 构建查询语句
		this.select = buildSelect();
		this.groupBy = buildGroupBy();
	}
	
	private String buildSelect(){
		Root root = aggregate.getRoot();
		StringBuilder sb = new StringBuilder();
		Specification[] specifications = aggregate.getSpecifications();
		for (Specification specification : specifications) {
			String alias = specification.getAlias();
			String expression = specification.getExpression();
			expression = root.parseEl(expression);
			AggregateType type = specification.getType();
			if (type == AggregateType.NONE) {
				sb.append(",");
			} else if (type == AggregateType.COUNT) {
				sb.append(",COUNT(");
			} else if (type == AggregateType.COUNT_DISTINCT) {
				sb.append(",COUNT(DISTINCT ");
			} else if (type == AggregateType.MAX) {
				sb.append(",MAX(");
			} else if (type == AggregateType.MIN) {
				sb.append(",MIN(");
			} else if (type == AggregateType.SUM) {
				sb.append(",SUM(");
			} else if (type == AggregateType.AVG) {
				sb.append(",AVG(");
			}else{
				throw new HibatisException("不支持的聚合类型");
			}
			sb.append(expression).append(") AS '").append(alias).append("'");
		}
		sb.delete(0 , 1);
		return sb.toString();
	}
	
	private String buildGroupBy(){
		StringBuilder sb = new StringBuilder();
		Root root = aggregate.getRoot();
		for (String group : aggregate.getGroups()) {
			Path path = root.getPath(group);
			sb.append(",").append(path.getColumn());
		}
		sb.delete(0 , 1);
		return sb.toString();
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

	public String getSelect() {
		return select;
	}

	public String getGroupBy() {
		return groupBy;
	}
}
