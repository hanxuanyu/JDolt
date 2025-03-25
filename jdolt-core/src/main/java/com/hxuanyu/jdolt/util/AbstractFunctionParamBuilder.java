package com.hxuanyu.jdolt.util;

import com.hxuanyu.jdolt.interfaces.DoltInfoFunction;
import com.hxuanyu.jdolt.model.SqlExecuteResult;

public abstract class AbstractFunctionParamBuilder<T extends AbstractFunctionParamBuilder<T>> extends AbstractParamBuilder{

    protected DoltInfoFunction<? extends AbstractFunctionParamBuilder<T>> doltInfoFunction;

    protected AbstractFunctionParamBuilder(Class<?> clazz, DoltInfoFunction<? extends AbstractFunctionParamBuilder<T>> doltInfoFunction) {
        super(clazz);
        this.doltInfoFunction = doltInfoFunction;
    }


    @Override
    public SqlExecuteResult execute() {
        checkParam();
        return doltInfoFunction.invoke(this.toProcedureArgs());
    }

}
