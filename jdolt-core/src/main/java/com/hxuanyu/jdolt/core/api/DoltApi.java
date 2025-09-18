package com.hxuanyu.jdolt.core.api;

import com.hxuanyu.jdolt.model.SqlExecuteResult;
import com.hxuanyu.jdolt.model.api.BranchInfo;
import com.hxuanyu.jdolt.model.api.CommitInfo;

import java.util.List;

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


}
