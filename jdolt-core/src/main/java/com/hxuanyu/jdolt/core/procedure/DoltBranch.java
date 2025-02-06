package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.annotation.MethodDependsOn;
import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.model.ProcedureResult;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;
import com.hxuanyu.jdolt.util.MethodConstraintValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 创建、删除和重命名分支。
 * <p>
 * 要列出分支，请使用 {@link dolt-system-tables.md#dolt_branches DOLT_BRANCHES 系统表}，而不是 `DOLT_BRANCH()` 存储过程。
 * <p>
 * 要查看当前分支，请使用 {@link dolt-sysvars.md#dbname_head_ref @@<dbname>_head_ref 系统变量}，或使用 `active_branch()` SQL 函数，如下方示例部分所示。
 *
 * <p><b>警告：</b>在多会话服务器环境中，如果某个分支正在其他会话中使用，Dolt将阻止您删除或重命名该分支。
 * 您可以通过传递 `--force` 选项强制重命名或删除，但需要注意的是，其他会话中正在使用该分支的客户端将无法再执行语句，并需要结束会话后重新连接。
 *
 * <pre>{@code
 * -- 从当前HEAD创建一个新分支
 * CALL DOLT_BRANCH('myNewBranch');
 *
 * -- 从feature1分支的起点创建一个新分支
 * CALL DOLT_BRANCH('myNewBranch', 'feature1');
 *
 * -- 通过复制现有分支创建一个新分支
 * -- 如果feature1分支已存在，将会失败
 * CALL DOLT_BRANCH('-c', 'main', 'feature1');
 *
 * -- 通过复制现有分支创建或替换分支
 * -- '-f'选项强制复制，即使feature1分支已存在
 * CALL DOLT_BRANCH('-c', '-f', 'main', 'feature1');
 *
 * -- 删除分支
 * CALL DOLT_BRANCH('-d', 'branchToDelete');
 *
 * -- 重命名分支
 * CALL DOLT_BRANCH('-m', 'currentBranchName', 'newBranchName');
 * }</pre>
 *
 * <h3>注意事项</h3>
 * 分支名称有一些限制，与Git对分支名称的约束类似。Dolt的分支名称限制更严格，要求必须使用
 * <a href="https://en.wikipedia.org/wiki/ASCII">ASCII</a>字符。规则如下：
 *
 * <ul>
 *   <li>所有字符必须是ASCII（7位）</li>
 *   <li>不得以 '.'（句点）开头</li>
 *   <li>不得包含 '..'（两个句点）</li>
 *   <li>不得包含 '@{'</li>
 *   <li>不得包含ASCII控制字符</li>
 *   <li>不得包含以下字符：':', '?', '[', '\\', '^', '~', '*'</li>
 *   <li>不得包含空格（空格、制表符、换行符）</li>
 *   <li>不得以 '/' 结尾</li>
 *   <li>不得以 '.lock' 结尾</li>
 *   <li>不得为 HEAD（不区分大小写）</li>
 *   <li>不得与提交哈希值无法区分（32个字符，且所有字符为0-9或a-z，区分大小写）</li>
 * </ul>
 *
 * <p>`dolt_branch()` 过程会隐式提交当前事务并开始一个新事务。
 *
 * <h3>选项</h3>
 * <ul>
 *   <li><code>-c, --copy</code>：复制分支。必须跟随要复制的源分支名称以及要创建的新分支名称。如果没有 <code>--force</code> 选项，当新分支已存在时复制将失败。</li>
 *   <li><code>-m, --move</code>：移动/重命名分支。必须跟随现有分支的当前名称和该分支的新名称。如果没有 <code>--force</code> 选项，当分支正在其他服务器会话中使用时，重命名将失败。需要注意，强制重命名或删除正在其他会话中使用的分支将要求这些会话断开连接并重新连接后才能再次执行语句。</li>
 *   <li><code>-d, --delete</code>：删除分支。必须跟随现有分支的名称。如果没有 <code>--force</code> 选项，当分支正在其他服务器会话中使用时，删除将失败。需要注意，强制重命名或删除正在其他会话中使用的分支将要求这些会话断开连接并重新连接后才能再次执行语句。</li>
 *   <li><code>-f, --force</code>：与 <code>--copy</code> 选项一起使用时，允许从另一个分支重新创建分支，即使该分支已存在。与 <code>--move</code> 或 <code>--delete</code> 选项一起使用时，强制允许您重命名或删除正在其他活动服务器会话中使用的分支，但需要注意，这将要求其他会话断开连接并重新连接后才能再次执行语句。</li>
 *   <li><code>-D</code>：<code>--delete --force</code> 的快捷方式。</li>
 * </ul>
 *
 * <h3>输出模式</h3>
 * <pre>{@code
 * +--------+------+---------------------------+
 * | Field  | Type | Description               |
 * +--------+------+---------------------------+
 * | status | int  | 0表示成功，1表示失败      |
 * +--------+------+---------------------------+
 * }</pre>
 *
 * <h3>示例</h3>
 * <pre>{@code
 * -- 列出可用分支
 * SELECT * FROM DOLT_BRANCHES;
 * +--------+----------------------------------+
 * | name   | hash                             |
 * +--------+----------------------------------+
 * | backup | nsqtc86d54kafkuf0a24s4hqircvg68g |
 * | main   | dvtsgnlg7n9squriob3nq6kve6gnhkf2 |
 * +--------+----------------------------------+
 *
 * -- 从head的顶端创建一个新的开发分支并切换到该分支
 * CALL DOLT_BRANCH('myNewFeature');
 * CALL DOLT_CHECKOUT('myNewFeature');
 *
 * -- 查看当前分支
 * SELECT active_branch();
 * +----------------+
 * | active_branch  |
 * +----------------+
 * | myNewFeature   |
 * +----------------+
 *
 * -- 从现有分支创建新分支
 * CALL DOLT_BRANCH('-c', 'backup', 'bugfix-3482');
 *
 * -- 重命名分支
 * CALL DOLT_BRANCH('-m', 'bugfix-3482', 'critical-bugfix-3482');
 *
 * -- 删除分支
 * CALL DOLT_BRANCH('-d', 'old-unused-branch');
 * }</pre>
 */
public class DoltBranch extends DoltRepository implements DoltProcedure {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltBranch> INSTANCES = new ConcurrentHashMap<>();

    private DoltBranch(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltBranch getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltBranch(connectionManager));
    }

    // 参数包装类作为静态内部类
    public static class Params {
        private final List<String> flags;

        private Params(Builder builder) {
            this.flags = List.copyOf(builder.flags);
        }

        public static class Builder {
            private final List<String> flags = new ArrayList<>();
            private MethodConstraintValidator validator = new MethodConstraintValidator(Builder.class);


            @MethodMutexGroup("create")
            public Builder create(String newBranchName) {
                validator.checkAndMark("create");
                flags.add(newBranchName);
                return this;
            }

            @MethodMutexGroup("create")
            public Builder create(String newBranch, String sourceBranch) {
                validator.checkAndMark("create");
                flags.add(newBranch);
                flags.add(sourceBranch);
                return this;
            }

            @MethodMutexGroup("copy")
            public Builder copy(String sourceBranch, String newBranch) {
                validator.checkAndMark("copy");
                flags.add("-c");
                flags.add(sourceBranch);
                flags.add(newBranch);
                return this;
            }

            @MethodMutexGroup("force")
            @MethodDependsOn({"move", "delete", "create"})
            public Builder force() {
                validator.checkAndMark("force");
                flags.add("-f");
                return this;
            }

            @MethodMutexGroup("move")
            public Builder move(String oldBranch, String newBranch) {
                validator.checkAndMark("move");
                flags.add("-m");
                flags.add(oldBranch);
                flags.add(newBranch);
                return this;
            }

            @MethodMutexGroup("delete")
            public Builder delete(String branch) {
                validator.checkAndMark("delete");
                flags.add("-d");
                flags.add(branch);
                return this;
            }

            /**
             * 构建参数对象
             */
            public Params build() {

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
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.SQL_PROCEDURE_DOLT_BRANCH, params);
    }

}