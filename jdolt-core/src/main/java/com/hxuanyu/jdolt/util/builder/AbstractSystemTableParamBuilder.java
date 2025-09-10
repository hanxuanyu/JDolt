package com.hxuanyu.jdolt.util.builder;

import com.hxuanyu.jdolt.interfaces.DoltSystemTable;
import com.hxuanyu.jdolt.model.SqlExecuteResult;

public abstract class AbstractSystemTableParamBuilder<T extends AbstractSystemTableParamBuilder<T>> extends AbstractParamBuilder {

    protected DoltSystemTable<? extends AbstractSystemTableParamBuilder<T>> doltSystemTable;

    protected AbstractSystemTableParamBuilder(Class<?> clazz, DoltSystemTable<? extends AbstractSystemTableParamBuilder<T>> doltSystemTable) {
        super(clazz);
        this.doltSystemTable = doltSystemTable;
    }


    @Override
    public SqlExecuteResult execute() {
        checkParam();
        return doltSystemTable.query(this.sqlParams);
    }

}
