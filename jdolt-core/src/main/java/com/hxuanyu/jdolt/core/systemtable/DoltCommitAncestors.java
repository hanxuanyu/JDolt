package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;

public class DoltCommitAncestors extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltCommitAncestors> INSTANCES = new ConcurrentHashMap<>();

    protected DoltCommitAncestors(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltCommitAncestors getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltCommitAncestors(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_commit_ancestors");
    }
}