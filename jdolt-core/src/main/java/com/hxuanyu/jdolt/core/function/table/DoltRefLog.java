package com.hxuanyu.jdolt.core.function.table;

import com.hxuanyu.jdolt.annotation.MethodInvokeRequiredGroup;
import com.hxuanyu.jdolt.interfaces.DoltTableFunction;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractTableFunctionParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;


import java.util.concurrent.ConcurrentHashMap;
/**
 * `DOLT_REFLOG()` 表函数显示命名引用（例如分支和标签）的历史记录，这在您希望了解某个分支或标签如何随着时间的推移指向不同的提交时非常有用，特别是当这些信息无法通过 `dolt_log` 系统表或 `dolt_log()` 表函数获取时。例如，如果您使用 `dolt_reset()` 更改了分支指向的提交，您可以使用 `dolt_reflog()` 查看该分支在更改之前指向的提交。另一个常见的使用场景是恢复意外删除的分支或标签。下面的示例部分展示了如何恢复已删除的分支。
 *
 * Dolt 的 reflog 数据来自 <a href="https://www.dolthub.com/blog/2023-03-08-dolt-chunk-journal/">Dolt 的日志块存储</a>。这些数据是 Dolt 数据库的本地数据，不会在推送、拉取或克隆 Dolt 数据库时包含。这意味着当您克隆一个 Dolt 数据库时，除非执行更改分支或标签指向的操作，否则它不会包含任何 reflog 数据。
 *
 * Dolt 的 reflog 类似于 <a href="https://git-scm.com/docs/git-reflog">Git 的 reflog</a>，但有一些不同之处：
 *
 * <ul>
 * <li>Dolt 的 reflog 当前仅支持命名引用，例如分支和标签，而不支持 Git 的特殊引用（例如 `HEAD`、`FETCH-HEAD`、`MERGE-HEAD`）。</li>
 * <li>Dolt 的 reflog 即使在引用被删除后也可以查询该引用的日志。在 Git 中，一旦分支或标签被删除，与该引用相关的 reflog 也会被删除。如果想找到分支或标签最后指向的提交，必须使用 Git 的特殊 `HEAD` reflog，这有时会比较复杂。而 Dolt 则更简单，它允许您查看已删除引用的历史记录，从而轻松找到分支或标签被删除前最后指向的提交。</li>
 * </ul>
 *
 * <h3>权限</h3>
 *
 * 使用 `dolt_reflog()` 表函数不需要特殊权限。
 *
 * <h3>选项</h3>
 *
 * <pre>{@code
 * DOLT_REFLOG()
 * DOLT_REFLOG(['--all'], <ref_name>)
 * }</pre>
 *
 * `dolt_reflog()` 表函数可以不带参数调用，也可以带一个参数调用。如果不带任何参数调用，它将返回完整的引用日志，按最新到最旧列出所有被跟踪的引用的更改。如果带一个参数调用，该参数是要查询的引用名称。这可以是分支名称（例如 "myBranch"）、标签名称（例如 "v1.1.4"），或者是完整的引用路径（例如 "refs/heads/myBranch"）。`ref_name` 参数对大小写不敏感。
 *
 * `dolt_reflog()` 表函数还可以使用 `--all` 标志调用，以显示所有引用，包括隐藏引用，例如 DoltHub 工作区引用。
 *
 * <h3>数据结构</h3>
 *
 * <pre>{@code
 * +-----------------------+-----------+
 * | 字段                 | 类型      |
 * +-----------------------+-----------+
 * | ref                   | TEXT      |
 * | ref_timestamp         | TIMESTAMP |
 * | commit_hash           | TEXT      |
 * | commit_message        | TEXT      |
 * +-----------------------+-----------+
 * }</pre>
 *
 * <h3>示例</h3>
 *
 * 以下示例展示了如何通过在 Dolt 的 reflog 中找到已删除分支最后指向的提交来恢复该分支。
 *
 * <pre>{@code
 * -- 某人错误地删除了错误的分支！
 * call dolt_branch('-D', 'prodBranch');
 *
 * -- 在发现删除了错误的分支后，我们在删除该分支的同一 Dolt 数据库实例中查询 Dolt 的 reflog，
 * -- 查看 prodBranch 分支曾经指向的提交。使用相同的 Dolt 实例很重要，因为 reflog 信息始终是本地的，
 * -- 不会在推送/拉取数据库时包含。
 * select * from dolt_reflog('prodBranch');
 * +-----------------------+---------------------+----------------------------------+-------------------------------+
 * | ref                   | ref_timestamp       | commit_hash                      | commit_message                |
 * +-----------------------+---------------------+----------------------------------+-------------------------------+
 * | refs/heads/prodBranch | 2023-10-25 20:54:37 | v531ptpmv2tquig8v591tsjghtj84ksg | inserting row 42              |
 * | refs/heads/prodBranch | 2023-10-25 20:53:12 | rvt34lqrbtdr3dhnjchruu73lik4e398 | inserting row 100000          |
 * | refs/heads/prodBranch | 2023-10-25 20:53:06 | v531ptpmv2tquig8v591tsjghtj84ksg | inserting row 42              |
 * | refs/heads/prodBranch | 2023-10-25 20:52:43 | ihuj1l7fmqq37sjhtlrgpup5n76gfhju | inserting row 1 into table xy |
 * +-----------------------+---------------------+----------------------------------+-------------------------------+
 *
 * -- prodBranch 最后指向的提交是 v531ptpmv2tquig8v591tsjghtj84ksg，因此要恢复分支，
 * -- 我们只需创建一个同名分支，并使其指向该最后的提交。
 * call dolt_branch('prodBranch', 'v531ptpmv2tquig8v591tsjghtj84ksg');
 * }</pre>
 */
@MethodInvokeRequiredGroup(value = {"all", "refName"}, allRequired = false)
public class DoltRefLog extends DoltRepository implements DoltTableFunction<DoltRefLog.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltRefLog> INSTANCES = new ConcurrentHashMap<>();

    private DoltRefLog(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltRefLog getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltRefLog(connectionManager));
    }

    public static class Params extends AbstractTableFunctionParamBuilder<Params> {

        protected Params(DoltTableFunction<Params> doltTableFunction) {
            super(Params.class, doltTableFunction);
        }

        /**
         * 显示所有引用，包括隐藏引用，例如 DoltHub 工作区引用
         *
         * @return 当前参数构建器实例
         */
        public Params all() {
            validator.checkAndMark("all");
            addFlags("--all");
            return this;
        }

        /**
         * 设置要查询的引用名称
         *
         * 可以是分支名称（例如 "myBranch"）、标签名称（例如 "v1.1.4"），
         * 或者是完整的引用路径（例如 "refs/heads/myBranch"）
         * 引用名称对大小写不敏感
         *
         * @param refName 引用名称
         * @return 当前参数构建器实例
         */
        public Params refName(String refName) {
            validator.checkAndMark("refName");
            addFlags(refName);
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
                .fromFunction("dolt_reflog")
                .withParams(params)
                .build();
    }

}