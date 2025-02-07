package com.hxuanyu.jdolt.core.function;

import com.hxuanyu.jdolt.interfaces.DoltFunction;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.AbstractFunctionParamBuilder;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;

import java.util.concurrent.ConcurrentHashMap;

public class ActiveBranch extends DoltRepository implements DoltFunction<ActiveBranch.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, ActiveBranch> INSTANCES = new ConcurrentHashMap<>();

    private ActiveBranch(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static ActiveBranch getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new ActiveBranch(connectionManager));
    }

    public static class Params extends AbstractFunctionParamBuilder<Params> {

        protected Params(DoltFunction<Params> doltFunction) {
            super(Params.class, doltFunction);
        }

    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getFunctionTemplate("active_branch"), params);
    }

}
