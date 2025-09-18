package com.hxuanyu.jdolt.core.api;

import com.hxuanyu.jdolt.model.SqlExecuteResult;

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




}
