package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.connection.DoltConnectionManager;
import com.hxuanyu.jdolt.constant.DoltSqlTemplate;
import com.hxuanyu.jdolt.core.DoltProcedure;
import com.hxuanyu.jdolt.core.DoltRepository;

/**
 * Dolt branch相关操作封装
 *
 * @author hanxuanyu
 * @version 1.0
 */
public class DoltBranch extends DoltRepository implements DoltProcedure {

    private static volatile DoltBranch instance;

    protected DoltBranch(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltBranch instance(DoltConnectionManager connectionManager) {
        if (instance == null) {
            synchronized (DoltBranch.class) {
                if (instance == null) {
                    instance = new DoltBranch(connectionManager);
                }
            }
        }
        return instance;
    }


    public boolean newBranch(String branchName) {
        return call(branchName);
    }

    public boolean newBranch(String branchName, String parentBranch) {
        return call(branchName, parentBranch);
    }

    public boolean copyBranch(String branchName, String originBranch) {
        return call("-c", branchName, originBranch);
    }

    public boolean forceCopyBranch(String branchName, String originBranch) {
        return call("-c", "-f", branchName, originBranch);
    }

    public boolean moveBranch(String branchName, String newBranch) {
        return call("-m", branchName, newBranch);
    }

    public boolean forceMoveBranch(String branchName, String newBranch) {
        return call("-m", "-f", branchName, newBranch);
    }

    public boolean renameBranch(String branchName, String newBranch) {
        return call("-m", branchName, newBranch);
    }

    public boolean forceRenameBranch(String branchName, String newBranch) {
        return call("-m", "-f", branchName, newBranch);
    }

    public boolean deleteBranch(String branchName) {
        return call("-d", branchName);
    }

    public boolean forceDeleteBranch(String branchName) {
        return call("-d", "-f", branchName);
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildBranchSql(params);
    }
}
