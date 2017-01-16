/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.entity.impl;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.asm.ClassWriter;
import org.springframework.asm.Opcodes;
import org.springframework.asm.Type;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.Signature;
import org.springframework.cglib.core.TypeUtils;

import com.yaoa.hibatis.entity.EntityEnhancer;
import com.yaoa.hibatis.entity.EntityState;
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
public class ProxyClassGenerator {
	
	private ClassEmitter emitter;

	private ClassWriter writer;

	private Class<?> entityType;

	private String entityClassPath;

	private String proxyClassName;

	private String proxyClassPath;
	
	private EntityMetadata metadata;

	public ProxyClassGenerator(Class<?> entityType , boolean overrideProperties) {
		this.writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		this.emitter = new ClassEmitter(writer);

		this.entityType = entityType;
		this.proxyClassName = entityType.getName() + "$$Hibatis";
		this.proxyClassPath = proxyClassName.replace(".", "/");
		this.entityClassPath = entityType.getName().replace(".", "/");
		this.metadata = EntityMetadata.get(entityType);
		// 开始声明类
		this.beginClass();
		// 声明字段
		Long serialVersionUID = new Long(proxyClassName.hashCode());
		int access = Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL;
		this.addField(access, "serialVersionUID", "J", serialVersionUID);
		// 加入字段
		this.addField(Opcodes.ACC_PRIVATE, "$propertyChanges", Type.getDescriptor(List.class), null);
		// 加入属性
		this.addProperty("$version", Type.getType(String.class), false, false); // 版本
		this.addProperty("$state", Type.getType(EntityState.class), false, false); // 状态
		this.addProperty("$type", Type.getType(Class.class), false, false); // 类型
		// 重写所有的属性
		if(overrideProperties){
			this.overrideProperties();
		}
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
		putField(e, "$version", metadata.getVersion()); // 设置版本
		putEnumField(e, "$state", EntityState.Transient); // 设置状态
		putClassField(e, "$type", entityType); // 实体类型
		putNewField(e, "$propertyChanges", ArrayList.class, List.class);
		// 返回值
		e.return_value();
		// 结束方法
		e.end_method();
	}

	private void putField(CodeEmitter e, String name, Object value) {
		String desc = Type.getDescriptor(value.getClass());
		e.visitVarInsn(Opcodes.ALOAD, 0);
		e.visitLdcInsn(value);
		e.visitFieldInsn(Opcodes.PUTFIELD, proxyClassPath, name, desc);
	}

	private void putEnumField(CodeEmitter e, String name, Enum<?> value) {
		Type type = Type.getType(value.getClass());
		e.visitVarInsn(Opcodes.ALOAD, 0);
		e.getstatic(type, value.name(), type);
		e.visitFieldInsn(Opcodes.PUTFIELD, proxyClassPath, name, type.getDescriptor());
	}

	private void putClassField(CodeEmitter e, String name, Class<?> type) {
		String desc = Type.getDescriptor(type.getClass());
		e.visitVarInsn(Opcodes.ALOAD, 0);
		e.visitLdcInsn(Type.getType(type));
		e.visitFieldInsn(Opcodes.PUTFIELD, proxyClassPath, name, desc);
	}

	private void putNewField(CodeEmitter e, String name, Class<?> realType, Class<?> interfaceType) {
		String realTypePath = realType.getName().replace(".", "/");
		e.visitVarInsn(Opcodes.ALOAD, 0);
		e.visitTypeInsn(Opcodes.NEW, realTypePath);
		e.visitInsn(Opcodes.DUP);
		e.visitMethodInsn(Opcodes.INVOKESPECIAL, realTypePath, "<init>", "()V", false);
		e.visitFieldInsn(Opcodes.PUTFIELD, proxyClassPath, name, Type.getDescriptor(interfaceType));
	}

	private void addField(int access, String name, String desc, Object value) {
		this.emitter.visitField(access, name, desc, null, value);
	}

