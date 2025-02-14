package com.hxuanyu.jdolt.core.function;

import com.hxuanyu.jdolt.interfaces.DoltFunction;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.AbstractFunctionParamBuilder;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;

import java.util.concurrent.ConcurrentHashMap;


/**
 * `DOLT_VERSION()`
 * <p>
 * `DOLT_VERSION()` 函数返回 Dolt 二进制文件的版本字符串。
 *
 * <pre>
 * mysql> select dolt_version();
 * +----------------+
 * | dolt_version() |
 * +----------------+
 * | 0.40.4         |
 * +----------------+
 * </pre>
 */
public class DoltVersion extends DoltRepository implements DoltFunction<DoltVersion.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltVersion> INSTANCES = new ConcurrentHashMap<>();

    private DoltVersion(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltVersion getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltVersion(connectionManager));
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
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getFunctionTemplate("dolt_version"), params);
    }

}