package com.hxuanyu.jdolt.core.api;

import com.hxuanyu.jdolt.core.procedure.DoltCheckout;
import com.hxuanyu.jdolt.exception.DoltException;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.model.SqlExecuteResult;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;


import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * Provides a high-level client for interacting with Dolt version control operations.
 * Encapsulates the setup and management of Dolt repositories through a connection to a data source.
 */
public class DoltClient {
    private final VersionControl versionControl;
    private final DoltApi doltApi;

    private DoltClient(VersionControl versionControl) {
        this.versionControl = versionControl;
        this.doltApi = new DoltApi(versionControl);
    }

    public static DoltClient initialize(DataSource dataSource) {
        DoltConnectionManager connectionManager = new DoltConnectionManager(dataSource);
        return new DoltClient(new VersionControl(connectionManager));
    }

    public VersionControl versionControl() {
        return versionControl;
    }

    public DoltApi api() {
        if (!isInitialized()) {
            throw new DoltException("DoltClient is not initialized");
        }
        return doltApi;
    }


    public DoltApi api(String branch) {
        if (!isInitialized()) {
            throw new DoltException("DoltClient is not initialized");
        }
        SqlExecuteResult executeResult = versionControl.procedure().doltCheckout().prepare().checkout(branch).execute();
        if (executeResult.isSuccess()) {
            return doltApi;
        } else {
            throw new DoltException("Dolt checkout failed, branch:" + branch);
        }
    }

    public boolean isInitialized() {
        return versionControl().isInitialized();
    }

}
