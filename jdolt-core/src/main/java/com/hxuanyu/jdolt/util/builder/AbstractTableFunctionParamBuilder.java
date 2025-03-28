package com.hxuanyu.jdolt.util.builder;

import com.hxuanyu.jdolt.interfaces.DoltTableFunction;
import com.hxuanyu.jdolt.model.SqlExecuteResult;

public abstract class AbstractTableFunctionParamBuilder<T extends AbstractTableFunctionParamBuilder<T>> extends AbstractParamBuilder {

    protected DoltTableFunction<? extends AbstractTableFunctionParamBuilder<T>> doltFunction;

    protected AbstractTableFunctionParamBuilder(Class<?> clazz, DoltTableFunction<? extends AbstractTableFunctionParamBuilder<T>> doltFunction) {
        super(clazz);
        this.doltFunction = doltFunction;
    }


    @Override
    public SqlExecuteResult execute() {
        checkParam();
        return doltFunction.invoke(this.toProcedureArgs());
    }

}
