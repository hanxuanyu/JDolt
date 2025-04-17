package com.hxuanyu.jdolt.core.function.table;

import com.hxuanyu.jdolt.annotation.MethodDependsOn;
import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.interfaces.DoltTableFunction;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractTableFunctionParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;


import java.util.concurrent.ConcurrentHashMap;

/**
 * ## {@code DOLT_LOG()}
 *
 * {@code DOLT_LOG} 表函数用于获取从指定修订版的 {@code HEAD}（如果未提供修订版，则为当前 {@code HEAD}）可到达的所有提交的提交日志。  
 * {@code DOLT_LOG()} 的工作方式类似于 <a href="../../cli/cli.md#dolt-log">CLI {@code dolt log} 命令</a>。  
 *
 * <p>请注意，{@code DOLT_LOG()} 表函数目前要求参数值必须是文字值。</p>  
 *
 * <h3>权限</h3>  
 * {@code DOLT_LOG()} 表函数需要对所有表具有 {@code SELECT} 权限。  
 *
 * <h3>选项</h3>  
 * <pre>{@code
 * DOLT_LOG([<optional_revisions>...], [--tables <tables>...])
 * }</pre>  
 * {@code DOLT_LOG()} 表函数接受任意数量的可选修订参数：  
 * <ul>  
 *   <li>  
 *     <b>optional_revision</b>：分支名称、标签或提交引用（可以带或不带祖先规范），用于指定要包含在结果中的祖先提交。  
 *     如果未指定修订，则默认为当前分支的 {@code HEAD}。  
 *     <ul>  
 *       <li>  
 *         如果您想获取  
 *         <a href="https://www.dolthub.com/blog/2022-11-11-two-and-three-dot-diff-and-log/#two-dot-log">双点日志</a>  
 *         （所有由 {@code revision2} 可到达但 {@code revision1} 不可到达的提交），可以在修订之间使用 {@code ..}
 *         （{@code DOLT_LOG('revision1..revision2')}）或在要排除的修订前使用 {@code ^}
 *         （{@code DOLT_LOG('revision2', '^revision1')}）。注意：如果提供两个修订，其中一个必须包含 {@code ^}。  
 *       </li>  
 *       <li>  
 *         如果您想获取  
 *         <a href="https://www.dolthub.com/blog/2022-11-11-two-and-three-dot-diff-and-log/#three-dot-log">三点日志</a>  
 *         （所有由 {@code revision1} 或 {@code revision2} 可到达的提交，但排除同时由两者可到达的提交），  
 *         可以在修订之间使用 {@code ...}（{@code DOLT_LOG('revision1...revision2')}）。  
 *       </li>  
 *     </ul>  
 *   </li>  
 *   <li><b>--min-parents</b>：提交必须具有的最少父提交数量，才能包含在日志中。</li>  
 *   <li><b>--merges</b>：等价于 {@code min-parents == 2}，将日志限制为具有两个或更多父提交的提交。</li>  
 *   <li><b>--parents</b>：显示日志中每个提交的所有父提交。</li>  
 *   <li><b>--decorate</b>：在提交旁显示引用。有效选项为 short、full、no 和 auto。注意：CLI 中的 {@code dolt log} 命令默认值为 "short"，而此表函数默认值为 "no"。</li>  
 *   <li><b>--not</b>：排除由修订可到达的提交。</li>  
 *   <li><b>--tables</b>：将日志限制为影响指定表的提交。可以指定任意数量的以逗号分隔的表。</li>  
 * </ul>  
 *
 * <h3>数据结构</h3>  
 * <pre>  
 * +-------------+----------+  
 * | field       | type     |  
 * +-------------+--------- +  
 * | commit_hash | text     |  
 * | committer   | text     |  
 * | email       | text     |  
 * | date        | datetime |  
 * | message     | text     |  
 * | parents     | text     | -- 除非提供 `--parents` 标志，否则此列隐藏  
 * | refs        | text     | -- 除非 `--decorate` 为 "short" 或 "full"，否则此列隐藏  
 * +-------------+--------- +  
 * </pre>  
 *
 * <h3>示例</h3>  
 *
 * 假设我们有以下提交图：  
 *
 * <pre>  
 * A - B - C - D (main)  
 *          \
 *           E - F (feature)  
 * </pre>  
 *
 * 要获取 {@code main} 分支的提交日志，可以使用以下查询：  
 * <pre>{@code
 * SELECT * FROM DOLT_LOG('main');
 * }</pre>  
 *
 * 它将以时间倒序返回提交 - {@code D}、{@code C}、{@code B} 和 {@code A}。输出可能类似于：  
 *
 * <pre>  
 * +----------------------------------+-----------+--------------------+-----------------------------------+---------------+  
 * | commit_hash                      | committer | email              | date                              | message       |  
 * +----------------------------------+-----------+--------------------+-----------------------------------+---------------+  
 * | qi331vjgoavqpi5am334cji1gmhlkdv5 | bheni     | brian@dolthub.com  | 2019-06-07 00:22:24.856 +0000 UTC | update rating  |  
 * | 137qgvrsve1u458briekqar5f7iiqq2j | bheni     | brian@dolthub.com  | 2019-04-04 22:43:00.197 +0000 UTC | change rating  |  
 * | rqpd7ga1nic3jmc54h44qa05i8124vsp | bheni     | brian@dolthub.com  | 2019-04-04 21:07:36.536 +0000 UTC | fixes          |  
 * | qfk3bpan8mtrl05n8nihh2e3t68t3hrk | bheni     | brian@dolthub.com  | 2019-04-04 21:01:16.649 +0000 UTC | test           |  
 * +----------------------------------+-----------+--------------------+-----------------------------------+---------------+  
 * </pre>  
 *
 * 要获取 {@code feature} 分支的提交日志，可以在上述查询中更改修订：  
 * <pre>{@code
 * SELECT * FROM DOLT_LOG('feature');
 * }</pre>  
 *
 * 它将返回从 {@code feature} 的 {@code HEAD} 可到达的所有提交 - {@code F}、{@code E}、{@code C}、{@code B} 和 {@code A}。  
 *
 * <h4>双点和三点日志</h4>  
 *
 * 我们还支持双点和三点日志。双点日志返回来自一个修订的提交，但排除另一个修订的提交。如果我们想获取 {@code feature} 中的所有提交，但排除 {@code main} 中的提交，以下查询都将返回 {@code F} 和 {@code E} 提交。  
 *
 * <pre>{@code
 * SELECT * FROM DOLT_LOG('main..feature');
 * SELECT * FROM DOLT_LOG('feature', '^main');
 * SELECT * FROM DOLT_LOG('feature', '--not', 'main');
 * }</pre>  
 *
 * 三点日志返回在任一修订中的提交，但排除同时在两个修订中的提交。如果我们想获取 {@code main} 或 {@code feature} 中的提交，但排除同时在 {@code main} 和 {@code feature} 中的提交，此查询将返回 {@code F}、{@code E} 和 {@code D}。  
 *
 * <pre>{@code
 * SELECT * FROM DOLT_LOG('main...feature');
 * }</pre>  
 *
 * 注意：双点日志中修订的顺序很重要，但三点日志中则无关紧要。  
 * {@code DOLT_LOG('main..feature')} 返回 {@code F} 和 {@code E}，而 {@code DOLT_LOG('feature..main')} 仅返回 {@code D}。  
 * {@code DOLT_LOG('main...feature')} 和 {@code DOLT_LOG('feature...main')} 都返回 {@code F}、{@code E} 和 {@code D}。  
 *
 * <p>了解更多关于双点与三点日志的信息，请访问  
 * <a href="https://www.dolthub.com/blog/2022-11-11-two-and-three-dot-diff-and-log">这里</a>。</p>  
 */
