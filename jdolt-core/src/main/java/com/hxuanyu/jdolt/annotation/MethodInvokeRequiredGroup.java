package com.hxuanyu.jdolt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标注类中一个或多个方法必须被调用。
 * 参数 allRequired 控制是否所有方法都必须调用，默认值为 true。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MethodInvokeRequiredGroup {
    String[] value(); // 必须调用的方法名

    boolean allRequired() default true; // 是否要求所有方法都必须调用
}