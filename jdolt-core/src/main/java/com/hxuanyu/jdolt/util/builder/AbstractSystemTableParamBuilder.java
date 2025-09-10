package com.hxuanyu.jdolt.util.builder;

import com.hxuanyu.jdolt.interfaces.DoltSystemTable;
import com.hxuanyu.jdolt.model.SqlExecuteResult;

public abstract class AbstractSystemTableParamBuilder<T extends AbstractSystemTableParamBuilder<T>> extends AbstractParamBuilder {

    protected DoltSystemTable<? extends AbstractSystemTableParamBuilder<T>> doltSystemTable;

    protected AbstractSystemTableParamBuilder(Class<?> clazz, DoltSystemTable<? extends AbstractSystemTableParamBuilder<T>> doltSystemTable) {
        super(clazz);
        this.doltSystemTable = doltSystemTable;
    }

    public void table(String tableName) {
        addParam(ParamType.TABLE_NAME, tableName);
    }

    public void columns(String... columns) {
        addParams(ParamType.COLUMN_LIST, columns);
    }

    public void where(String whereCondition) {
        addParam(ParamType.WHERE_CONDITION, whereCondition);
    }

    public void orderBy(String orderBy) {
        addParam(ParamType.ORDER_BY, orderBy);
    }

    public void groupBy(String groupBy) {
        addParam(ParamType.GROUP_BY, groupBy);
    }

    public void limit(int limit) {
        addParam(ParamType.LIMIT, String.valueOf(limit));
    }

    public void offset(int offset) {
        addParam(ParamType.OFFSET, String.valueOf(offset));
    }

    @Override
    public SqlExecuteResult execute() {
        checkParam();
        return doltSystemTable.query(this.sqlParams);
    }

}
