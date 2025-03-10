package com.hxuanyu.jdolt.interfaces;

import com.hxuanyu.jdolt.model.SqlExecuteResult;
import com.hxuanyu.jdolt.util.AbstractProcedureParamBuilder;

import java.util.List;
import java.util.Map;

/**
 * Dolt存储过程接口定义，dolt存储过程应实现本接口定义的方法，对于通用的call方法， 可以使用默认实现或根据需要进行重写
 *
 * @author hanxuanyu
 * @version 1.0
 */
public interface DoltProcedure<T extends AbstractProcedureParamBuilder<T>> {


    default boolean callWithResult(String... params) {
        return commonDoltExecute(buildSql(params), params);
    }


    default SqlExecuteResult call(String... params) {
        List<Map<String, Object>> resultMaps = executeQueryAsList(buildSql(params), params);
        if (resultMaps != null && !resultMaps.isEmpty()) {
            return SqlExecuteResult.success("success", resultMaps);
        } else {
            return SqlExecuteResult.failed("failed");
        }
    }

    boolean commonDoltExecute(String sql, String... params);

    List<Map<String, Object>> executeQueryAsList(String sql, String... params);

    AbstractProcedureParamBuilder<T> prepare();

    String buildSql(String... params);

}
