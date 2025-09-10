package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;

public class DoltCommits extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltCommits> INSTANCES = new ConcurrentHashMap<>();

    protected DoltCommits(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltCommits getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltCommits(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_commits");
    }
}