package com.hxuanyu.jdolt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示当前方法必须是唯一调用的方法。
 * <p>
 * 这是一种排他性约束，确保标记了该注解的方法要么是第一个被调用的方法（此时不允许再调用其他方法），
 * 要么在其他方法已被调用的情况下不能被调用。
 * <p>
 * 处理逻辑：
 * 1. 如果当前方法被标记为唯一调用，且已经有其他方法被调用，则抛出异常
 * 2. 如果其他方法被标记为唯一调用且已被调用，则当前方法不能被调用
 * <p>
 * 注意：该注解不能与 {@link MethodMutexGroup} 或 {@link MethodAllowGroup} 同时使用。
 * 
 * @see com.hxuanyu.jdolt.util.validator.MethodConstraintValidator
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodExclusive {
}
