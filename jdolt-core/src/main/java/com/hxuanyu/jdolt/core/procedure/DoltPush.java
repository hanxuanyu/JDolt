package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.annotation.MethodDependsOn;
import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;
import com.hxuanyu.jdolt.util.builder.AbstractProcedureParamBuilder;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ## `DOLT_PUSH()`
 * <p>
 * 使用本地引用更新远程引用，同时发送完成给定引用所需的对象。其功能与 CLI 中的 `dolt push` 完全相同，并接受相同的参数。
 * <p>
 * 示例代码：
 * {@code
 * CALL DOLT_PUSH('origin', 'main');
 * CALL DOLT_PUSH('--force', 'origin', 'main');
 * }
 * <p>
 * ### 选项
 * <p>
 * `--force`：使用本地历史记录更新远程历史记录，覆盖远程中的任何冲突历史记录。
 * <p>
 * ### 输出模式
 * <p>
 * {@code
 * +---------+------+--------------------------------+
 * | 字段    | 类型 | 描述                           |
 * +---------+------+--------------------------------+
 * | status  | int  | 成功为 0，失败为 1             |
 * | message | text | 可选的提示信息                 |
 * +---------+------+--------------------------------+
 * }
 * <p>
 * ### 示例
 * <p>
 * {@code
 * -- 切换到新分支
 * CALL DOLT_CHECKOUT('-b', 'feature-branch');
 * <p>
 * -- 添加一个表
 * CREATE TABLE test (a int primary key);
 * <p>
 * -- 创建提交
 * CALL DOLT_COMMIT('-a', '-m', 'create table test');
 * <p>
 * -- 推送到远程
 * CALL DOLT_PUSH('origin', 'feature-branch');
 * }
 */
public class DoltPush extends DoltRepository implements DoltProcedure<DoltPush.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltPush> INSTANCES = new ConcurrentHashMap<>();

    private DoltPush(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltPush getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltPush(connectionManager));
    }


    public static class Params extends AbstractProcedureParamBuilder<Params> {

        protected Params(DoltProcedure<Params> doltProcedure) {
            super(Params.class, doltProcedure);
        }

        @MethodMutexGroup({"withRemote"})
        public Params withRemote(String remote) {
            validator.checkAndMark("withRemote");
            addFlags(remote);
            return this;
        }

        @MethodMutexGroup({"withRefSpec"})
        @MethodDependsOn({"withRemote"})
        public Params withRefSpec(String refSpec) {
            validator.checkAndMark("withRefSpec");
            addFlags(refSpec);
            return this;
        }

        @MethodMutexGroup({"force"})
        public Params force() {
            validator.checkAndMark("force");
            addFlags("--force");
            return this;
        }

        @MethodMutexGroup({"pushAll"})
        public Params pushAll() {
            validator.checkAndMark("pushAll");
            addFlags("--all");
            return this;
        }

        @MethodMutexGroup({"silent"})
        public Params silent() {
            validator.checkAndMark("silent");
            addFlags("--silent");
            return this;
        }

        @MethodMutexGroup({"setUpstream"})
        public Params setUpStream() {
            validator.checkAndMark("setUpStream");
            addFlags("--set-upstream");
            return this;
        }


    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getProcedureTemplate("dolt_push"), params);
    }

}