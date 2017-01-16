/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.query.aggregate.impl;

import com.yaoa.hibatis.metadata.Root;
import com.yaoa.hibatis.query.Criterion;
import com.yaoa.hibatis.query.aggregate.Aggregate;
import com.yaoa.hibatis.query.aggregate.Specification;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月31日
 */
public class AggregateImpl implements Aggregate{
	
	private Root root;
	
	private Criterion criterion; 
	
	private Specification[] specifications;
	
	private String[] groups;

	public Root getRoot() {
		return root;
	}

	public void setRoot(Root root) {
		this.root = root;
	}

	public Criterion getCriterion() {
		return criterion;
	}

	public void setCriterion(Criterion criterion) {
		this.criterion = criterion;
	}

	public Specification[] getSpecifications() {
		return specifications;
	}

	public void setSpecifications(Specification[] specifications) {
		this.specifications = specifications;
	}

	public String[] getGroups() {
		return groups;
	}

	public void setGroups(String[] groups) {
		this.groups = groups;
	}
}
