package com.hxuanyu.jdolt.core.api;

import com.hxuanyu.jdolt.core.function.info.*;
import com.hxuanyu.jdolt.core.function.table.*;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;

/**
 * 函数操作类（从 VersionControl 内部类拆分）
 */
public class Functions {

    private final DoltConnectionManager connectionManager;

    public Functions(DoltConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

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
