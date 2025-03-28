package com.hxuanyu.jdolt.core.function.info;

import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.interfaces.DoltInfoFunction;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractInfoFunctionParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;

import java.util.concurrent.ConcurrentHashMap;

/**
 * DOLT_HASHOF() 函数返回某个分支或其他提交规范的提交哈希值。
 */
public class DoltInfoHashOf extends DoltRepository implements DoltInfoFunction<DoltInfoHashOf.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltInfoHashOf> INSTANCES = new ConcurrentHashMap<>();

    private DoltInfoHashOf(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltInfoHashOf getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltInfoHashOf(connectionManager));
    }

    public static class Params extends AbstractInfoFunctionParamBuilder<Params> {

        protected Params(DoltInfoFunction<Params> doltInfoFunction) {
            super(Params.class, doltInfoFunction);
        }

        @MethodMutexGroup({"withBranch"})
        public Params withBranch(String branch) {
            validator.checkAndMark("withBranch");
            addFlags(branch);
            return this;
        }


    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public SqlBuilder.SqlTemplate buildSqlTemplate(String... params) {
        return SqlBuilder.selectFunction("dolt_hashof")
                .withParams(params)
                .build();
    }

}