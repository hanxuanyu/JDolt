package com.hxuanyu.jdolt.util;

import com.hxuanyu.jdolt.interfaces.DoltFunction;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.model.FunctionResult;
import com.hxuanyu.jdolt.model.ProcedureResult;

public abstract class AbstractFunctionParamBuilder<T extends AbstractFunctionParamBuilder<T>> extends AbstractParamBuilder{

    protected DoltFunction<? extends AbstractFunctionParamBuilder<T>> doltFunction;

    protected AbstractFunctionParamBuilder(Class<?> clazz, DoltFunction<? extends AbstractFunctionParamBuilder<T>> doltFunction) {
        super(clazz);
        this.doltFunction = doltFunction;
    }


    @Override
    public FunctionResult execute() {
        validator.checkRequired();
        return doltFunction.invoke(this.toProcedureArgs());
    }

}
