package com.hxuanyu.jdolt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于定义方法所属的互斥组。
 * <p>
 * 互斥组是一种约束机制，确保标记了相同互斥组名称的方法不能同时被调用。
 * 当一个方法被调用时，系统会检查该方法所属的互斥组，如果已经调用了属于相同互斥组的其他方法，
 * 则会抛出异常阻止当前方法的调用。
 * <p>
 * 处理逻辑：
 * 1. 当方法被调用时，系统会检查该方法所属的互斥组
 * 2. 如果已经调用了属于相同互斥组的其他方法，则抛出异常
 * 3. 否则，允许调用该方法
 * <p>
 * 注意：该注解不能与 {@link MethodAllowGroup} 或 {@link MethodExclusive} 同时使用。
 * 
 * @see com.hxuanyu.jdolt.util.validator.MethodConstraintValidator
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodMutexGroup {
    /**
     * 互斥组的名称数组。
     * <p>
     * 一个方法可以属于多个互斥组。如果任何一个互斥组有冲突，该方法都不能被调用。
     * 
     * @return 互斥组名称数组
     */
    String[] value();
}
