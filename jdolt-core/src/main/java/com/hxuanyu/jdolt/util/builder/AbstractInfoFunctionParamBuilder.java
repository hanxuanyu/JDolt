package com.hxuanyu.jdolt.util.builder;

import com.hxuanyu.jdolt.interfaces.DoltInfoFunction;
import com.hxuanyu.jdolt.model.SqlExecuteResult;

import java.util.List;

public abstract class AbstractInfoFunctionParamBuilder<T extends AbstractInfoFunctionParamBuilder<T>> extends AbstractParamBuilder {

    protected DoltInfoFunction<? extends AbstractInfoFunctionParamBuilder<T>> doltInfoFunction;

    protected AbstractInfoFunctionParamBuilder(Class<?> clazz, DoltInfoFunction<? extends AbstractInfoFunctionParamBuilder<T>> doltInfoFunction) {
        super(clazz);
        this.doltInfoFunction = doltInfoFunction;
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
        List<String> functionParams = this.sqlParams.get(ParamType.FUNCTION_PARAMS);
        if (functionParams == null || functionParams.isEmpty()) {
            return doltInfoFunction.invoke();
        } else {
            return doltInfoFunction.invoke(functionParams.toArray(new String[0]));
        }
    }

}
