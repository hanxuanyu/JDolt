package com.hxuanyu.jdolt.core.api;

import com.hxuanyu.jdolt.core.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.core.procedure.DoltAdd;
import com.hxuanyu.jdolt.core.procedure.DoltBranch;
import com.hxuanyu.jdolt.core.procedure.DoltCheckout;
import com.hxuanyu.jdolt.core.procedure.DoltCherryPick;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /**
     * 封装版本管理相关操作
     */
    public static class VersionControl {
        private final Logger logger = LoggerFactory.getLogger(VersionControl.class);

        DoltConnectionManager connectionManager;

        public VersionControl(DoltConnectionManager connectionManager) {
            this.connectionManager = connectionManager;
        }



        public DoltAdd doltAdd() {
            return DoltAdd.instance(connectionManager);
        }

        public DoltBranch doltBranch() {
            return DoltBranch.instance(connectionManager);
        }

        public DoltCheckout doltCheckout() {
            return DoltCheckout.instance(connectionManager);
        }

        public DoltCherryPick doltCherryPick() {
            return DoltCherryPick.instance(connectionManager);
        }

    }
}
