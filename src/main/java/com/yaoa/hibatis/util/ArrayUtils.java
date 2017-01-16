/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月28日
 */
@SuppressWarnings("unchecked")
public class ArrayUtils {

	public static <T> void addToList(T[] array , List<T> list){
		for (T item : array) {
			list.add(item);
		}
	}
	

	public static <T> String join(T[] array , String separator){
		StringBuilder sb = new StringBuilder();
		for (T item : array) {
			sb.append(separator).append(item);
		}
		if(sb.length() > 0){
			sb.delete(0, separator.length());
		}
		return sb.toString();
	}

	public static <T> T[] toArray(List<T> list){
		int length = list.size();
		if(length == 0){
			return (T[])list.toArray();
		}
		Class<?> componentType = list.get(0).getClass();
		T[] array = (T[]) Array.newInstance(componentType, length);
		for (int i = 0; i < length; i++) {
			array[i] = list.get(i);
		}
		return array;
	}

	public static <T> List<T> asList(T[] array) {
		List<T> list = new ArrayList<T>(array.length);
		for (int i = 0; i < array.length; i++) {
			T item = array[i];
			list.add(item);
		}
		return list;
	} 

	public static <T> T[] merge(T[] array1 , T[] array2) {
		int length = array1.length + array2.length;
		if(length == 0){
			return array1;
		}
		Class<?> componentType = (array1.length> 0 ? array1[0] : array2[0]).getClass() ;
		int offset = 0;
		T[] newArray = (T[]) Array.newInstance(componentType, length);
		for (T item : array1) {
			newArray[offset++] = item;
		}
		for (T item : array2) {
			newArray[offset++] = item;
		}
		return newArray;
	}
	
	public static boolean contains(Object[] array , Object item){
		return indexOf(array, item) != -1;
	}

	public static int indexOf(Object[] array , Object item){
		for (int i = 0; i < array.length; i++) {
			Object cur = array[i];
			if(item == null){
				if(cur == null){
					return i;
				}
			}else if(item.equals(cur)){
				return i;
			}
		}
		return -1;
	}
	
	public static byte[] merge(byte[] array1 ,byte[] array2) {
		int length = array1.length + array2.length;
		if(length == 0){
			return array1;
		}
		int offset = 0;
		byte[] newArray = new byte[length];
		for (byte item : array1) {
			newArray[offset++] = item;
		}
		for (byte item : array2) {
			newArray[offset++] = item;
		}
		return newArray;
	}

}