	private void addProperty(String name, Type type, boolean isGetter, boolean isSetter) {
		String property = TypeUtils.upperFirst(name);
		// 声明字段
		this.addField(Opcodes.ACC_PRIVATE, name, type.getDescriptor(), null);
		// 声明读方法
		if (isGetter) {
			Signature signature = new Signature("get" + property, type, new Type[] {});
			CodeEmitter e = this.emitter.begin_method(Opcodes.ACC_PUBLIC, signature, null);
			e.load_this();
			e.visitVarInsn(Opcodes.ALOAD, 0);
			e.getfield(name);
			e.return_value();
			e.end_method();
		}
		// 声明写方法
		if (isSetter) {
			Signature signature = new Signature("set" + property, Type.VOID_TYPE, new Type[] { type });
			CodeEmitter e = this.emitter.begin_method(Opcodes.ACC_PUBLIC, signature, null);
			e.load_this();
			e.load_arg(0);
			e.putfield(name);
			e.return_value();
			e.end_method();
		}
	}

	private void overrideProperties() {
		Collection<ColumnProperty> properties = metadata.getSelectProperties();
		for (ColumnProperty property : properties) {
			overrideSetter(property);
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
		Type valueType = Type.getType(property.getType());
		Signature signature = new Signature(methodName, valueType, new Type[] {});
		// 加入父方法代理
		addSuperProxyMethod(signature);
		// 重写方法
		CodeEmitter e = this.emitter.begin_method(Opcodes.ACC_PUBLIC, signature, null);
		e.visitCode();
		e.visitLdcInsn(methodName);
		e.visitLdcInsn(property.getName());
		e.load_this();
		signature = new Signature("onPropertyGetterInvoke", Type.getType(Object.class),
				new Type[] { Type.getType(String.class) , Type.getType(String.class), Type.getType(HibatisEntity.class) });
		e.invoke_static(Type.getType(EntityEnhancer.class), signature);
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

	private void overrideSetter(Property property) {
		Method method = property.getDescriptor().getWriteMethod();
		if(method == null){
			throw new RuntimeException("属性["+ property.getName() +"]没有Setter");
		}
		String methodName = method.getName();
		Type valueType = Type.getType(property.getType());
		Signature signature = new Signature(methodName, Type.VOID_TYPE, new Type[] { valueType });
		// 加入父方法代理
		addSuperProxyMethod(signature);
		// 重写方法
		CodeEmitter e = this.emitter.begin_method(Opcodes.ACC_PUBLIC, signature, null);
		e.visitCode();
		e.visitLdcInsn(methodName);
		e.visitLdcInsn(property.getName());
		// 判断是否非包装类型
		Type boxedValueType = TypeUtils.getBoxedType(valueType);
		if (boxedValueType != valueType) {
			e.getstatic(boxedValueType, "TYPE", Type.getType(Class.class));
		} else {
			e.visitLdcInsn(valueType);
		}
		e.load_this();
		e.load_arg(0);
		// 判断是否非包装类型
		if (boxedValueType != valueType) {
			signature = new Signature("valueOf", boxedValueType, new Type[] { valueType });
			e.invoke_static(boxedValueType, signature);
		} 
		signature = new Signature("onPropertySetterInvoke", Type.VOID_TYPE, new Type[] { 
				Type.getType(String.class) , Type.getType(String.class), Type.getType(Class.class),
				Type.getType(HibatisEntity.class), Type.getType(Object.class) });
		e.invoke_static(Type.getType(EntityEnhancer.class), signature);
		e.return_value();
		e.end_method();
	}

	private void addSuperProxyMethod(Signature signature) {
		String methodName = signature.getName();
		signature = new Signature("$super_" + methodName, signature.getReturnType(), signature.getArgumentTypes());
		CodeEmitter e = this.emitter.begin_method(Opcodes.ACC_PRIVATE, signature, null);
		e.visitCode();
		e.visitVarInsn(Opcodes.ALOAD, 0);
		for (int i = 0; i < signature.getArgumentTypes().length; i++) {
			e.load_arg(i);
		}
		e.visitMethodInsn(Opcodes.INVOKESPECIAL, entityClassPath, methodName, signature.getDescriptor(), false);
		e.return_value();
		e.end_method();
	}

}
