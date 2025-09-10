package com.hxuanyu.jdolt.util.builder;

import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.model.SqlExecuteResult;

import java.util.List;

public abstract class AbstractProcedureParamBuilder<T extends AbstractProcedureParamBuilder<T>> extends AbstractParamBuilder{

    protected DoltProcedure<? extends AbstractProcedureParamBuilder<T>> doltProcedure;

    protected AbstractProcedureParamBuilder(Class<?> clazz, DoltProcedure<? extends AbstractProcedureParamBuilder<T>> doltProcedure) {
        super(clazz);
        this.doltProcedure = doltProcedure;
    }

    public void addFlag(String flag) {
        addParam(ParamType.PROCEDURE_PARAMS, flag);
    }

    public void addFlags(String... flags) {
        addParams(ParamType.PROCEDURE_PARAMS, flags);
    }


    @Override
    public SqlExecuteResult execute() {
        checkParam();
        List<String> procedureParams = this.sqlParams.get(ParamType.PROCEDURE_PARAMS);
        if (procedureParams == null || procedureParams.isEmpty()) {
            return doltProcedure.call();
        } else {
            return doltProcedure.call(procedureParams.toArray(new String[0]));
        }
    }

}
