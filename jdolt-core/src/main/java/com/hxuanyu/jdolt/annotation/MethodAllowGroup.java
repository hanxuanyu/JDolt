package com.hxuanyu.jdolt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于定义方法允许共存的组。
 * 如果方法被标记为允许组，则它只允许与指定的方法同时调用。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodAllowGroup {
    String[] value();
}
