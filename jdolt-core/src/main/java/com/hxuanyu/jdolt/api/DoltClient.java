package com.hxuanyu.jdolt.api;

import com.hxuanyu.jdolt.connection.DoltConnectionManager;
import com.hxuanyu.jdolt.core.DoltVersionControl;

import javax.sql.DataSource;

/**
 * Provides a high-level client for interacting with Dolt version control operations.
 * Encapsulates the setup and management of Dolt repositories through a connection to a data source.
 */
public class DoltClient {
    private final DoltVersionControl versionControl;

    private DoltClient(DoltVersionControl versionControl) {
        this.versionControl = versionControl;
    }

    public static DoltClient initialize(DataSource dataSource) {
        DoltConnectionManager connectionManager = new DoltConnectionManager(dataSource);
        return new DoltClient(new DoltVersionControl(connectionManager));
    }

    public DoltVersionControl versionControl() {
        return versionControl;
    }
}
