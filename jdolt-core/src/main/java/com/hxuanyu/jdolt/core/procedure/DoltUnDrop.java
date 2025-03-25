package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;
import com.hxuanyu.jdolt.util.builder.AbstractProcedureParamBuilder;

import java.util.concurrent.ConcurrentHashMap;


/**
 * `DOLT_UNDROP()`
 * <p>
 * 恢复已删除的数据库。有关如何永久删除已删除的数据库，请参阅
 * {@link #dolt_purge_dropped_databases() dolt_purge_dropped_databases()}存储过程。
 *
 * <pre>
 * CALL DOLT_UNDROP(<database_name>);
 * </pre>
 *
 * <h3>选项</h3>
 * <p>
 * `dolt_undrop()` 接受一个参数——要恢复的已删除数据库的名称。如果在没有任何参数的情况下调用
 * `dolt_undrop()`，将返回一条错误消息，其中包含所有可恢复的已删除数据库的列表。
 *
 * <h3>示例</h3>
 *
 * <pre>
 * -- 创建一个数据库并在工作集内填充一个表
 * CREATE DATABASE database1;
 * USE database1;
 * CREATE TABLE t(pk INT PRIMARY KEY);
 *
 * -- 删除数据库会将其移动到一个临时存储区域
 * DROP DATABASE database1;
 *
 * -- 在没有参数的情况下调用 dolt_undrop() 会返回一条错误消息，
 * -- 其中列出了所有可恢复的已删除数据库
 * CALL dolt_undrop();
 *
 * -- 使用 dolt_undrop() 恢复数据库
 * CALL dolt_undrop('database1');
 * SELECT * FROM database1.t;
 * </pre>
 *
 * <h3>使用说明</h3>
 * <p>
 * 已删除的数据库会被移动到 Dolt 数据目录中的 `.dolt_dropped_databases` 目录。
 * 如果具有相同名称的数据库被多次删除，之前的副本将被重命名为 `<database_name>.backup.<timestamp>`。
 * 这使您即使在数据库被重新创建并再次删除的情况下，也可以恢复之前的版本。
 * 要恢复之前的版本，请将备份目录重命名为原始数据库名称，然后调用
 * `dolt_undrop('<database_name>')`。如果您未重命名目录并在调用 `dolt_undrop()` 时使用带有时间戳的名称，
 * 则数据库将以包含时间戳的名称恢复。
 */
public class DoltUnDrop extends DoltRepository implements DoltProcedure<DoltUnDrop.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltUnDrop> INSTANCES = new ConcurrentHashMap<>();

    private DoltUnDrop(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltUnDrop getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltUnDrop(connectionManager));
    }

    public static class Params extends AbstractProcedureParamBuilder<Params> {

        protected Params(DoltProcedure<Params> doltProcedure) {
            super(Params.class, doltProcedure);
        }

        @MethodMutexGroup({"withDatabase"})
        public Params withDatabase(String database) {
            validator.checkAndMark("withDatabase");
            addFlags(database);
            return this;
        }


    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getProcedureTemplate("dolt_undrop"), params);
    }

}