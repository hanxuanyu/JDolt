package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;

public class DoltCommitDiff extends DoltSystemTableWithSuffix{
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltCommitDiff> INSTANCES = new ConcurrentHashMap<>();

    protected DoltCommitDiff(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltCommitDiff getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltCommitDiff(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_commit_diff");
    }
}