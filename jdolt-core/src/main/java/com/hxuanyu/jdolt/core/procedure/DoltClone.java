package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.model.ProcedureResult;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.ArrayUtils;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;

/**
 * ## `DOLT_CLONE()`
 * <p>
 * 在当前 Dolt 环境中，将现有的 Dolt 数据库克隆为一个新的数据库。
 * 必须将现有数据库作为参数指定，可以是指向磁盘上现有 Dolt 数据库的文件 URL，
 * 或者是远程托管数据库的 `doltremote` URL（例如托管在 DoltHub 或 DoltLab 上的数据库），
 * 也可以使用 `<org>/<database>` 的简写形式（例如 `dolthub/us-jails`）来表示托管在 DoltHub 上的数据库。
 * 可以选择性地提供一个额外参数来指定新克隆数据库的名称，否则将使用现有数据库的当前名称。
 * <p>
 * 注意：从文件 URL 克隆时，目前必须包含 `.dolt/noms` 子目录。
 * 更多详细信息请参阅 GitHub 跟踪问题 <a href="https://github.com/dolthub/dolt/issues/1860">dolt#1860</a>。
 * <p>
 * 示例：
 * <pre>
 * CALL DOLT_CLONE('file:///myDatabasesDir/database/.dolt/noms');
 * CALL DOLT_CLONE('dolthub/us-jails', 'myCustomDbName');
 * </pre>
 * <p>
 * ### 选项
 * <p>
 * - `--remote`：要添加到新克隆数据库的远程名称。默认值为 `origin`。
 * - `-b`, `--branch`：要克隆的分支。如果未指定，将克隆所有分支。
 * - `--depth`：仅克隆单个分支，并将历史记录限制为指定的提交深度。
 * <p>
 * ### 输出模式
 * <pre>
 * +--------+------+---------------------------+
 * | Field  | Type | Description               |
 * +--------+------+---------------------------+
 * | status | int  | 0 表示成功，1 表示失败    |
 * +--------+------+---------------------------+
 * </pre>
 */
public class DoltClone extends DoltRepository implements DoltProcedure {

    private static volatile DoltClone instance;

    protected DoltClone(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltClone instance(DoltConnectionManager connectionManager) {
        if (instance == null) {
            synchronized (DoltClone.class) {
                if (instance == null) {
                    instance = new DoltClone(connectionManager);
                }
            }
        }
        return instance;
    }


    public ProcedureResult cloneUrl(String url) {
        return call(url);
    }

    public ProcedureResult cloneWithDatabaseName(String url, String databaseNane) {
        return call(url, databaseNane);
    }

    public ProcedureResult cloneBranch(String url, String databaseName, String originBranch) {
        return call(url, databaseName, "-branch", originBranch);
    }

    public ProcedureResult cloneBranch(String url, String databaseName, String originBranch, String remoteName) {
        return call(url, databaseName, "-branch", originBranch, "--remote", remoteName);
    }

    public ProcedureResult cloneBranchWithDepth(String url, String databaseName, String originBranch, int depth) {
        return call(url, databaseName, "-branch", originBranch, "--depth", String.valueOf(depth));
    }

    public ProcedureResult cloneBranchWithDepth(String url, String databaseName, String originBranch, String remoteName, int depth) {
        return call(url, databaseName, "-branch", originBranch, "--remote", remoteName, "--depth", String.valueOf(depth));
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.SQL_PROCEDURE_DOLT_CLONE, params);
    }
}
