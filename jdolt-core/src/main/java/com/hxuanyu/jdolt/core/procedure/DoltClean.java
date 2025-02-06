package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.model.ProcedureResult;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.ArrayUtils;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;

import java.util.Arrays;

/**
 * ## `DOLT_CLEAN()`
 * <p>
 * 删除工作集中的未跟踪表。
 * <p>
 * 如果传入表名作为参数，则仅删除指定的未跟踪表。
 * <p>
 * 使用 `--dry-run` 标志，可以测试删除未跟踪表是否会返回零状态。
 * <p>
 * 示例用法：
 * <pre>
 * CALL DOLT_CLEAN();
 * CALL DOLT_CLEAN('untracked-table');
 * CALL DOLT_CLEAN('--dry-run');
 * </pre>
 * <p>
 * ### 选项
 * <p>
 * `--dry-run`：测试从工作集中删除未跟踪表。
 * <p>
 * ### 输出模式
 *
 * <pre>
 * +--------+------+---------------------------+
 * | 字段   | 类型 | 描述                      |
 * +--------+------+---------------------------+
 * | status | int  | 成功为0，失败为1          |
 * +--------+------+---------------------------+
 * </pre>
 */
public class DoltClean extends DoltRepository implements DoltProcedure {

    private static volatile DoltClean instance;

    protected DoltClean(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltClean instance(DoltConnectionManager connectionManager) {
        if (instance == null) {
            synchronized (DoltClean.class) {
                if (instance == null) {
                    instance = new DoltClean(connectionManager);
                }
            }
        }
        return instance;
    }

    /**
     * 空参调用
     *
     * @return 调用结果
     */
    public ProcedureResult call() {
        return call(new String[]{});
    }

    /**
     * 测试清理操作是否会返回成功，建议在call前尝试调用一次
     *
     * @param params 要清理的表
     * @return 调用结果
     */
    public ProcedureResult test(String... params) {
        return call(ArrayUtils.add(params, "--dry-run"));
    }

    /**
     * 测试清理操作是否会返回成功，建议在call前尝试调用一次
     *
     * @return 调用结果
     */
    public ProcedureResult test() {
        return call("--dry-run");
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getProcedureTemplate("dolt_clean")
                , params);
    }
}
