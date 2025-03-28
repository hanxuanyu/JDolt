package com.hxuanyu.jdolt.core.api;

import com.hxuanyu.jdolt.core.function.info.*;
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

    public DolrVerifyConstraints doltVerifyConstraints() {
        return DolrVerifyConstraints.getInstance(connectionManager);
    }

    //---------------------------------------------------------------

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

    public DoltConnectionManager getConnectionManager() {
        return connectionManager;
    }
}
