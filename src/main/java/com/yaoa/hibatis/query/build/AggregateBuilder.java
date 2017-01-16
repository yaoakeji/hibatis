/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.query.build;

import java.util.ArrayList;
import java.util.List;

import com.yaoa.hibatis.metadata.Root;
import com.yaoa.hibatis.query.Criterion;
import com.yaoa.hibatis.query.aggregate.Aggregate;
import com.yaoa.hibatis.query.aggregate.AggregateType;
import com.yaoa.hibatis.query.aggregate.Specification;
import com.yaoa.hibatis.query.aggregate.impl.AggregateImpl;
import com.yaoa.hibatis.query.aggregate.impl.SpecificationImpl;
import com.yaoa.hibatis.util.ArrayUtils;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月31日
 */
public class AggregateBuilder{

	private Root root;
	
	private Criterion criterion;
	
	private List<Specification> specifications = new ArrayList<Specification>();
	
	private String[] groups = new String[0];
	
	private AggregateBuilder(Root root){
		this.root = root;
	}
	
	public static AggregateBuilder create(Class<?> entityType){
		Root root = Root.get(entityType);
		return new AggregateBuilder(root);
	}
	
	public AggregateBuilder field(String path , String alias){
		Specification specification =  new SpecificationImpl(AggregateType.NONE, path, alias);
		this.specifications.add(specification);
		return this;
	}
	
	public AggregateBuilder field(String name){
		return this.field(name, name);
	}
	
	public AggregateBuilder count(String expression , String alias){
		Specification specification =  new SpecificationImpl(AggregateType.COUNT, expression, alias);
		this.specifications.add(specification);
		return this;
	}
	
	public AggregateBuilder count(String expression){
		return this.count(expression, expression);
	}
	
	public AggregateBuilder countDistinct(String expression , String alias){
		Specification specification =  new SpecificationImpl(AggregateType.COUNT_DISTINCT, expression, alias);
		this.specifications.add(specification);
		return this;
	}
	
	public AggregateBuilder countDistinct(String expression){
		return this.countDistinct(expression, expression);
	}
	
	public AggregateBuilder sum(String expression , String alias){
		Specification specification =  new SpecificationImpl(AggregateType.SUM, expression, alias);
		this.specifications.add(specification);
		return this;
	}
	
	public AggregateBuilder sum(String expression){
		return this.sum(expression, expression);
	}
	
	public AggregateBuilder max(String expression , String alias){
		Specification specification =  new SpecificationImpl(AggregateType.MAX, expression, alias);
		this.specifications.add(specification);
		return this;
	}
	
	public AggregateBuilder max(String expression){
		return this.max(expression, expression);
	}
	
	public AggregateBuilder min(String expression , String alias){
		Specification specification =  new SpecificationImpl(AggregateType.MIN, expression, alias);
		this.specifications.add(specification);
		return this;
	}
	
	public AggregateBuilder min(String expression){
		return this.min(expression, expression);
	}
	
	public AggregateBuilder groupBy(String... paths){
		this.groups = paths;
		return this;
	}
	
	public AggregateBuilder where(Criterion criterion){
		this.criterion = criterion;
		return this;
	}
	
	public AggregateBuilder whereEqual(String name , Object value){
		this.criterion = CriterionBuilder.create(root).equal(name, value).build();
		return this;
	}
	
	public Aggregate build(){
		AggregateImpl aggregate = new AggregateImpl();
		aggregate.setCriterion(criterion);
		aggregate.setRoot(root);
		aggregate.setGroups(groups);
		aggregate.setSpecifications(ArrayUtils.toArray(specifications));
		return aggregate;
	}
}
