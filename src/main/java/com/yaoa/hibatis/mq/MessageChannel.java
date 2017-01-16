package com.yaoa.hibatis.mq;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Description TODO
 * @author cjh
 * @version 1.0
 * @date：2016年12月29日 上午11:28:53
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target({ElementType.TYPE})
@Documented
public @interface MessageChannel {
	
	String value();

}
