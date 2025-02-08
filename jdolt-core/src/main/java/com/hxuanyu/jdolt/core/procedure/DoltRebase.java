package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.annotation.*;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.AbstractProcedureParamBuilder;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;

import java.util.concurrent.ConcurrentHashMap;


/**
 * ## `DOLT_REBASE()`
 * <p>
 * 通过重放提交记录重写当前分支的提交历史，从而可以重新排序、合并或删除提交记录。包含在变基计划中的提交记录是当前分支可达的提交记录，
 * 但不包括从开始变基时指定的分支（也称为上游分支）可达的提交记录。这与 Git 和 Dolt 的
 * <a href="https://www.dolthub.com/blog/2022-11-11-two-and-three-dot-diff-and-log/#two-dot-log">“双点日志”语法</a>相同，即 |upstreamBranch|..|currentBranch|。
 * <p>
 * 例如，考虑以下提交图，其中 `feature` 分支从 `main` 分支分出，并且两个分支都添加了提交记录：
 *
 * <pre>{@code
 * A → B → C → D → E → F  main
 *          ↘
 *            G → H → I  feature
 * }</pre>
 * <p>
 * 如果我们从 `feature` 分支变基，使用 `main` 分支作为上游分支，默认的变基计划将包括提交记录 `G`、`H` 和 `I`，
 * 因为这些提交记录是从当前分支可达的，但从上游分支不可达。默认情况下，这些提交记录的更改将按相同的顺序重新应用到上游分支 `main` 的最新提交。
 * 最终的提交图将如下所示：
 *
 * <pre>{@code
 * A → B → C → D → E → F  main
 *                      ↘
 *                        G' → H' → I'  feature
 * }</pre>
 * <p>
 * 变基在清理和组织提交历史方面非常有用，尤其是在将功能分支合并回共享分支之前。例如，您可以删除包含调试或测试更改的提交记录，
 * 将小的提交记录合并或修正为一个提交记录，或者重新排序提交记录，使相关更改在新的提交历史中相邻。
 *
 * <pre>{@code
 * CALL DOLT_REBASE('--interactive', 'main');
 * CALL DOLT_REBASE('-i', 'main');
 * CALL DOLT_REBASE('-i', '--empty=keep', 'main');
 * CALL DOLT_REBASE('--continue');
 * CALL DOLT_REBASE('--abort');
 * }</pre>
 * <p>
 * ### 限制
 * <p>
 * 目前仅支持交互式变基。数据冲突的解决通过 Dolt 的标准冲突解决流程支持，但架构冲突的解决尚不支持。
 * 如果变基遇到架构冲突，变基将自动中止。
 * <p>
 * ### 选项
 * <p>
 * `--interactive` 或 `-i`：启动交互式变基。目前仅支持交互式变基，因此此选项是必需的。
 * <p>
 * `--continue`：在调整存储于 `dolt_rebase` 中的变基计划后继续交互式变基。
 * <p>
 * `--abort`：中止正在进行的变基。
 * <p>
 * `--empty`：
 * 如何处理在变基后变为空的提交记录。有效值为：drop（默认）或 keep。此选项只能在开始变基时指定，在继续变基时无效。
 * <p>
 * ### 输出模式
 *
 * <pre>{@code
 * +---------+------+-----------------------------+
 * | Field   | Type | Description                 |
 * +---------+------+-----------------------------+
 * | status  | int  | 0 表示成功，1 表示失败      |
 * | message | text | 成功/失败的信息             |
 * +---------+------+-----------------------------+
 * }</pre>
 * <p>
 * ### 示例
 *
 * <pre>{@code
 * -- 创建一个简单的表
 * create table t (pk int primary key);
 * call dolt_commit('-Am', 'creating table t');
 *
 * -- 创建一个新分支，稍后将在其上添加更多提交记录
 * call dolt_branch('branch1');
 *
 * -- 在 branch1 分支分出后，立即在 main 分支上创建另一个提交
 * insert into t values (0);
 * call dolt_commit('-am', 'inserting row 0');
 *
 * -- 切换到 branch1 并创建三个提交记录，每个提交插入一行
 * call dolt_checkout('branch1');
 * insert into t values (1);
 * call dolt_commit('-am', 'inserting row 1');
 * insert into t values (2);
 * call dolt_commit('-am', 'inserting row 2');
 * insert into t values (3);
 * call dolt_commit('-am', 'inserting row 3');
 *
 * -- 检查变基前 branch1 分支的提交历史
 * select commit_hash, message from dolt_log;
 * +----------------------------------+----------------------------+
 * | commit_hash                      | message                    |
 * +----------------------------------+----------------------------+
 * | tsq01op7b48ij6dfa2tst60vbfm9rcus | inserting row 3            |
 * | uou7dibe86e9939pu8fdtjdce5pt7v1c | inserting row 2            |
 * | 3umkjmqeeep5ho7nn0iggfinajoo1l6q | inserting row 1            |
 * | 35gfll6o322aq9uffdqin1dqmq7q3vek | creating table t           |
 * | do1tp9u39vsja3c8umshv9p6fernr0lt | 初始化数据仓库              |
 * +----------------------------------+----------------------------+
 *
 * -- 启动交互式变基并检查默认变基计划；
 * -- 此操作将变基此分支上的所有新提交记录并将其移动到 main 分支的最新提交
 * call dolt_rebase('-i', 'main');
 * select * from dolt_rebase order by rebase_order;
 * +--------------+--------+----------------------------------+-----------------+
 * | rebase_order | action | commit_hash                      | commit_message  |
 * +--------------+--------+----------------------------------+-----------------+
 * | 1.00         | pick   | 3umkjmqeeep5ho7nn0iggfinajoo1l6q | inserting row 1 |
 * | 2.00         | pick   | uou7dibe86e9939pu8fdtjdce5pt7v1c | inserting row 2 |
 * | 3.00         | pick   | tsq01op7b48ij6dfa2tst60vbfm9rcus | inserting row 3 |
 * +--------------+--------+----------------------------------+-----------------+
 *
 * -- 调整变基计划以修改第一条提交记录的消息，删除插入第 2 行的提交记录，
 * -- 并将第三条提交记录合并到上一条提交记录中
 * update dolt_rebase set action='reword', commit_message='insert rows' where rebase_order=1;
 * update dolt_rebase set action='drop' where rebase_order=2;
 * update dolt_rebase set action='fixup' where rebase_order=3;
 *
 * -- 调整变基计划后继续变基
 * call dolt_rebase('--continue');
 *
 * -- 检查提交历史
 * select commit_hash, message from dolt_log;
 * +----------------------------------+----------------------------+
 * | commit_hash                      | message                    |
 * +----------------------------------+----------------------------+
 * | 8jc1dpj25fv6f2kn3bd47uokc8hs1vp0 | insert rows                |
 * | hb9fnqnrsd5ghq3fgag0kiq6nvpsasvo | inserting row 0            |
 * | 35gfll6o322aq9uffdqin1dqmq7q3vek | creating table t           |
 * | do1tp9u39vsja3c8umshv9p6fernr0lt | 初始化数据仓库              |
 * +----------------------------------+----------------------------+
 * }</pre>
 */
