package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.model.ProcedureResult;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.AbstractParamBuilder;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * `DOLT_BACKUP()`
 * <p>
 * 添加或移除已配置的备份，与已配置的备份同步，将备份同步到远程URL，将远程URL备份恢复为一个新数据库。
 *
 * <h2>输出模式</h2>
 * <pre>
 * +--------+------+---------------------------+
 * | 字段   | 类型 | 描述                      |
 * +--------+------+---------------------------+
 * | status | int  | 成功为0，失败为1          |
 * +--------+------+---------------------------+
 * </pre>
 *
 * <h3>将当前数据库同步到已配置的备份：</h3>
 * <pre>{@code
 * CALL DOLT_BACKUP('sync', 'name');
 * }</pre>
 *
 * <h3>与未配置为备份的远程URL同步：</h3>
 * <pre>{@code
 * CALL DOLT_BACKUP('sync-url', 'https://dolthub.com/some_organization/some_dolthub_repository');
 * }</pre>
 *
 * <h3>添加和移除已配置的备份：</h3>
 * <pre>{@code
 * CALL DOLT_BACKUP('add', 'dolthub', 'https://dolthub.com/some_organization/some_dolthub_repository');
 *
 * CALL DOLT_BACKUP('remove', 'dolthub');
 * }</pre>
 *
 * <h3>恢复备份：</h3>
 * <pre>{@code
 * CALL DOLT_BACKUP('restore', 'https://dolthub.com/some_organization/some_dolthub_repository', 'database_name');
 * }</pre>
 *
 * <h2>示例</h2>
 * <pre>{@code
 * -- 为会话设置当前数据库
 * USE mydb;
 *
 * -- 配置一个备份以进行同步
 * CALL dolt_backup('add', 'my-backup', 'https://dolthub.com/some_organization/some_dolthub_repository');
 *
 * -- 将当前数据库内容上传到命名备份
 * CALL dolt_backup('sync', 'my-backup')
 *
 * -- 将上传的数据库恢复为一个新的数据库名称
 * CALL dolt_backup('restore', 'https://dolthub.com/some_organization/some_dolthub_repository', 'mydb_restored');
 * }</pre>
 */
public class DoltBackup extends DoltRepository implements DoltProcedure {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltBackup> INSTANCES = new ConcurrentHashMap<>();

    private DoltBackup(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltBackup getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltBackup(connectionManager));
    }

    // 参数包装类作为静态内部类
    public static class Params extends AbstractParamBuilder<Params> {


        protected Params() {
            super(Params.class);
        }

        @MethodMutexGroup({"syncToUrl", "addSyncUrl", "removeUrl", "restore", "sync"})
        public Params syncToUrl(String url) {
            validator.checkAndMark("syncToUrl");
            addFlags("sync-url", url);
            return this;
        }

        @MethodMutexGroup({"syncToUrl", "addSyncUrl", "removeUrl", "restore", "sync"})
        public Params addSyncUrl(String backupName, String url) {
            validator.checkAndMark("addSyncUrl");
            addFlags("add", backupName, url);
            return this;
        }

        @MethodMutexGroup({"syncToUrl", "addSyncUrl", "removeUrl", "restore", "sync"})
        public Params removeUrl(String backupName) {
            validator.checkAndMark("removeUrl");
            addFlags("remove", backupName);
            return this;
        }

        @MethodMutexGroup({"syncToUrl", "addSyncUrl", "removeUrl", "restore", "sync"})
        public Params sync(String backupName) {
            validator.checkAndMark("removeUrl");
            addFlags("sync", backupName);
            return this;
        }

        @MethodMutexGroup({"syncToUrl", "addSyncUrl", "removeUrl", "restore"})
        public Params restore(String url, String newDbName) {
            validator.checkAndMark("restore");
            addFlags("restore", url, newDbName);
            return this;
        }
    }

    /**
     * 准备参数构建器
     */
    public Params prepare() {
        return new Params();
    }

    /**
     * 执行存储过程
     *
     * @return 执行结果
     */
    public ProcedureResult execute(Params params) {
        return call(params.toProcedureArgs());
    }

    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getProcedureTemplate("dolt_backup"), params);
    }

}