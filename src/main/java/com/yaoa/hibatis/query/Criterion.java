/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.query;

import com.yaoa.hibatis.lock.LockMode;
import com.yaoa.hibatis.metadata.Root;

/**
 * 
 * 条件描述
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月15日
 */
public interface Criterion{
	
	public Root getRoot();
	
	public boolean isCacheable();
	
	public Criterion getParent();
	
	public Connective getConnective();

	public Iterable<Predicate> getPredicates();
	
	public Iterable<Criterion> getChildren();
	
	public boolean hasPredicates();
	
	public boolean hasChildren();
	
	public void addPredicate(Predicate predicate);
	
	public void addChild(Criterion criterion);
	
	public Sort[] getSorts();
	
	public void setSorts(Sort[] sorts);
	
	public void setFirstResult(int firstResult);
	
	public void setMaxResults(int maxResults);
	
	public int getFirstResult();
	
	public int getMaxResults();
	
	public LockMode getLockMode();
}
