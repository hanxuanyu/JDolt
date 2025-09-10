package com.hxuanyu.jdolt.core.systemtable;

import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import java.util.concurrent.ConcurrentHashMap;

public class DoltBranches extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltBranches> INSTANCES = new ConcurrentHashMap<>();

    protected DoltBranches(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltBranches getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltBranches(connectionManager));
    }

    @Override
    public DoltSystemTable.Params prepare() {
        return new DoltSystemTable.Params(this).from("dolt_branches");
    }
}