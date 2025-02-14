package com.hxuanyu.jdolt.core.function;

import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.interfaces.DoltFunction;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.AbstractFunctionParamBuilder;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;

import java.util.concurrent.ConcurrentHashMap;

/**
 * DOLT_HASHOF() 函数返回某个分支或其他提交规范的提交哈希值。
 */
public class DoltHashOf extends DoltRepository implements DoltFunction<DoltHashOf.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltHashOf> INSTANCES = new ConcurrentHashMap<>();

    private DoltHashOf(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltHashOf getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltHashOf(connectionManager));
    }

    public static class Params extends AbstractFunctionParamBuilder<Params> {

        protected Params(DoltFunction<Params> doltFunction) {
            super(Params.class, doltFunction);
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
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getFunctionTemplate("dolt_hashof"), params);
    }

}