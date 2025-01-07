package com.hxuanyu.jdolt.core;

import com.hxuanyu.jdolt.connection.DoltConnectionManager;
import com.hxuanyu.jdolt.constant.DoltSqlTemplate;
import com.hxuanyu.jdolt.core.procedure.DoltAdd;
import com.hxuanyu.jdolt.util.CommonParamValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Arrays;

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
        return DoltAdd.instance(connectionManager);
    }


}
