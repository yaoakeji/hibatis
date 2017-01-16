/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.metadata;

import java.lang.reflect.Field;

import com.yaoa.hibatis.annotation.Column;
import com.yaoa.hibatis.util.CamelCaseUtils;

/**
 * 
 * 列属性
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年7月15日
 */
public class ColumnProperty extends AbstractProperty {

	private String columnName;

	public ColumnProperty(EntityMetadata metadata, Field field) {
		super(metadata, field);
		// 获取列名
		Column column = field.getAnnotation(Column.class);
		if (column == null || "".equals(column.name())) {
			columnName = CamelCaseUtils.toUnderlineName(field.getName());
		} else {
			columnName = column.name();
		}
	}

	public String getColumnName() {
		return columnName;
	}
}
