package com.hxuanyu.jdolt.core.function.table;

import com.hxuanyu.jdolt.annotation.MethodDependsOn;
import com.hxuanyu.jdolt.annotation.MethodInvokeRequiredGroup;
import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.interfaces.DoltTableFunction;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractTableFunctionParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;

import java.util.concurrent.ConcurrentHashMap;

@MethodInvokeRequiredGroup(value = {"withTable"})
public class DoltDiff extends DoltRepository implements DoltTableFunction<DoltDiff.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltDiff> INSTANCES = new ConcurrentHashMap<>();

    private DoltDiff(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltDiff getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltDiff(connectionManager));
    }

    public static class Params extends AbstractTableFunctionParamBuilder<Params> {

        protected Params(DoltTableFunction<Params> doltTableFunction) {
            super(Params.class, doltTableFunction);
        }

        @MethodMutexGroup({"withTable"})
        @MethodDependsOn({"toRevision", "twoDot", "threeDot"})
        public Params withTable(String table) {
            validator.checkAndMark("withTable");
            addFlags(table);
            return this;
        }

        @MethodMutexGroup({"fromRevision", "twoDot", "threeDot"})
        public Params fromRevision(String fromRevision) {
            validator.checkAndMark("fromRevision");
            addFlags(fromRevision);
            return this;
        }

        @MethodDependsOn({"fromRevision"})
        public Params toRevision(String toRevision) {
            validator.checkAndMark("toRevision");
            addFlags(toRevision);
            return this;
        }

        @MethodMutexGroup({"fromRevision", "twoDot", "threeDot"})
        public Params twoDot(String from, String to) {
            validator.checkAndMark("twoDot");
            addFlags(from + ".." + to);
            return this;
        }

        @MethodMutexGroup({"fromRevision", "twoDot", "threeDot"})
        public Params threeDot(String from, String to) {
            validator.checkAndMark("threeDot");
            addFlags(from + "..." + to);
            return this;
        }


    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public SqlBuilder.SqlTemplate buildSqlTemplate(String... params) {
        return SqlBuilder.select()
                .fromFunction("dolt_diff")
                .withParams(params)
                .build();
    }

}