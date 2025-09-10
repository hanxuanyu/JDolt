package com.hxuanyu.jdolt.core.systemtable;

import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractParamBuilder;
import com.hxuanyu.jdolt.util.builder.AbstractSystemTableParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;
import com.hxuanyu.jdolt.model.WhereCondition;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DoltSystemTable extends DoltRepository implements com.hxuanyu.jdolt.interfaces.DoltSystemTable<DoltSystemTable.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltSystemTable> INSTANCES = new ConcurrentHashMap<>();

    protected DoltSystemTable(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltSystemTable getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltSystemTable(connectionManager));
    }

    public static class Params extends AbstractSystemTableParamBuilder<Params> {

        protected Params(com.hxuanyu.jdolt.interfaces.DoltSystemTable<Params> doltTableFunction) {
            super(Params.class, doltTableFunction);
        }

        public Params from(String tableName) {
            addParam(ParamType.TABLE_NAME, tableName);
            return this;
        }

        /**
         * 添加WHERE条件
         * @param whereCondition WHERE条件对象
         * @return Params实例
         */
        public Params where(WhereCondition whereCondition) {
            addParam(ParamType.WHERE_CONDITION, whereCondition);
            return this;
        }

        /**
         * 添加ORDER BY子句（重写父类方法以返回正确类型）
         * @param columns 排序列名
         * @return Params实例
         */
        public Params orderBy(String... columns) {
            for (String column : columns) {
                addParam(ParamType.ORDER_BY, column);
            }
            return this;
        }

        public Params limit(int limit) {
            addParam(ParamType.LIMIT, limit);
            return this;
        }

        public Params offset(int offset) {
            addParam(ParamType.OFFSET, offset);
            return this;
        }

        public Params limit(String limit) {
            addParam(ParamType.LIMIT, limit);
            return this;
        }

        public Params offset(String offset) {
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
        if (tableNames != null && !tableNames.isEmpty()) {
            builder.from(tableNames.get(0).toString());
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