package com.hxuanyu.jdolt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于定义方法的依赖关系。
 * <p>
 * 该注解指定当前方法依赖于其他方法，即当前方法只能在其依赖的方法被调用后才能被调用。
 * 依赖关系可以配置为要求所有依赖方法都被调用，或者只要求至少一个依赖方法被调用。
 * <p>
 * 处理逻辑：
 * 1. 当方法被调用时，系统会检查其依赖的方法是否已被调用
 * 2. 如果 allRequired=true，则要求所有依赖方法都必须已被调用
 * 3. 如果 allRequired=false，则要求至少一个依赖方法已被调用
 * 4. 如果依赖条件不满足，则抛出异常阻止当前方法的调用
 * 
 * @see com.hxuanyu.jdolt.util.validator.MethodConstraintValidator
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MethodDependsOn {
    /**
     * 当前方法依赖的方法名称数组。
     * <p>
     * 这些方法必须在当前方法被调用前调用，具体要求取决于 allRequired 参数。
     * 
     * @return 依赖的方法名称数组
     */
    String[] value();

    /**
     * 是否需要所有依赖的方法都被调用。
     * <p>
     * 如果设置为 true，则所有依赖方法都必须在当前方法之前被调用。
     * 如果设置为 false（默认值），则至少需要一个依赖方法在当前方法之前被调用。
     * 
     * @return 是否需要所有依赖方法都被调用
     */
    boolean allRequired() default false;
}
