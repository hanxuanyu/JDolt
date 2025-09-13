package com.hxuanyu.jdolt.core.systemtable;

import com.hxuanyu.jdolt.annotation.MethodInvokeRequired;
import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.model.WhereCondition;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractParamBuilder;
import com.hxuanyu.jdolt.util.builder.AbstractSystemTableParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DoltSystemTableWithSuffix extends DoltRepository implements com.hxuanyu.jdolt.interfaces.DoltSystemTable<DoltSystemTableWithSuffix.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltSystemTableWithSuffix> INSTANCES = new ConcurrentHashMap<>();

    protected DoltSystemTableWithSuffix(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltSystemTableWithSuffix getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltSystemTableWithSuffix(connectionManager));
    }

    public static class Params extends AbstractSystemTableParamBuilder<Params> {




        protected Params(com.hxuanyu.jdolt.interfaces.DoltSystemTable<Params> doltTableFunction) {
            super(Params.class, doltTableFunction);
        }

        @MethodInvokeRequired
        @MethodMutexGroup({"withTableNameSuffix"})
        public Params withTableNameSuffix(String tableName){
            validator.checkAndMark("withTableNameSuffix");
            addParam(ParamType.TABLE_NAME_SUFFIX, tableName);
            return this;
        }

        @MethodInvokeRequired
        @MethodMutexGroup({"from"})
        public Params from(String tableName) {
            validator.checkAndMark("from");
            addParam(ParamType.TABLE_NAME, tableName);
            return this;
        }

        /**
         * 添加WHERE条件
         * @param whereCondition WHERE条件对象
         * @return Params实例
         */
        public Params where(WhereCondition whereCondition) {
            validator.checkAndMark("where");
            addParam(ParamType.WHERE_CONDITION, whereCondition);
            return this;
        }

        /**
         * 添加ORDER BY子句（重写父类方法以返回正确类型）
         * @param columns 排序列名
         * @return Params实例
         */
        @MethodMutexGroup("orderBy")
        public Params orderBy(String... columns) {
            validator.checkAndMark("orderBy");
            for (String column : columns) {
                addParam(ParamType.ORDER_BY, column);
            }
            return this;
        }

        @MethodMutexGroup("limit")
        public Params limit(int limit) {
            validator.checkAndMark("limit");
            addParam(ParamType.LIMIT, limit);
            return this;
        }

        @MethodMutexGroup("offset")
        public Params offset(int offset) {
            validator.checkAndMark("offset");
            addParam(ParamType.OFFSET, offset);
            return this;
        }

        @MethodMutexGroup("limit")
        public Params limit(String limit) {
            validator.checkAndMark("limit");
            addParam(ParamType.LIMIT, limit);
            return this;
        }

        @MethodMutexGroup("offset")
        public Params offset(String offset) {
            validator.checkAndMark("offset");
            addParam(ParamType.OFFSET, offset);
            return this;
        }



    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public SqlBuilder.SqlTemplate buildSqlTemplate(Map<AbstractParamBuilder.ParamType, List<Object>> params) {
        SqlBuilder builder = SqlBuilder.select();

        // 处理FROM子句
        List<Object> tableNames = params.get(AbstractParamBuilder.ParamType.TABLE_NAME);
        List<Object> tableNameSuffix = params.get(AbstractParamBuilder.ParamType.TABLE_NAME_SUFFIX);
        if (tableNames != null && !tableNames.isEmpty() && tableNameSuffix != null && !tableNameSuffix.isEmpty()) {
            builder.from(tableNames.get(0).toString() + "_" + tableNameSuffix.get(0).toString());
        }

        // 处理WHERE条件
        List<Object> whereConditions = params.get(AbstractParamBuilder.ParamType.WHERE_CONDITION);
        if (whereConditions != null) {
            for (Object condition : whereConditions) {
                if (condition instanceof WhereCondition) {
                    builder.where((WhereCondition) condition);
                }
            }
        }

        // 处理ORDER BY子句
        List<Object> orderByColumns = params.get(AbstractParamBuilder.ParamType.ORDER_BY);
        if (orderByColumns != null && !orderByColumns.isEmpty()) {
            String[] columns = orderByColumns.stream()
                    .map(Object::toString)
                    .toArray(String[]::new);
            builder.orderBy(columns);
        }

        // 处理LIMIT子句
        List<Object> limits = params.get(AbstractParamBuilder.ParamType.LIMIT);
        if (limits != null && !limits.isEmpty()) {
            try {
                int limit = Integer.parseInt(limits.get(0).toString());
                builder.limit(limit);
            } catch (NumberFormatException e) {
                // 忽略无效的LIMIT值
            }
        }

        // 处理OFFSET子句
        List<Object> offsets = params.get(AbstractParamBuilder.ParamType.OFFSET);
        if (offsets != null && !offsets.isEmpty()) {
            try {
                int offset = Integer.parseInt(offsets.get(0).toString());
                builder.offset(offset);
            } catch (NumberFormatException e) {
                // 忽略无效的OFFSET值
            }
        }

        return builder.build();
    }

}