package com.hxuanyu.jdolt.core.function;

import com.hxuanyu.jdolt.annotation.MethodExclusive;
import com.hxuanyu.jdolt.annotation.MethodInvokeRequired;
import com.hxuanyu.jdolt.interfaces.DoltInfoFunction;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractFunctionParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;

import java.util.concurrent.ConcurrentHashMap;

public class DoltInfoMergeBase extends DoltRepository implements DoltInfoFunction<DoltInfoMergeBase.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltInfoMergeBase> INSTANCES = new ConcurrentHashMap<>();

    private DoltInfoMergeBase(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltInfoMergeBase getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltInfoMergeBase(connectionManager));
    }

    public static class Params extends AbstractFunctionParamBuilder<Params> {

        protected Params(DoltInfoFunction<Params> doltInfoFunction) {
            super(Params.class, doltInfoFunction);
        }

        @MethodExclusive
        @MethodInvokeRequired
        public Params withBranch(String branchA, String branchB) {
            validator.checkAndMark("withBranch");
            addFlags(branchA, branchB);
            return this;
        }


    }

    @Override
    public Params prepare() {
        return new Params(this);
    }



    @Override
    public SqlBuilder.SqlTemplate buildSqlTemplate(String... params) {
        return SqlBuilder.selectFunction("dolt_merge_base")
                .withParams(params)
                .build();
    }
}