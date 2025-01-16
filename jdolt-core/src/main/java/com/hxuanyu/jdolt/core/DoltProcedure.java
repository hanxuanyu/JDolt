package com.hxuanyu.jdolt.core;

import com.hxuanyu.jdolt.constant.DoltSqlTemplate;
import com.hxuanyu.jdolt.model.ProcedureResult;

/**
 * TODO
 *
 * @author hanxuanyu
 * @version 1.0
 */
public interface DoltProcedure {


    default boolean call(String... params) {
        return commonDoltExecute(DoltSqlTemplate.buildAddSql(params), params);
    }


    default <T> ProcedureResult<T> call(Class<T> resultClass, String... params) {
        if (call(params)) {
            return ProcedureResult.success();
        } else {
            return ProcedureResult.failed();
        }
    }

    boolean commonDoltExecute(String sql, String... params);

}