public class DoltLog extends DoltRepository implements DoltTableFunction<DoltLog.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltLog> INSTANCES = new ConcurrentHashMap<>();

    private DoltLog(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltLog getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltLog(connectionManager));
    }

    public static class Params extends AbstractTableFunctionParamBuilder<Params> {

        protected Params(DoltTableFunction<Params> doltTableFunction) {
            super(Params.class, doltTableFunction);
        }

        /**
         * 添加一个修订版本参数
         * @param revision 分支名称、标签或提交引用
         * @return this
         */
        @MethodMutexGroup({"revision", "revisions", "twoDot", "threeDot"})
        public Params revision(String revision) {
            validator.checkAndMark("revision");
            addFlags(revision);
            return this;
        }

        /**
         * 添加多个修订版本参数
         * @param revisions 分支名称、标签或提交引用数组
         * @return this
         */
        @MethodMutexGroup({"revision", "revisions", "twoDot", "threeDot"})
        public Params revisions(String... revisions) {
            validator.checkAndMark("revisions");
            for (String revision : revisions) {
                addFlags(revision);
            }
            return this;
        }

        /**
         * 设置双点日志，返回所有由 to 可到达但 from 不可到达的提交
         * @param from 起始修订版本
         * @param to 目标修订版本
         * @return this
         */
        @MethodMutexGroup({"revision", "revisions", "twoDot", "threeDot"})
        public Params twoDot(String from, String to) {
            validator.checkAndMark("twoDot");
            addFlags(from + ".." + to);
            return this;
        }

        /**
         * 设置三点日志，返回所有由 from 或 to 可到达的提交，但排除同时由两者可到达的提交
         * @param from 第一个修订版本
         * @param to 第二个修订版本
         * @return this
         */
        @MethodMutexGroup({"revision", "revisions", "twoDot", "threeDot"})
        public Params threeDot(String from, String to) {
            validator.checkAndMark("threeDot");
            addFlags(from + "..." + to);
            return this;
        }

        /**
         * 排除由修订可到达的提交
         * @param revision 要排除的修订版本
         * @return this
         */
        public Params not(String revision) {
            validator.checkAndMark("not");
            addFlags("--not", revision);
            return this;
        }

        /**
         * 将日志限制为影响指定表的提交
         * @param tables 表名数组
         * @return this
         */
        public Params tables(String... tables) {
            validator.checkAndMark("tables");
            addFlags("--tables");
            for (String table : tables) {
                addFlags(table);
            }
            return this;
        }

        /**
         * 设置提交必须具有的最少父提交数量
         * @param minParents 最少父提交数量
         * @return this
         */
        @MethodMutexGroup({"merges"})
        public Params minParents(int minParents) {
            validator.checkAndMark("minParents");
            addFlags("--min-parents", String.valueOf(minParents));
            return this;
        }

        /**
         * 将日志限制为具有两个或更多父提交的提交（等价于 --min-parents=2）
         * @return this
         */
        @MethodMutexGroup({"minParents"})
        public Params merges() {
            validator.checkAndMark("merges");
            addFlags("--merges");
            return this;
        }

        /**
         * 显示日志中每个提交的所有父提交
         * @return this
         */
        public Params parents() {
            validator.checkAndMark("parents");
            addFlags("--parents");
            return this;
        }

        /**
         * 在提交旁显示引用
         * @param option 装饰选项：short、full、no 或 auto
         * @return this
         */
        public Params decorate(String option) {
            validator.checkAndMark("decorate");
            addFlags("--decorate", option);
            return this;
        }
    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public SqlBuilder.SqlTemplate buildSqlTemplate(String... params) {
        return SqlBuilder.select()
                .fromFunction("DOLT_LOG")
                .withParams(params)
                .build();
    }

}
