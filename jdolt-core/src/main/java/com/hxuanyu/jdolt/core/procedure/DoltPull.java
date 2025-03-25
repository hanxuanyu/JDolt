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
 * `DOLT_PULL()`
 * <p>
 * 从其他数据库或本地分支获取并集成数据。在默认模式下，`dolt pull` 是 `dolt fetch` 和 `dolt merge <remote>/<branch>` 的简写。
 * 其功能与 CLI 中的 `dolt pull` 完全相同，并接受相同的参数。
 * <p>
 * 在事务提交或创建新的 Dolt 提交之前，必须解决所有合并冲突。
 *
 * <pre>
 * CALL DOLT_PULL('origin');
 * CALL DOLT_PULL('origin', 'some-branch');
 * CALL DOLT_PULL('feature-branch', '--force');
 * </pre>
 *
 * <h3>选项</h3>
 * <ul>
 * <li><code>--no-ff</code>：即使合并可以快速前进，也会创建一个合并提交。</li>
 * <li><code>--squash</code>：将更改合并到工作集，而不更新提交历史记录。</li>
 * <li><code>--force</code>：忽略任何外键警告并继续提交。</li>
 * </ul>
 * <p>
 * 在合并分支时，您的会话状态必须是干净的。请先 `COMMIT` 或 `ROLLBACK` 所有更改，
 * 然后使用 `DOLT_COMMIT()` 在目标分支上创建一个新的 Dolt 提交。
 * <p>
 * 如果合并导致冲突或约束违规，您必须使用 `dolt_conflicts` 系统表解决这些问题，然后才能提交事务。
 * 详情请参阅 <a href="dolt-system-tables.md##dolt_conflicts_usdtablename">Dolt 系统表</a>。
 *
 * <h3>输出模式</h3>
 * <pre>
 * +--------------+------+-------------------------------------+
 * | 字段         | 类型 | 描述                                |
 * +--------------+------+-------------------------------------+
 * | fast_forward | int  | 是否为快速前进合并                 |
 * | conflicts    | int  | 产生的冲突数量                     |
 * | message      | text | 可选的提示信息                     |
 * +--------------+------+-------------------------------------+
 * </pre>
 *
 * <h3>示例</h3>
 * <pre>
 * -- 使用远程更改更新本地工作集
 * -- 注意：这需要设置上游跟踪信息，以便 Dolt 知道要合并的远程分支
 * CALL DOLT_PULL('origin');
 *
 * -- 使用指定分支的远程更改更新本地工作集
 * CALL DOLT_PULL('origin', 'some-branch');
 *
 * -- 查看新提交的日志
 * SELECT * FROM dolt_log LIMIT 5;
 * </pre>
 */
public class DoltPull extends DoltRepository implements DoltProcedure<DoltPull.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltPull> INSTANCES = new ConcurrentHashMap<>();

    private DoltPull(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltPull getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltPull(connectionManager));
    }

    public static class Params extends AbstractProcedureParamBuilder<Params> {

        protected Params(DoltProcedure<Params> doltProcedure) {
            super(Params.class, doltProcedure);
        }

        @MethodMutexGroup({"noFastForward"})
        public Params noFastForward() {
            validator.checkAndMark("noFastForward");
            addFlags("--no-ff");
            return this;
        }

        @MethodMutexGroup({"withSquash"})
        public Params withSquash() {
            validator.checkAndMark("withSquash");
            addFlags("--squash");
            return this;
        }

        @MethodMutexGroup({"withRemote"})
        public Params withRemote(String remote) {
            validator.checkAndMark("withRemote");
            addFlags(remote);
            return this;
        }

        @MethodMutexGroup({"force"})
        public Params force() {
            validator.checkAndMark("force");
            addFlags("--force");
            return this;
        }

        @MethodMutexGroup({"withBranch"})
        @MethodDependsOn({"withRemote"})
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
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getProcedureTemplate("dolt_pull"), params);
    }

}