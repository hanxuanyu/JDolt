package com.hxuanyu.jdolt.core.function.info;

import com.hxuanyu.jdolt.interfaces.DoltInfoFunction;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractInfoFunctionParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;

import java.util.concurrent.ConcurrentHashMap;

public class ActiveBranch extends DoltRepository implements DoltInfoFunction<ActiveBranch.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, ActiveBranch> INSTANCES = new ConcurrentHashMap<>();

    private ActiveBranch(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static ActiveBranch getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new ActiveBranch(connectionManager));
    }

    public static class Params extends AbstractInfoFunctionParamBuilder<Params> {

        protected Params(DoltInfoFunction<Params> doltInfoFunction) {
            super(Params.class, doltInfoFunction);
        }

    }

    @Override
    public Params prepare() {
        return new Params(this);
    }



    @Override
    public SqlBuilder.SqlTemplate buildSqlTemplate(String... params) {
        return SqlBuilder.selectFunction("active_branch")
                .withParams(params)
                .build();
    }
}
