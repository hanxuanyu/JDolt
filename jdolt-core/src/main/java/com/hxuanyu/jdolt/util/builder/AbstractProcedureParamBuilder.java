package com.hxuanyu.jdolt.util.builder;

import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.model.SqlExecuteResult;

public abstract class AbstractProcedureParamBuilder<T extends AbstractProcedureParamBuilder<T>> extends AbstractParamBuilder{

    protected DoltProcedure<? extends AbstractProcedureParamBuilder<T>> doltProcedure;

    protected AbstractProcedureParamBuilder(Class<?> clazz, DoltProcedure<? extends AbstractProcedureParamBuilder<T>> doltProcedure) {
        super(clazz);
        this.doltProcedure = doltProcedure;
    }


    @Override
    public SqlExecuteResult execute() {
        checkParam();
        return doltProcedure.call(this.toProcedureArgs());
    }

}
