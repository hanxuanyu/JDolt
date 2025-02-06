package com.hxuanyu.jdolt.interfaces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface ParamBuilder<T extends ParamBuilder<T>> {
    // flags 属性由接口维护
    List<String> flags = new ArrayList<>();

    // 默认方法：添加单个参数
    default T addFlag(String flag) {
        flags.add(flag);
        return self();
    }

    // 默认方法：添加多个参数
    default T addFlags(String... flags) {
        this.flags.addAll(Arrays.asList(flags));
        return self();
    }

    // 默认方法：获取所有参数
    default String[] toProcedureArgs() {
        return flags.toArray(new String[0]);
    }

    // 子类实现：返回自身类型
    T self();
}