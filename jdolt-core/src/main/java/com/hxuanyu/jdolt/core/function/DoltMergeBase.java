package com.hxuanyu.jdolt.core.function;

import com.hxuanyu.jdolt.annotation.MethodExclusive;
import com.hxuanyu.jdolt.annotation.MethodInvokeRequired;
import com.hxuanyu.jdolt.interfaces.DoltFunction;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.AbstractFunctionParamBuilder;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;

import java.util.concurrent.ConcurrentHashMap;

public class DoltMergeBase extends DoltRepository implements DoltFunction<DoltMergeBase.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltMergeBase> INSTANCES = new ConcurrentHashMap<>();

    private DoltMergeBase(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltMergeBase getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltMergeBase(connectionManager));
    }

    public static class Params extends AbstractFunctionParamBuilder<Params> {

        protected Params(DoltFunction<Params> doltFunction) {
            super(Params.class, doltFunction);
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
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getFunctionTemplate("dolt_merge_base"), params);
    }

}