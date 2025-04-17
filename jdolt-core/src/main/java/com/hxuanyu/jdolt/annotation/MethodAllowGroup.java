package com.hxuanyu.jdolt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于定义方法允许共存的组。
 * <p>
 * 如果方法被标记为允许组，则它只允许与指定的方法同时调用。这是一种严格的约束机制，
 * 确保标记了该注解的方法只能与明确列出的其他方法一起使用。
 * <p>
 * 处理逻辑：
 * 1. 当方法被调用时，系统会检查是否有其他已调用的方法不在该方法的允许组中
 * 2. 如果存在这样的方法，则抛出异常阻止当前方法的调用
 * 3. 否则，允许调用该方法
 * <p>
 * 注意：该注解不能与 {@link MethodMutexGroup} 或 {@link MethodExclusive} 同时使用。
 * 
 * @see com.hxuanyu.jdolt.util.validator.MethodConstraintValidator
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodAllowGroup {
    /**
     * 允许与当前方法共存的方法名称数组。
     * <p>
     * 当前方法只能与这些指定的方法一起调用，如果已经调用了不在此列表中的其他方法，
     * 则当前方法的调用会被拒绝。
     * 
     * @return 允许共存的方法名称数组
     */
    String[] value();
}
