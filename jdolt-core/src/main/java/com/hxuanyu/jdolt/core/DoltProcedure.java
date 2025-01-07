package com.hxuanyu.jdolt.core;

import com.hxuanyu.jdolt.model.ProcedureResult;

/**
 * TODO
 *
 * @author hanxuanyu
 * @version 1.0
 */
public interface DoltProcedure {

    <T> ProcedureResult<T> call(Class<T> resultClass, String... params);

    boolean call(String... params);
}
