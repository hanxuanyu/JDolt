package com.hxuanyu.jdolt.core.api;

import com.hxuanyu.jdolt.core.function.info.*;
import com.hxuanyu.jdolt.core.function.table.*;
import com.hxuanyu.jdolt.core.procedure.*;
import com.hxuanyu.jdolt.core.systemtable.*;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 封装版本管理相关操作
 */
public class VersionControl {
    private final Logger logger = LoggerFactory.getLogger(VersionControl.class);

    DoltConnectionManager connectionManager;
    
    private final Procedures procedures;
    private final Functions functions;
    private final SystemTables systemTables;

    public VersionControl(DoltConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.procedures = new Procedures();
        this.functions = new Functions();
        this.systemTables = new SystemTables();
    }

    /**
     * 获取存储过程操作
     */
    public Procedures procedure() {
        return procedures;
    }

    /**
     * 获取函数操作
     */
    public Functions function() {
        return functions;
    }

    /**
     * 获取系统表操作
     */
    public SystemTables systemTable() {
        return systemTables;
    }

    public DoltConnectionManager getConnectionManager() {
        return connectionManager;
    }

    /**
     * 存储过程操作类
     */
    public class Procedures {
        
        public DoltAdd doltAdd() {
            return DoltAdd.getInstance(connectionManager);
        }

        public DoltBackup doltBackup() {
            return DoltBackup.getInstance(connectionManager);
        }

        public DoltBranch doltBranch() {
            return DoltBranch.getInstance(connectionManager);
        }

        public DoltCheckout doltCheckout() {
            return DoltCheckout.getInstance(connectionManager);
        }

        public DoltCherryPick doltCherryPick() {
            return DoltCherryPick.getInstance(connectionManager);
        }

        public DoltClean doltClean() {
            return DoltClean.getInstance(connectionManager);
        }

        public DoltClone doltClone() {
            return DoltClone.getInstance(connectionManager);
        }

        public DoltCommit doltCommit() {
            return DoltCommit.getInstance(connectionManager);
        }

        public DoltConflictsResolve doltConflictsResolve() {
            return DoltConflictsResolve.getInstance(connectionManager);
        }

        public DoltFetch doltFetch() {
            return DoltFetch.getInstance(connectionManager);
        }

        public DoltGc doltGc() {
            return DoltGc.getInstance(connectionManager);
        }

        public DoltMerge doltMerge() {
            return DoltMerge.getInstance(connectionManager);
        }

        public DoltPull doltPull() {
            return DoltPull.getInstance(connectionManager);
        }

        public DoltPurgeDroppedDatabases doltPurgeDroppedDatabases() {
            return DoltPurgeDroppedDatabases.getInstance(connectionManager);
        }

        public DoltPush doltPush() {
            return DoltPush.getInstance(connectionManager);
        }

        public DoltRebase doltRebase() {
            return DoltRebase.getInstance(connectionManager);
        }

        public DoltRemote doltRemote() {
            return DoltRemote.getInstance(connectionManager);
        }

        public DoltReset doltReset() {
            return DoltReset.getInstance(connectionManager);
        }

        public DoltRevert doltRevert() {
            return DoltRevert.getInstance(connectionManager);
        }

        public DoltTag doltTag() {
            return DoltTag.getInstance(connectionManager);
        }

        public DoltUnDrop doltUnDrop() {
            return DoltUnDrop.getInstance(connectionManager);
        }

        public DoltVerifyConstraints doltVerifyConstraints() {
            return DoltVerifyConstraints.getInstance(connectionManager);
        }
    }

    /**
     * 函数操作类
     */
    public class Functions {
        
        // Info functions
        public ActiveBranch activeBranch() {
            return ActiveBranch.getInstance(connectionManager);
        }

        public DoltInfoMergeBase doltMergeBase() {
            return DoltInfoMergeBase.getInstance(connectionManager);
        }

        public DoltInfoHashOf doltHashOf() {
            return DoltInfoHashOf.getInstance(connectionManager);
        }

        public DoltInfoHashOfTable doltHashOfTable() {
            return DoltInfoHashOfTable.getInstance(connectionManager);
        }

        public DoltInfoHashOfDB doltHashOfDB() {
            return DoltInfoHashOfDB.getInstance(connectionManager);
        }

        public DoltInfoVersion doltVersion() {
            return DoltInfoVersion.getInstance(connectionManager);
        }

        public HasAncestor hasAncestor() {
            return HasAncestor.getInstance(connectionManager);
        }

        public LastInsertUUID lastInsertUUID() {
            return LastInsertUUID.getInstance(connectionManager);
        }

        // Table functions
        public DoltDiff doltDiff() {
            return DoltDiff.getInstance(connectionManager);
        }

        public DoltDiffStat doltDiffStat() {
            return DoltDiffStat.getInstance(connectionManager);
        }

        public DoltPatch doltPatch() {
            return DoltPatch.getInstance(connectionManager);
        }
        
        public DoltLog doltLog() {
            return DoltLog.getInstance(connectionManager);
        }

        public DoltDiffSummary doltDiffSummary() {
            return DoltDiffSummary.getInstance(connectionManager);
        }

        public DoltRefLog doltRefLog() {
            return DoltRefLog.getInstance(connectionManager);
        }
        
        public DoltSchemaDiff doltSchemaDiff() {
            return DoltSchemaDiff.getInstance(connectionManager);
        }
        
        public DoltQueryDiff doltQueryDiff() {
            return DoltQueryDiff.getInstance(connectionManager);
        }
    }

    /**
     * 系统表操作类
     */
    public class SystemTables {
        
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
    }
}
