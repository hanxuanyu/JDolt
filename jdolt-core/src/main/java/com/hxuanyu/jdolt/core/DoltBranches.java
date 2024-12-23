package com.hxuanyu.jdolt.core;

import com.hxuanyu.jdolt.connection.DoltConnectionManager;

/**
 * dolt branch 相关操作，包括以下方法：
 * - 查询数据库列表 SHOW DATABASES;
 * - 查询分支列表  CALL DOLT BRANCHES;
 * - 查询当前数据库 SELECT DATABASES();
 * - 查询当前分支 SELECT ACTIVE_BRANCH();
 * - 切换数据库 USE ''
 * - 切换分支 CALL DOLT_CHECKOUT
 *
 * @see <a href="https://docs.dolthub.com/sql-reference/version-control">...</a>
 * @author hanxuanyu
 * @version 1.0
 */
public class DoltBranches extends DoltRepository {
    protected DoltBranches(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

}
