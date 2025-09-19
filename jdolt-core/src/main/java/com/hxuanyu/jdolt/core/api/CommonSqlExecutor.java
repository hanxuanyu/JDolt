package com.hxuanyu.jdolt.core.api;

import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;

/**
 * SQL通用执行器
 *
 * @author hanxuanyu
 * @version 1.0
 */
public class CommonSqlExecutor extends DoltRepository {
    private CommonSqlExecutor(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static CommonSqlExecutor getInstance(DoltConnectionManager connectionManager) {
        return new CommonSqlExecutor(connectionManager);
    }
}
