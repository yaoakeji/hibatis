/*
 * Copyright 2015-2016 Yaoa & Co., Ltd.
 */
package com.yaoa.hibatis.ibatis;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.session.Configuration;

/**
 * 
 *
 * @author kingsy.lin
 * @version 1.0 , 2016年10月24日
 */
class StatementResultMapHelper {
	
	public static MappedStatement addMappedStatement(Configuration cfg , MappedStatement ms) {
		String msId = ms.getId();
		StatementResultLock lock = StatementResultLock.getLock("s#".concat(msId));
		lock.lock();
		try {
			if (cfg.hasStatement(msId)) {
				return cfg.getMappedStatement(msId);
			}else{
				cfg.addMappedStatement(ms);	
				return ms;
			}
		} finally {
			lock.unlock();
		}
	}
	
	public static ResultMap addResultMap(Configuration cfg , ResultMap map) {
		String mapId = map.getId();
		StatementResultLock lock = StatementResultLock.getLock("m#".concat(mapId));
		lock.lock();
		try {
			if (cfg.hasResultMap(mapId)) {
				return cfg.getResultMap(mapId);
			}else{
				cfg.addResultMap(map);
				return map;
			}
		} finally {
			lock.unlock();
		}
	}
	
}


