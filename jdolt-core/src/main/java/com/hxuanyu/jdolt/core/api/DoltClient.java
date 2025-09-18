package com.hxuanyu.jdolt.core.api;

import com.hxuanyu.jdolt.exception.DoltException;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;


import javax.sql.DataSource;

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

    public boolean isInitialized() {
        return versionControl().isInitialized();
    }

}
