package com.hxuanyu.jdolt.core.api;

import com.hxuanyu.jdolt.model.SqlExecuteResult;
import com.hxuanyu.jdolt.model.api.BranchInfo;
import com.hxuanyu.jdolt.model.api.CommitInfo;
import com.hxuanyu.jdolt.model.api.DoltLogInfo;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;

import java.util.List;
import java.util.Map;

/**
 * 封装Dolt常用的操作api
 *
 * @author hanxuanyu
 * @version 1.0
 */
public class DoltApi {

    private final VersionControl versionControl;

    public DoltApi(VersionControl versionControl) {
        this.versionControl = versionControl;
    }

    public String doltVersion() {
        SqlExecuteResult execute = versionControl.function().doltVersion().prepare().execute();
        if (execute.isNotEmpty() && execute.hasColumn("dolt_version()")) {
            return execute.getString("dolt_version()");
        }
        return null;
    }

    public String activeBranch() {
        SqlExecuteResult execute = versionControl.function().activeBranch().prepare().execute();
        if (execute.isNotEmpty() && execute.hasColumn("active_branch()")) {
            return execute.getString("active_branch()");
        }
        return null;
    }


    public List<BranchInfo> branches() {
        SqlExecuteResult query = versionControl.systemTable().branches().query();
        if (query.isNotEmpty() && query.hasColumn("name")) {
            return query.toObjectList(BranchInfo.class);
        }
        return null;
    }

    public List<CommitInfo> commits() {
        SqlExecuteResult query = versionControl.systemTable().commits().query();
        if (query.isNotEmpty() && query.hasColumn("committer")) {
            return query.toObjectList(CommitInfo.class);
        }
        return null;
    }

    public List<DoltLogInfo> logs() {
        SqlExecuteResult query = versionControl.function().doltLog().prepare().parents().decorate("short").execute();
        if (query.isNotEmpty() && query.hasColumn("commit_hash")) {
            return query.toObjectList(DoltLogInfo.class);
        }
        return null;
    }

    public List<DoltLogInfo> logs(String ref) {
        SqlExecuteResult query = versionControl.function().doltLog().prepare().revision(ref).parents().decorate("short").execute();
        if (query.isNotEmpty() && query.hasColumn("commit_hash")) {
            return query.toObjectList(DoltLogInfo.class);
        }
        return null;
    }

    public SqlExecuteResult commonSql(String sql, Object... params) {
        CommonSqlExecutor sqlExecutor = CommonSqlExecutor.getInstance(versionControl.getConnectionManager());
        SqlBuilder.SqlTemplate sqlTemplate = new SqlBuilder.SqlTemplate(sql, List.of(params));
        List<Map<String, Object>> maps = sqlExecutor.executeQueryAsList(sqlTemplate);
        if (maps != null && !maps.isEmpty()) {
            return SqlExecuteResult.success("success", maps);
        } else {
            return SqlExecuteResult.failed("failed");
        }
    }



}
