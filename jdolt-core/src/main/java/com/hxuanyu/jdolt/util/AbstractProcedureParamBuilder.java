package com.hxuanyu.jdolt.util;

import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.model.ProcedureResult;

public abstract class AbstractProcedureParamBuilder<T extends AbstractProcedureParamBuilder<T>> extends AbstractParamBuilder{

    protected DoltProcedure<? extends AbstractProcedureParamBuilder<T>> doltProcedure;

    protected AbstractProcedureParamBuilder(Class<?> clazz, DoltProcedure<? extends AbstractProcedureParamBuilder<T>> doltProcedure) {
        super(clazz);
        this.doltProcedure = doltProcedure;
    }


    @Override
    public ProcedureResult execute() {
        validator.checkRequired();
        return doltProcedure.call(this.toProcedureArgs());
    }

}
