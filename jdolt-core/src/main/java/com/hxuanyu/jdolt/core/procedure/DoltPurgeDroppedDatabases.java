package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;
import com.hxuanyu.jdolt.util.builder.AbstractProcedureParamBuilder;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ## `DOLT_PURGE_DROPPED_DATABASES()`
 * <p>
 * 永久删除存储在临时保存区域中的已删除数据库。当一个 Dolt 数据库被删除时，它会被移动到一个临时保存区域，
 * 在该区域中可以通过 {@code dolt_undrop()} 恢复它。`dolt_purge_dropped_databases()` 存储过程会清空该保存区域，
 * 并永久删除这些数据库中的所有数据。此操作不可逆，因此调用者在使用时应谨慎。使用此函数的主要好处是回收临时保存区域占用的磁盘空间。
 * <p>
 * 由于这是一个破坏性操作，调用者必须拥有 `SUPER` 权限才能执行。
 * <p>
 * ### 示例
 * <p>
 * {@code
 * -- 创建一个数据库并在工作集中填充一个表
 * CREATE DATABASE database1;
 * use database1;
 * create table t(pk int primary key);
 * <p>
 * -- 删除数据库会将其移动到临时保存区域
 * DROP DATABASE database1;
 * <p>
 * -- 此时，可以通过调用 dolt_undrop('database1') 恢复该数据库，但
 * -- 我们选择通过调用 dolt_purge_dropped_databases() 永久删除它。
 * CALL dolt_purge_dropped_databases();
 * }
 */
public class DoltPurgeDroppedDatabases extends DoltRepository implements DoltProcedure<DoltPurgeDroppedDatabases.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltPurgeDroppedDatabases> INSTANCES = new ConcurrentHashMap<>();

    private DoltPurgeDroppedDatabases(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltPurgeDroppedDatabases getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltPurgeDroppedDatabases(connectionManager));
    }

    public static class Params extends AbstractProcedureParamBuilder<Params> {

        protected Params(DoltProcedure<Params> doltProcedure) {
            super(Params.class, doltProcedure);
        }

    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getProcedureTemplate("dolt_purge_dropped_databases"), params);
    }

}