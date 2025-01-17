package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.connection.DoltConnectionManager;
import com.hxuanyu.jdolt.constant.DoltSqlTemplate;
import com.hxuanyu.jdolt.core.DoltProcedure;
import com.hxuanyu.jdolt.core.DoltRepository;
import com.hxuanyu.jdolt.util.BranchNameValidator;

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
        checkBranchName(branchName);
        return call(branchName);
    }

    public boolean newBranch(String branchName, String parentBranch) {
        checkBranchName(branchName, parentBranch);
        return call(branchName, parentBranch);
    }

    public boolean copyBranch(String branchName, String originBranch) {
        checkBranchName(branchName, originBranch);
        return call("-c", branchName, originBranch);
    }

    public boolean forceCopyBranch(String branchName, String originBranch) {
        checkBranchName(branchName, originBranch);
        return call("-c", "-f", branchName, originBranch);
    }

    public boolean moveBranch(String branchName, String newBranch) {
        checkBranchName(branchName, newBranch);
        return call("-m", branchName, newBranch);
    }

    public boolean forceMoveBranch(String branchName, String newBranch) {
        checkBranchName(branchName, newBranch);
        return call("-m", "-f", branchName, newBranch);
    }

    public boolean renameBranch(String branchName, String newBranch) {
        checkBranchName(branchName, newBranch);
        return call("-m", branchName, newBranch);
    }

    public boolean forceRenameBranch(String branchName, String newBranch) {
        checkBranchName(branchName, newBranch);
        return call("-m", "-f", branchName, newBranch);
    }

    public boolean deleteBranch(String branchName) {
        checkBranchName(branchName);
        return call("-d", branchName);
    }

    public boolean forceDeleteBranch(String branchName) {
        checkBranchName(branchName);
        return call("-d", "-f", branchName);
    }

    private void checkBranchName(String... branchNames) {
        if (branchNames == null) {
            throw new IllegalArgumentException("branchNames is null");
        }
        for (String param : branchNames) {
            if (!BranchNameValidator.isValidBranchName(param)) {
                throw new IllegalArgumentException("branchName is invalid, current: " + param);
            }
        }
    }

    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildBranchSql(params);
    }
}
