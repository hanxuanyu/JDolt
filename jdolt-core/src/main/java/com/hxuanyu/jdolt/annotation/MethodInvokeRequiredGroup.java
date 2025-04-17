package com.hxuanyu.jdolt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标注类中一个或多个方法必须被调用。
 * <p>
 * 该注解应用于类级别，用于指定类中的一组方法必须被调用。可以通过 allRequired 参数
 * 控制是要求所有指定的方法都必须调用，还是只要求至少一个方法被调用。
 * <p>
 * 处理逻辑：
 * 1. 在验证过程结束时，系统会检查该注解指定的方法组
 * 2. 如果 allRequired=true（默认值），则要求所有指定的方法都必须已被调用
 * 3. 如果 allRequired=false，则要求至少一个指定的方法已被调用
 * 4. 如果条件不满足，则抛出异常
 * <p>
 * 注意：该注解与 {@link MethodInvokeRequired} 的区别在于，前者应用于单个方法，
 * 而本注解应用于类级别并可以指定一组方法中的部分或全部必须被调用。
 * 
 * @see com.hxuanyu.jdolt.util.validator.MethodConstraintValidator
 * @see MethodInvokeRequired
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MethodInvokeRequiredGroup {
    /**
     * 必须调用的方法名数组。
     * <p>
     * 这些方法是否都必须被调用取决于 allRequired 参数。
     * 
     * @return 必须调用的方法名数组
     */
    String[] value();

    /**
     * 是否要求所有指定的方法都必须调用。
     * <p>
     * 如果设置为 true（默认值），则所有指定的方法都必须被调用。
     * 如果设置为 false，则至少需要一个指定的方法被调用。
     * 
     * @return 是否要求所有方法都必须调用
     */
    boolean allRequired() default true;
}