public class DoltRebase extends DoltRepository implements DoltProcedure<DoltRebase.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltRebase> INSTANCES = new ConcurrentHashMap<>();

    private DoltRebase(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltRebase getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltRebase(connectionManager));
    }

    @MethodInvokeRequiredGroup(value = {"start", "continue", "abort"}, allRequired = false)
    public static class Params extends AbstractProcedureParamBuilder<Params> {

        protected Params(DoltProcedure<Params> doltProcedure) {
            super(Params.class, doltProcedure);
        }

        @MethodMutexGroup({"start"})
        public Params start() {
            validator.checkAndMark("start");
            addFlags("-i");
            return this;
        }

        @MethodMutexGroup({"keepEmpty", "dropEmpty"})
        @MethodDependsOn("start")
        public Params keepEmpty() {
            validator.checkAndMark("keepEmpty");
            addFlags("--empty=keep");
            return this;
        }

        @MethodMutexGroup({"dropEmpty", "keepEmpty"})
        @MethodDependsOn("start")
        public Params dropEmpty() {
            validator.checkAndMark("dropEmpty");
            addFlags("--empty=drop");
            return this;
        }

        @MethodInvokeRequired
        @MethodMutexGroup({"withUpstream"})
        public Params withUpstream(String upstream) {
            validator.checkAndMark("withUpstream");
            addFlags(upstream);
            return this;
        }

        @MethodExclusive
        public Params continueRebase() {
            validator.checkAndMark("continueRebase");
            addFlags("--continue");
            return this;
        }


        @MethodExclusive
        public Params abort() {
            validator.checkAndMark("abort");
            addFlags("--abort");
            return this;
        }


    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getProcedureTemplate("dolt_rebase"), params);
    }

}