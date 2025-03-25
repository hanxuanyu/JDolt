package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.annotation.MethodExclusive;
import com.hxuanyu.jdolt.annotation.MethodInvokeRequiredGroup;
import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractProcedureParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;

import java.util.concurrent.ConcurrentHashMap;


/**
 * ## `DOLT_MERGE()`
 * <p>
 * 将指定提交（自从其历史与当前分支分叉以来的更改）合并到当前分支。其功能与CLI中的`dolt merge`完全相同，并接受相同的参数。
 * <p>
 * 在事务提交或创建新的Dolt提交之前，必须解决所有生成的合并冲突。`DOLT_MERGE()`会为任何成功的合并创建一个新的提交，如果未定义提交信息，则会自动生成提交信息。
 *
 * <pre>{@code
 * CALL DOLT_MERGE('feature-branch'); -- 可选的 --squash 参数
 * CALL DOLT_MERGE('feature-branch', '--no-ff', '-m', '这是一个非快进合并的消息');
 * CALL DOLT_MERGE('--abort');
 * }</pre>
 * <p>
 * 注意事项:
 * <p>
 * - `dolt_merge()`过程会隐式提交当前事务并开始一个新的事务。
 * <p>
 * ### 选项
 * <p>
 * - `--no-ff`：即使合并可以快进，也会创建一个合并提交。
 * - `--squash`：将更改合并到工作集而不更新提交历史。
 * - `-m <msg>, --message=<msg>`：使用给定的消息作为提交信息。此选项仅对`--no-ff`提交有用。
 * - `--abort`：中止当前的冲突解决过程，并尝试重建合并前的状态。
 * - `--author`：使用标准格式`A U Thor <author@example.com>`指定明确的作者。
 * <p>
 * 在合并分支时，您的会话状态必须是干净的。请先`COMMIT`或`ROLLBACK`所有更改，然后使用`DOLT_COMMIT()`在目标分支上创建一个新的Dolt提交。
 * <p>
 * 如果合并导致冲突或约束违规，您必须在事务提交之前，通过`dolt_conflicts`系统表解决这些问题。详情请参阅[Dolt系统表](dolt-system-tables.md##dolt_conflicts_usdtablename)。
 * <p>
 * ### 输出模式
 *
 * <pre>{@code
 * +--------------+------+--------------------------------------+
 * | 字段         | 类型 | 描述                                 |
 * +--------------+------+--------------------------------------+
 * | hash         | text | 合并提交的哈希值                     |
 * | fast_forward | int  | 合并是否为快进                       |
 * | conflicts    | int  | 生成的冲突数量                       |
 * | message      | text | 可选的提示信息                       |
 * +--------------+------+--------------------------------------+
 * }</pre>
 * <p>
 * ### 示例
 *
 * <pre>{@code
 * -- 设置当前会话的数据库
 * USE mydb;
 *
 * -- 创建并切换到新分支
 * CALL DOLT_CHECKOUT('-b', 'feature-branch');
 *
 * -- 进行修改
 * UPDATE table
 * SET column = "new value"
 * WHERE pk = "key";
 *
 * -- 暂存并提交所有更改
 * CALL DOLT_COMMIT('-a', '-m', '提交所有更改');
 *
 * -- 切换回主分支
 * CALL DOLT_MERGE('feature-branch', '--author', 'John Doe <johndoe@example.com>');
 * }</pre>
 */
public class DoltMerge extends DoltRepository implements DoltProcedure<DoltMerge.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltMerge> INSTANCES = new ConcurrentHashMap<>();

    private DoltMerge(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltMerge getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltMerge(connectionManager));
    }

    @MethodInvokeRequiredGroup(value = {"withBranch", "abort"}, allRequired = false)
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

        @MethodMutexGroup({"withMessage"})
        public Params withMessage(String message) {
            validator.checkAndMark("withMessage");
            addFlags("--message", message);
            return this;
        }

        @MethodMutexGroup({"withAuthor"})
        public Params withAuthor(String authorName, String mail) {
            validator.checkAndMark("withAuthor");
            addFlags("--author", authorName + " <" + mail + ">");
            return this;
        }

        @MethodExclusive
        public Params abort() {
            validator.checkAndMark("abort");
            addFlags("--abort");
            return this;
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
    public SqlBuilder.SqlTemplate buildSqlTemplate(String... params) {
        return SqlBuilder.callProcedure("dolt_merge")
                .withParams(params)
                .build();
    }

}