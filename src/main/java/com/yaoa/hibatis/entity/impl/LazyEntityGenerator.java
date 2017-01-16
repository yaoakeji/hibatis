/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.entity.impl;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;

import org.springframework.asm.ClassWriter;
import org.springframework.asm.Opcodes;
import org.springframework.asm.Type;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;

import com.yaoa.hibatis.entity.HibatisEntity;
import com.yaoa.hibatis.metadata.AssociationProperty;
import com.yaoa.hibatis.metadata.CollectionProperty;
import com.yaoa.hibatis.metadata.ColumnProperty;
import com.yaoa.hibatis.metadata.EntityMetadata;
import com.yaoa.hibatis.metadata.Property;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月21日
 */
class LazyEntityGenerator {
	
	private ClassEmitter emitter;

	private ClassWriter writer;

	private Class<?> entityType;

	private String proxyClassName;

	private EntityMetadata metadata;
	
	public LazyEntityGenerator(Class<?> entityType) {
		this.writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		this.emitter = new ClassEmitter(writer);
		this.entityType = entityType;
		this.proxyClassName = entityType.getName() + "$$Hibatis$Lazy";
		this.metadata = EntityMetadata.get(entityType);
		// 开始声明类
		this.beginClass();
		// 加入属性
		addField(Opcodes.ACC_PRIVATE, "$target", Type.getType(Object.class).getDescriptor() , null);
		addField(Opcodes.ACC_PRIVATE, "$source", Type.getType(HibatisEntity.class).getDescriptor() , null);
		addField(Opcodes.ACC_PRIVATE, "$association", Type.getType(AssociationProperty.class).getDescriptor() , null);
		// 重写属性
		this.overrideProperties();
		// 结束声明类
		this.emitter.visitEnd();
	}

	public Class<?> generateClass() throws Exception {
		byte[] classBytes = writer.toByteArray();
		ClassLoader classLoader = this.getClass().getClassLoader();
		return org.springframework.cglib.core.ReflectUtils.defineClass(proxyClassName, classBytes, classLoader);
	}

	private void beginClass() {
		Type supperType = Type.getType(entityType);
		Type[] interfaceTypes = new Type[] { Type.getType(Serializable.class), Type.getType(HibatisEntity.class) };
		this.emitter.begin_class(Opcodes.V1_5, Opcodes.ACC_PUBLIC, proxyClassName, supperType, interfaceTypes,
				null);
		addConstructor();
	}

	private void addConstructor() {
		// 创建构造方法
		CodeEmitter e = emitter.begin_method(Opcodes.ACC_PUBLIC, TypeUtils.parseConstructor(""), null);
		e.load_this();
		e.super_invoke_constructor();
		// 返回值
		e.return_value();
		// 结束方法
		e.end_method();
	}

	private void addField(int access, String name, String desc, Object value) {
		this.emitter.visitField(access, name, desc, null, value);
	}

	private void overrideProperties() {
		Collection<ColumnProperty> properties = metadata.getSelectProperties();
		for (ColumnProperty property : properties) {
			overrideGetter(property);
		}
		Collection<AssociationProperty> associations = metadata.getAssociations();
		for (AssociationProperty association : associations) {
			overrideGetter(association);
		}
		Collection<CollectionProperty> collections = metadata.getCollections();
		for (CollectionProperty collection : collections) {
			overrideGetter(collection);
		}
	}

	private void overrideGetter(Property property) {
		Method method = property.getDescriptor().getReadMethod();
		if(method == null){
			throw new RuntimeException("属性["+ property.getName() +"]没有Getter");
		}
		String methodName = method.getName();
		if(methodName.equals("isOnSale")){
			System.err.println("1111111111");
		}
		Type valueType = Type.getType(property.getType());
		Signature signature = new Signature(methodName, valueType, new Type[] {});
		// 重写方法
		CodeEmitter e = this.emitter.begin_method(Opcodes.ACC_PUBLIC, signature, null);
		e.visitCode();
		e.visitLdcInsn(methodName);
		e.visitLdcInsn(property.getName());
		e.load_this();
		signature = new Signature("onPropertyGetterInvoke", Type.getType(Object.class),
				new Type[] { Type.getType(String.class) , Type.getType(String.class), Type.getType(Object.class) });
		e.invoke_static(Type.getType(LazyEntityEnhancer.class), signature);
		// 判断是否为包装类型
		Type boxedValueType = TypeUtils.getBoxedType(valueType);
		if (boxedValueType != valueType) {
			e.checkcast(boxedValueType);
			e.visitVarInsn(Opcodes.ASTORE, 0);
			e.visitVarInsn(Opcodes.ALOAD, 0);
			String unboxedMethod = valueType.getClassName() + "Value";
			signature = new Signature(unboxedMethod, valueType, new Type[] {});
			e.invoke_virtual(boxedValueType , signature);
		} else {
			e.checkcast(valueType);
			e.visitVarInsn(Opcodes.ASTORE, 0);
			e.visitVarInsn(Opcodes.ALOAD, 0);
		}
		e.return_value();
		e.end_method();
	}
	
}
