package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.model.ProcedureResult;
import com.hxuanyu.jdolt.repository.DoltRepository;
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
    public static class Params {
        private final List<String> flags;

        private Params(Builder builder) {
            this.flags = List.copyOf(builder.flags);
        }

        public static class Builder {
            private final List<String> flags = new ArrayList<>();

            // backup存储过程的各个方法不允许同时存在，因此使用计数器来对调用进行计数，若存在两个或两个以上的调用，则build方法中直接抛出异常
            private int invokeCount = 0;


            public Builder syncToUrl(String url) {
                invokeCount++;
                flags.add("sync-url");
                flags.add(url);
                return this;
            }

            public Builder addSyncUrl(String name, String url) {
                invokeCount++;
                flags.add("add");
                flags.add(name);
                flags.add(url);
                return this;
            }

            public Builder removeUrl(String name) {
                invokeCount++;
                flags.add("remove");
                flags.add(name);
                return this;
            }

            public Builder restore(String url, String newDbName) {
                invokeCount++;
                flags.add("restore");
                flags.add(url);
                flags.add(newDbName);
                return this;
            }


            /**
             * 构建参数对象
             */
            public Params build() {
                if (invokeCount > 1) {
                    throw new IllegalStateException("Cannot call multiple methods (syncToUrl, addSyncUrl, removeUrl, restore) simultaneously.");
                }
                return new Params(this);
            }
        }

        /**
         * 将参数转换为存储过程调用所需的字符串数组
         */
        String[] toProcedureArgs() {
            return flags.toArray(new String[0]);
        }
    }

    /**
     * 准备参数构建器
     */
    public Params.Builder prepare() {
        return new Params.Builder();
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