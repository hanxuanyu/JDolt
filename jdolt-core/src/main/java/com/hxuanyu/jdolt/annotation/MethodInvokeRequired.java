package com.hxuanyu.jdolt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注方法必须被调用。
 * <p>
 * 该注解用于标记必须在某个操作流程中被调用的方法。在验证过程结束时（通常是在调用
 * {@link com.hxuanyu.jdolt.util.validator.MethodConstraintValidator#checkRequired()}方法时），
 * 系统会检查所有标记了该注解的方法是否都已被调用，如果有未被调用的方法，则会抛出异常。
 * <p>
 * 处理逻辑：
 * 1. 在验证过程结束时，系统会检查所有标记了该注解的方法
 * 2. 如果有标记了该注解的方法未被调用，则抛出异常
 * <p>
 * 注意：该注解与 {@link MethodInvokeRequiredGroup} 的区别在于，前者应用于单个方法，
 * 而后者应用于类级别并可以指定一组方法中的部分或全部必须被调用。
 * 
 * @see com.hxuanyu.jdolt.util.validator.MethodConstraintValidator
 * @see MethodInvokeRequiredGroup
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodInvokeRequired {
}
