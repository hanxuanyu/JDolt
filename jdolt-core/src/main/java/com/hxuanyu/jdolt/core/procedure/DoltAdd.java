package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.repository.DoltRepository;


/**
 * `DOLT_ADD()`
 * <p>
 * 将当前会话中的工作区更改添加到暂存区。功能与CLI中的`dolt add`完全相同，并接受相同的参数。
 * <p>
 * 将表添加到暂存区后，可以使用`DOLT_COMMIT()`提交这些更改。
 * <p>
 * 用法示例：
 * <pre>
 * CALL DOLT_ADD('-A');
 * CALL DOLT_ADD('.');
 * CALL DOLT_ADD('table1', 'table2');
 * </pre>
 *
 * <h3>选项</h3>
 *
 * <ul>
 *   <li><code>table</code>：要添加到暂存区的表。可以使用缩写<code>.</code>来添加所有表。</li>
 *   <li><code>-A</code>：暂存所有有更改的表。</li>
 * </ul>
 *
 * @author hanxuanyu
 * @version 1.0
 */
public class DoltAdd extends DoltRepository implements DoltProcedure {

    private static volatile DoltAdd instance;


    private DoltAdd(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }


    public static DoltAdd instance(DoltConnectionManager connectionManager) {
        if (instance == null) {
            synchronized (DoltBranch.class) {
                if (instance == null) {
                    instance = new DoltAdd(connectionManager);
                }
            }
        }
        return instance;
    }


    /**
     * 将所有的表添加到暂存区
     *
     * @return 执行结果，true：执行成功，false：执行失败
     */
    public boolean addAll() {
        return call("-A").isSuccess();
    }


    /**
     * 将传入的table列表添加到暂存区
     *
     * @param tables 要添加到暂存区的表列表
     * @return 执行结果，true：执行成功，false：执行失败
     */
    public boolean addTables(String... tables) {
        return call(tables).isSuccess();
    }

    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.SQL_PROCEDURE_DOLT_ADD, params);
    }
}
