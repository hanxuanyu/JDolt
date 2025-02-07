package com.hxuanyu.jdolt.interfaces;

import com.hxuanyu.jdolt.model.FunctionResult;
import com.hxuanyu.jdolt.util.AbstractFunctionParamBuilder;


import java.util.List;
import java.util.Map;

/**
 * Dolt内置函数接口定义，dolt内置函数应实现本接口定义的方法，对于通用的invoke方法， 可以使用默认实现或根据需要进行重写
 *
 * @author hanxuanyu
 * @version 1.0
 */
public interface DoltFunction<T extends AbstractFunctionParamBuilder<T>> {


    default boolean invokeWithResult(String... params) {
        return commonDoltExecute(buildSql(params), params);
    }


    default FunctionResult invoke(String... params) {
        List<Map<String, Object>> resultMaps = executeQueryAsList(buildSql(params), params);
        if (resultMaps != null && !resultMaps.isEmpty()) {
            return FunctionResult.success("success", resultMaps);
        } else {
            return FunctionResult.failed("failed");
        }
    }

    boolean commonDoltExecute(String sql, String... params);

    List<Map<String, Object>> executeQueryAsList(String sql, String... params);

    AbstractFunctionParamBuilder<T> prepare();

    String buildSql(String... params);

}
