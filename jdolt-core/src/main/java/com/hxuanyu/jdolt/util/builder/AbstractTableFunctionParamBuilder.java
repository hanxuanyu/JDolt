package com.hxuanyu.jdolt.util.builder;

import com.hxuanyu.jdolt.interfaces.DoltTableFunction;
import com.hxuanyu.jdolt.model.SqlExecuteResult;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractTableFunctionParamBuilder<T extends AbstractTableFunctionParamBuilder<T>> extends AbstractParamBuilder {

    protected DoltTableFunction<? extends AbstractTableFunctionParamBuilder<T>> doltFunction;

    protected AbstractTableFunctionParamBuilder(Class<?> clazz, DoltTableFunction<? extends AbstractTableFunctionParamBuilder<T>> doltFunction) {
        super(clazz);
        this.doltFunction = doltFunction;
    }

    public void addFlag(String flag) {
        addParam(ParamType.FUNCTION_PARAMS, flag);
    }

    public void addFlags(String... flags) {
        addParams(ParamType.FUNCTION_PARAMS, flags);
    }


    @Override
    public SqlExecuteResult execute() {
        checkParam();
        List<Object> functionParams = this.sqlParams.get(ParamType.FUNCTION_PARAMS);
        if (functionParams == null || functionParams.isEmpty()) {
            return doltFunction.invoke();
        } else {
            return doltFunction.invoke(convertToStringArray(functionParams));
        }
    }

}
