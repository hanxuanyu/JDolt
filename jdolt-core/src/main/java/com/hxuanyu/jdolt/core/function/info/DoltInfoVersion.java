package com.hxuanyu.jdolt.core.function.info;

import com.hxuanyu.jdolt.interfaces.DoltInfoFunction;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractInfoFunctionParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;

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
public class DoltInfoVersion extends DoltRepository implements DoltInfoFunction<DoltInfoVersion.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltInfoVersion> INSTANCES = new ConcurrentHashMap<>();

    private DoltInfoVersion(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltInfoVersion getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltInfoVersion(connectionManager));
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
        return SqlBuilder.selectFunction("dolt_version")
                .withParams(params)
                .build();
    }
}