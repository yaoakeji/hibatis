/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.metadata;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yaoa.hibatis.exception.HibatisException;
import com.yaoa.hibatis.metadata.impl.PathImpl;
import com.yaoa.hibatis.util.ReflectionUtils;

/**
 *
 * 根路径
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月15日
 */
public class Root {

	private EntityMetadata metadata;

	private Root(Class<?> entityType) {
		this.metadata = EntityMetadata.get(entityType);
	}

	public static Root get(Class<?> entityType) {
		return new Root(entityType);
	}

	public Class<?> getEntityType() {
		return metadata.getEntityType();
	}

	public EntityMetadata getMetadata() {
		return metadata;
	}

	public Path getPath(ColumnProperty property) {
		return new PathImpl(null, property);
	}

	public Path getPath(ReferenceProperty reference, ColumnProperty property) {
		return new PathImpl(reference, property);
	}

	@SuppressWarnings("unchecked")
	public EntityID toId(Object obj) {
		EntityID id = new EntityID(metadata);
		if (obj instanceof Map) {
			id.putAll((Map<String, Object>) obj);
		} else if (obj.getClass().isArray()) {
			id.putArray(obj);
		} else if (ReflectionUtils.isComplexType(obj.getClass())) {
			id.putObject(obj);
		} else {
			id.putFirst(obj);
		}
		return id;
	}

	public Path getPath(String expression, boolean throwEx) {
		ReferenceProperty reference;
		ColumnProperty property;
		String[] names = expression.split("[.]");
		if (names.length == 1) {
			reference = null;
			property = (ColumnProperty) metadata.findProperty(names[0]);
		} else {
			reference = (ReferenceProperty) metadata.findProperty(names[0]);
			if (reference == null) {
				if (throwEx) {
					throw new HibatisException("Path [" + names[0] + "] is not exists");
				} else {
					return null;
				}
			}
			property = (ColumnProperty) reference.findSubProperty(names[1]);
		}
		if (property == null) {
			if (throwEx) {
				throw new HibatisException("Path [" + expression + "] is not exists");
			} else {
				return null;
			}
		}
		return new PathImpl(reference, property);
	}

	public Path getPath(String expression) {
		return getPath(expression, true);
	}

	public String parseEl(String expression) {
		StringBuffer sb = new StringBuffer();
		Pattern p = Pattern.compile("[a-zA-z]\\w*([.]\\w+)?");
		Matcher matcher = p.matcher(expression);
		while (matcher.find()) {
			String name = matcher.group();
			Path path = getPath(name, true);
			String column = path != null ? path.getColumn() : name;
			matcher.appendReplacement(sb, column);
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

}
