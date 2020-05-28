package com.iwhalecloud.verfication.mq.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author luotuan
 * @Description
 * @create 2020-05-26 14:42
 **/
@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MsgSend {
}
