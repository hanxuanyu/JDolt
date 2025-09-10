package com.hxuanyu.jdolt.util.builder;

import com.hxuanyu.jdolt.model.SqlExecuteResult;
import com.hxuanyu.jdolt.util.validator.MethodConstraintValidator;

import java.util.*;

public abstract class AbstractParamBuilder {
    
    public enum ParamType {
        TABLE_NAME,
        COLUMN_LIST,
        WHERE_CONDITION,
        ORDER_BY,
        GROUP_BY,
        HAVING,
        LIMIT,
        OFFSET,
        JOIN,
        PROCEDURE_PARAMS,
        FUNCTION_PARAMS,
        TABLE_FUNCTION_PARAMS,
        SCHEMA_NAME,
        DATABASE_NAME,
        DISTINCT,
        UNION,
        UNION_ALL,
        VALUES,
        SET_CLAUSE,
        INTO_CLAUSE,
        FLAGS,
    }
    
    protected final MethodConstraintValidator validator;

    public AbstractParamBuilder(Class<?> clazz) {
        this.validator = new MethodConstraintValidator(clazz);
    }


    // params 属性由接口维护，保存了一系列键值对
    Map<ParamType, List<String>> sqlParams = new HashMap<>();

    public void addParam(ParamType type, String param) {
        sqlParams.computeIfAbsent(type, k -> new ArrayList<>()).add(param);
    }

    public void addParams(ParamType type, String... params) {
        sqlParams.computeIfAbsent(type, k -> new ArrayList<>()).addAll(List.of(params));
    }


    public void checkParam() {
        validator.checkRequired();
    }

    public abstract SqlExecuteResult execute();
}
