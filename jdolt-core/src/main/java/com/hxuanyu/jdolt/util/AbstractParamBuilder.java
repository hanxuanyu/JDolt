package com.hxuanyu.jdolt.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractParamBuilder<T extends AbstractParamBuilder<T>> {
    protected final MethodConstraintValidator validator;

    protected AbstractParamBuilder(Class<?> clazz) {
        this.validator = new MethodConstraintValidator(clazz);
    }

    // flags 属性由接口维护
    List<String> flags = new ArrayList<>();

    // 默认方法：添加单个参数
    protected void addFlag(String flag) {
        flags.add(flag);
    }

    // 默认方法：添加多个参数
    protected void addFlags(String... flags) {
        this.flags.addAll(Arrays.asList(flags));
    }

    // 默认方法：获取所有参数
    public String[] toProcedureArgs() {
        return flags.toArray(new String[0]);
    }

}
