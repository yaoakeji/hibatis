/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.ibatis;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;

import com.yaoa.hibatis.annotation.FetchMode;
import com.yaoa.hibatis.entity.EntityEnhancer;
import com.yaoa.hibatis.metadata.AssociationProperty;
import com.yaoa.hibatis.metadata.ColumnProperty;
import com.yaoa.hibatis.metadata.EntityMetadata;
import com.yaoa.hibatis.metadata.IdProperty;
import com.yaoa.hibatis.metadata.Path;
import com.yaoa.hibatis.metadata.Root;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月20日
 */
class ResultMapBuilder {

	private Configuration configuration;

	public ResultMapBuilder(Configuration configuration) {
		this.configuration = configuration;
	}

	// 生成结果集id
	private String buildResultMapId(Class<?> entityType, String name) {
		return "hibatis:" + entityType.getName() + "." + name;
	}
	
	public ResultMap autoMapping(Class<?> resultType) {
		String mapId = buildResultMapId(resultType, "autoMapping");
		if (configuration.hasResultMap(mapId)) {
			return configuration.getResultMap(mapId);
		}
		List<ResultMapping> mappings = new ArrayList<ResultMapping>();
		ResultMap resultMap = new ResultMap.Builder(configuration, mapId, resultType, mappings , true).build();
		StatementResultMapHelper.addResultMap(configuration, resultMap);
		return resultMap;
	}

	public ResultMap getDefault(Class<?> entityType) {
		// 结果映射名
		String mapId = buildResultMapId(entityType, "default");
		if (configuration.hasResultMap(mapId)) {
			return configuration.getResultMap(mapId);
		}
		Root root = Root.get(entityType);
		List<ResultMapping> mappings = new ArrayList<ResultMapping>();
		// 设置列映射
		EntityMetadata metadata = EntityMetadata.get(entityType);
		for (ColumnProperty property : metadata.getSelectProperties()) {
			Path path = root.getPath(property);
			String propName = path.getPropertyName();
			String mapping = path.getMapping();
			Class<?> javaType = property.getType();
			mappings.add(new ResultMapping.Builder(configuration, propName, mapping, javaType).build());
		}
		// 外连接其他表
		for (AssociationProperty association : metadata.getAssociations()) {
			// 非Join方式不处理
			if (association.getFetchMode() != FetchMode.JOIN) {
				continue;
			}
			// 是否懒加载
			if(association.isLazy()){
				continue;
			}
			// 判断时关联类是否使用缓存，是则忽略
			Class<?> referenceType = association.getReferenceType();
			EntityMetadata refMetadata = EntityMetadata.get(referenceType);
			if (refMetadata.cacheable()) {
				continue;
			}
			// 加入关联映射
			ResultMapping resultMapping = createAssocResultMapping(association);
			mappings.add(resultMapping);
		}
		// 加入结果映射
		Class<?> resultType = EntityEnhancer.getEnhancer(entityType).getProxyClass();
		ResultMap resultMap = new ResultMap.Builder(configuration, mapId, resultType, mappings).build();
		StatementResultMapHelper.addResultMap(configuration, resultMap);
		return resultMap;
	}

	// 创建关联的结果映射
	private ResultMapping createAssocResultMapping(AssociationProperty association) {
		Class<?> entityType = association.getMetadata().getEntityType();
		Root root = Root.get(entityType);
		Class<?> referenceType = association.getReferenceType();
		EntityMetadata refMetadata = EntityMetadata.get(referenceType);
		List<ResultMapping> mappings = new ArrayList<ResultMapping>();
		for (ColumnProperty property : refMetadata.getSelectProperties()) {
			Path path = root.getPath(association, property);
			String propName = path.getPropertyName();
			String mapping = path.getMapping();
			Class<?> javaType = property.getType();
			mappings.add(new ResultMapping.Builder(configuration, propName, mapping, javaType).build());
		}
		Class<?> referenceProxyClass = EntityEnhancer.getEnhancer(referenceType).getProxyClass();
		String resultMapId = buildResultMapId(entityType, "association_" + association.getName());
		ResultMap resultMap = new ResultMap.Builder(configuration, resultMapId, referenceProxyClass, mappings).build();
		StatementResultMapHelper.addResultMap(configuration, resultMap);
		return new ResultMapping.Builder(configuration, association.getName()).nestedResultMapId(resultMapId)
				.javaType(referenceProxyClass).build();
	}

	public ResultMap getId(Class<?> entityType , Class<?> resultType) {
		// 结果映射名
		String mapId = buildResultMapId(entityType, "id");
		if (configuration.hasResultMap(mapId)) {
			return configuration.getResultMap(mapId);
		}
		List<ResultMapping> mappings = new ArrayList<ResultMapping>();
		// 设置列映射
		Root root = Root.get(entityType);
		EntityMetadata metadata = EntityMetadata.get(entityType);
		for (IdProperty property : metadata.getIdProperties()) {
			Path path = root.getPath(property);
			String propName = property.getName();
			String mapping = path.getMapping();
			Class<?> javaType = property.getType();
			mappings.add(new ResultMapping.Builder(configuration, propName, mapping, javaType).build());
		}
		// 加入结果映射
		ResultMap resultMap = new ResultMap.Builder(configuration, mapId, resultType, mappings).build();
		StatementResultMapHelper.addResultMap(configuration, resultMap);
		return resultMap;
	}

	public ResultMap count(Class<?> entityType) {
		// 结果映射名
		String mapId = buildResultMapId(entityType, "count");
		if (configuration.hasResultMap(mapId)) {
			return configuration.getResultMap(mapId);
		}
		List<ResultMapping> mappings = new ArrayList<ResultMapping>();
		ResultMap map = new ResultMap.Builder(configuration, mapId, long.class, mappings).build();
		return StatementResultMapHelper.addResultMap(configuration, map);
	}
}
