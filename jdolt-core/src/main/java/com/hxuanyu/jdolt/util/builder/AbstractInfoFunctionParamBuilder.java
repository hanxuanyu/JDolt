package com.hxuanyu.jdolt.util.builder;

import com.hxuanyu.jdolt.interfaces.DoltInfoFunction;
import com.hxuanyu.jdolt.model.SqlExecuteResult;

public abstract class AbstractInfoFunctionParamBuilder<T extends AbstractInfoFunctionParamBuilder<T>> extends AbstractParamBuilder {

    protected DoltInfoFunction<? extends AbstractInfoFunctionParamBuilder<T>> doltInfoFunction;

    protected AbstractInfoFunctionParamBuilder(Class<?> clazz, DoltInfoFunction<? extends AbstractInfoFunctionParamBuilder<T>> doltInfoFunction) {
        super(clazz);
        this.doltInfoFunction = doltInfoFunction;
    }


    @Override
    public SqlExecuteResult execute() {
        checkParam();
        return doltInfoFunction.invoke(this.toProcedureArgs());
    }

}
