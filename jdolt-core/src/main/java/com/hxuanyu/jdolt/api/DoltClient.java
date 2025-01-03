package com.hxuanyu.jdolt.api;

import com.hxuanyu.jdolt.connection.DoltConnectionManager;
import com.hxuanyu.jdolt.core.VersionControl;

import javax.sql.DataSource;

/**
 * Provides a high-level client for interacting with Dolt version control operations.
 * Encapsulates the setup and management of Dolt repositories through a connection to a data source.
 */
public class DoltClient {
    private final VersionControl versionControl;

    private DoltClient(VersionControl versionControl) {
        this.versionControl = versionControl;
    }

    public static DoltClient initialize(DataSource dataSource) {
        DoltConnectionManager connectionManager = new DoltConnectionManager(dataSource);
        return new DoltClient(new VersionControl(connectionManager));
    }

    public VersionControl versionControl() {
        return versionControl;
    }
}
