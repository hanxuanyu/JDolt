package com.hxuanyu.jdolt.interfaces;

import com.hxuanyu.jdolt.model.SqlExecuteResult;
import com.hxuanyu.jdolt.util.builder.AbstractTableFunctionParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;

import java.util.List;
import java.util.Map;

/**
 * Dolt内置函数接口定义，dolt内置函数应实现本接口定义的方法，对于通用的invoke方法， 可以使用默认实现或根据需要进行重写
 *
 * @author hanxuanyu
 * @version 1.0
 */
public interface DoltTableFunction<T extends AbstractTableFunctionParamBuilder<T>> {


    default boolean invokeWithResult(String... params) {
        return commonDoltExecute(buildSqlTemplate(params));
    }


    default SqlExecuteResult invoke(String... params) {
        List<Map<String, Object>> resultMaps = executeQueryAsList(buildSqlTemplate(params));
        if (resultMaps != null && !resultMaps.isEmpty()) {
            return SqlExecuteResult.success("success", resultMaps);
        } else {
            return SqlExecuteResult.failed("failed");
        }
    }

    boolean commonDoltExecute(SqlBuilder.SqlTemplate sqlTemplate);

    List<Map<String, Object>> executeQueryAsList(SqlBuilder.SqlTemplate sqlTemplate);

    AbstractTableFunctionParamBuilder<T> prepare();

    SqlBuilder.SqlTemplate buildSqlTemplate(String... params);

}
