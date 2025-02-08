package com.hxuanyu.jdolt.core.api;

import com.hxuanyu.jdolt.core.function.ActiveBranch;
import com.hxuanyu.jdolt.core.function.DoltMergeBase;
import com.hxuanyu.jdolt.core.procedure.*;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 封装版本管理相关操作
 */
public class VersionControl {
    private final Logger logger = LoggerFactory.getLogger(VersionControl.class);

    DoltConnectionManager connectionManager;

    public VersionControl(DoltConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }


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

    public ActiveBranch activeBranch() {
        return ActiveBranch.getInstance(connectionManager);
    }

    public DoltMergeBase doltMergeBase() {
        return DoltMergeBase.getInstance(connectionManager);
    }

    public DoltConnectionManager getConnectionManager() {
        return connectionManager;
    }
}
