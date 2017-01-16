/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.query.impl;

import java.util.ArrayList;
import java.util.List;

import com.yaoa.hibatis.lock.LockMode;
import com.yaoa.hibatis.metadata.Root;
import com.yaoa.hibatis.query.Connective;
import com.yaoa.hibatis.query.Criterion;
import com.yaoa.hibatis.query.Predicate;
import com.yaoa.hibatis.query.Sort;

/**
 * 
 * 条件实现类
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月15日
 */
public class CriterionImpl implements Criterion {
	
	private Root root;
	
	private boolean isCacheable;
	
	private Criterion parent;
	
	private Connective connective;
	
	private List<Predicate> predicates;
	
	private List<Criterion> children;
	
	private int firstResult = -1;
	
	private int maxResults = 0;
	
	private LockMode lockMode = LockMode.NONE;
	
	private Sort[] sorts = new Sort[0];
	
	public CriterionImpl(Root root , Connective connective){
		this.root = root;
		this.connective = connective;
		this.predicates = new ArrayList<Predicate>();
		this.children = new ArrayList<Criterion>();
	}

	public Connective getConnective() {
		return connective;
	}

	public Iterable<Predicate> getPredicates() {
		return predicates;
	}

	public Iterable<Criterion> getChildren() {
		return children;
	}

	public void addPredicate(Predicate predicate) {
		this.predicates.add(predicate);
	}

	public void addChild(Criterion criterion) {
		((CriterionImpl)criterion).parent = this;
		this.children.add(criterion);
	}
	
	public void setConnective(Connective connective) {
		this.connective = connective;
	}

	public Root getRoot() {
		return root;
	}

	public void setRoot(Root root) {
		this.root = root;
	}

	public Criterion getParent() {
		return parent;
	}

	public boolean hasPredicates() {
		return !this.predicates.isEmpty();
	}

	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	public boolean isCacheable() {
		if(!root.getMetadata().cacheable()){
			return false;
		}
		return isCacheable;
	}

	public void setCacheable(boolean isCacheable) {
		this.isCacheable = isCacheable;
	}

	
	public Sort[] getSorts() {
		return sorts;
	}
	
	
	public void setSorts(Sort[] sorts){
		this.sorts = sorts;
	}

	
	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	
	public int getFirstResult() {
		return firstResult;
	}

	
	public int getMaxResults() {
		return maxResults;
	}
	
	public void setLock(LockMode lockMode) {
		this.lockMode = lockMode;
	}

	public LockMode getLockMode() {
		return lockMode;
	}
}
