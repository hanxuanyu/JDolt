package com.hxuanyu.jdolt.core.api;

import com.hxuanyu.jdolt.core.systemtable.*;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;

/**
 * 系统表操作类（从 VersionControl 内部类拆分）
 */
public class SystemTables {

    private final DoltConnectionManager connectionManager;

    public SystemTables(DoltConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public DoltSystemTable anySystemTable() {
        return DoltSystemTable.getInstance(connectionManager);
    }

    public DoltBranches branches(){
        return DoltBranches.getInstance(connectionManager);
    }

    public DoltCommits commits(){
        return DoltCommits.getInstance(connectionManager);
    }

    public DoltDocs docs(){
        return DoltDocs.getInstance(connectionManager);
    }

    public DoltProcedures procedures(){
        return DoltProcedures.getInstance(connectionManager);
    }

    public DoltRemoteBranches remoteBranches(){
        return DoltRemoteBranches.getInstance(connectionManager);
    }

    public DoltQueryCatalog queryCatalog(){
        return DoltQueryCatalog.getInstance(connectionManager);
    }

    public DoltRemotes remotes(){
        return DoltRemotes.getInstance(connectionManager);
    }

    public DoltBackups backups(){
        return DoltBackups.getInstance(connectionManager);
    }

    public DoltSchemas schemas(){
        return DoltSchemas.getInstance(connectionManager);
    }

    public DoltTags tags(){
        return DoltTags.getInstance(connectionManager);
    }

    public DoltStatistics statistics(){
        return DoltStatistics.getInstance(connectionManager);
    }

    public DoltBlame blame(){
        return DoltBlame.getInstance(connectionManager);
    }

    public DoltCommitAncestors commitAncestors(){
        return DoltCommitAncestors.getInstance(connectionManager);
    }

    public DoltHistory history(){
        return DoltHistory.getInstance(connectionManager);
    }

    public DoltLog log(){
        return DoltLog.getInstance(connectionManager);
    }

    public DoltCommitDiff commitDiff(){
        return DoltCommitDiff.getInstance(connectionManager);
    }

    public DoltColumnDiff columnDiff(){
        return DoltColumnDiff.getInstance(connectionManager);
    }

    public DoltDiff diff(){
        return DoltDiff.getInstance(connectionManager);
    }

    public DoltConflicts conflicts(){
        return DoltConflicts.getInstance(connectionManager);
    }

    public DoltSchemaConflicts schemaConflicts(){
        return DoltSchemaConflicts.getInstance(connectionManager);
    }
}
