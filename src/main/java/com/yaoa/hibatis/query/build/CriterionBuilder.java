/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.query.build;

import java.lang.reflect.Field;

import com.yaoa.hibatis.lock.LockMode;
import com.yaoa.hibatis.metadata.Root;
import com.yaoa.hibatis.query.Connective;
import com.yaoa.hibatis.query.Criterion;
import com.yaoa.hibatis.query.Operator;
import com.yaoa.hibatis.query.Predicate;
import com.yaoa.hibatis.query.Sort;
import com.yaoa.hibatis.query.impl.CriterionImpl;
import com.yaoa.hibatis.query.impl.PredicateImpl;

/**
 * 
 * 条件生成器
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月15日
 */
public class CriterionBuilder {

	private CriterionImpl criterion;

	private CriterionBuilderLink and;

	private CriterionBuilderLink or;

	private CriterionBuilder(){
		and = new CriterionBuilderLink(Connective.AND);
		or = new CriterionBuilderLink(Connective.OR);
		and.setBuilder(this);
		or.setBuilder(this);
	}

	private CriterionBuilder(Root root, Connective connective) {
		this();
		this.criterion = new CriterionImpl(root, connective);
	}

	public static CriterionBuilderLink create(Root root) {
		return new CriterionBuilder(root, Connective.AND).and;
	}
	
	public static CriterionBuilderLink create(Class<?> entityType) {
		Root root = Root.get(entityType);
		return create(root);
	}

	public static class CriterionBuilderLink {

		private Connective connective;

		private CriterionBuilder builder;

		public final CriterionBuilderLink and;

		public final CriterionBuilderLink or;

		public CriterionBuilderLink(Connective connective) {
			this.connective = connective;
			this.and  = null;
			this.or  = null;
		}
		
		private CriterionBuilderLink setBuilder(CriterionBuilder builder){
			this.builder = builder;
			try {
				Field field = getClass().getDeclaredField("and");
				field.setAccessible(true);
				field.set(this, builder.and);
				field = getClass().getDeclaredField("or");
				field.setAccessible(true);
				field.set(this, builder.or);
			} catch (Exception e) {	
				e.printStackTrace();
			}
			return this;
		}

		public CriterionBuilderLink cache() {
			builder.criterion.setCacheable(true);
			return this;
		}
		
		public CriterionBuilderLink cache(boolean cacheable) {
			builder.criterion.setCacheable(cacheable);
			return this;
		}
		
		public CriterionBuilderLink orderBy(Sort... sorts) {
			builder.criterion.setSorts(sorts);
			return this;
		}
		
		public CriterionBuilderLink orderBy(String... orders) {
			Sort[] sorts = new Sort[orders.length];
			for (int i = 0; i < sorts.length; i++) {
				String order = orders[i];
				sorts[i] = Sort.parse(order);
			}
			builder.criterion.setSorts(sorts);
			return this;
		}
		
		public CriterionBuilderLink lock() {
			return lock(LockMode.UPGRADE);
		}
		
		public CriterionBuilderLink lock(LockMode lockMode) {
			builder.criterion.setLock(lockMode);
			return this;
		}
		
		public CriterionBuilderLink page(int firstResult , int maxResults) {
			builder.criterion.setFirstResult(firstResult);
			builder.criterion.setMaxResults(maxResults);
			return this;
		}

		public CriterionBuilderLink equal(String name, Object value) {
			Predicate predicate = new PredicateImpl(connective, name, Operator.EQ, new Object[] { value });
			builder.criterion.addPredicate(predicate);
			return this;
		}

		public CriterionBuilderLink like(String name, String value) {
			Predicate predicate = new PredicateImpl(connective, name, Operator.LIKE, new Object[] { value });
			builder.criterion.addPredicate(predicate);
			return this;
		}

		public CriterionBuilderLink greaterThan(String name, Object value) {
			Predicate predicate = new PredicateImpl(connective, name, Operator.GT, new Object[] { value });
			builder.criterion.addPredicate(predicate);
			return this;
		}

		public CriterionBuilderLink greaterEqual(String name, Object value) {
			Predicate predicate = new PredicateImpl(connective, name, Operator.GE, new Object[] { value });
			builder.criterion.addPredicate(predicate);
			return this;
		}

		public CriterionBuilderLink lessThan(String name, Object value) {
			Predicate predicate = new PredicateImpl(connective, name, Operator.LT, new Object[] { value });
			builder.criterion.addPredicate(predicate);
			return this;
		}

		public CriterionBuilderLink lessEqual(String name, Object value) {
			Predicate predicate = new PredicateImpl(connective, name, Operator.LE, new Object[] { value });
			builder.criterion.addPredicate(predicate);
			return this;
		}

		public CriterionBuilderLink notEqual(String name, Object value) {
			Predicate predicate = new PredicateImpl(connective, name, Operator.NQ, new Object[] { value });
			builder.criterion.addPredicate(predicate);
			return this;
		}

		public CriterionBuilderLink in(String name, Object[] values) {
			Predicate predicate = new PredicateImpl(connective, name, Operator.IN, values);
			builder.criterion.addPredicate(predicate);
			return this;
		}

		public CriterionBuilderLink notIn(String name, Object[] values) {
			Predicate predicate = new PredicateImpl(connective, name, Operator.NOT_IN, values);
			builder.criterion.addPredicate(predicate);
			return this;
		}

		public CriterionBuilderLink isNull(String name) {
			Predicate predicate = new PredicateImpl(connective, name, Operator.IS_NULL, new Object[0]);
			builder.criterion.addPredicate(predicate);
			return this;
		}

		public CriterionBuilderLink isNotNull(String name) {
			Predicate predicate = new PredicateImpl(connective, name, Operator.NOT_NULL, new Object[0]);
			builder.criterion.addPredicate(predicate);
			return this;
		}

		public CriterionBuilderLink between(String name, Object value1, Object value2) {
			Predicate predicate = new PredicateImpl(connective, name, Operator.BETWEEN,
					new Object[] { value1, value2 });
			builder.criterion.addPredicate(predicate);
			return this;
		}

		public CriterionBuilderLink sql(String sql) {
			Predicate predicate = new PredicateImpl(connective, null, Operator.NONE, new Object[] { sql });
			builder.criterion.addPredicate(predicate);
			return this;
		}

		public CriterionBuilderLink and(CriterionBuilderLink link) {
			link.builder.criterion.setConnective(Connective.AND);
			builder.criterion.addChild(link.builder.criterion);
			return this;
		}

		public CriterionBuilderLink or(CriterionBuilderLink link) {
			link.builder.criterion.setConnective(Connective.OR);
			builder.criterion.addChild(link.builder.criterion);
			return this;
		}

		public Criterion build() {
			return builder.criterion;
		}

	}

}
