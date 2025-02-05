package com.hxuanyu.jdolt.core.api;

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


    /**
     * dolt_add() 存储过程，支持传入要纳入版本管理的table数组或 "."、"-A"
     *
     * @return doltAdd存储过程对象，对相关操作进行封装
     */
    public DoltAdd doltAdd() {
        return DoltAdd.getInstance(connectionManager);
    }

    /**
     * dolt_branch()存储过程，支持对dolt分支进行增、删、改操作
     *
     * @return doltBranch存储过程对象，对相关操作进行封装
     */
    public DoltBranch doltBranch() {
        return DoltBranch.instance(connectionManager);
    }

    public DoltCheckout doltCheckout() {
        return DoltCheckout.instance(connectionManager);
    }

    public DoltCherryPick doltCherryPick() {
        return DoltCherryPick.instance(connectionManager);
    }

    public DoltClean doltClean() {
        return DoltClean.instance(connectionManager);
    }

}
