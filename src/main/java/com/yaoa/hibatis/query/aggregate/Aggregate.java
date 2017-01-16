/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.query.aggregate;

import com.yaoa.hibatis.metadata.Root;
import com.yaoa.hibatis.query.Criterion;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月31日
 */
public interface Aggregate {
	
	public Root getRoot();
	
	public Criterion getCriterion();
	
	public Specification[] getSpecifications();
	
	public String[] getGroups();
}
