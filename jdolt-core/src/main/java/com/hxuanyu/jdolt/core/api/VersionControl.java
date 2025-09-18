package com.hxuanyu.jdolt.core.api;

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
        this.procedures = new Procedures(connectionManager);
        this.functions = new Functions(connectionManager);
        this.systemTables = new SystemTables(connectionManager);
    }

    public boolean isInitialized() {
        return connectionManager.isInitialized();
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
}
