package com.hxuanyu.jdolt.interfaces;

import com.hxuanyu.jdolt.model.ProcedureResult;

import java.util.List;
import java.util.Map;

/**
 * TODO
 *
 * @author hanxuanyu
 * @version 1.0
 */
public interface DoltProcedure {


    default boolean callWithResult(String... params) {
        return commonDoltExecute(buildSql(params), params);
    }


    default ProcedureResult call(String... params) {
        List<Map<String, Object>> resultMaps = executeQueryAsList(buildSql(params), params);
        if (resultMaps != null && !resultMaps.isEmpty()) {
            return ProcedureResult.success("success", resultMaps);
        } else {
            return ProcedureResult.failed("failed");
        }
    }

    boolean commonDoltExecute(String sql, String... params);

    List<Map<String, Object>> executeQueryAsList(String sql, String... params);

    String buildSql(String... params);

}
