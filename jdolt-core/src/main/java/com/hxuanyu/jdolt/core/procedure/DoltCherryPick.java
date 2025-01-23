package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.model.ProcedureResult;


/**
 * 应用现有提交引入的更改。
 * <p>
 * 从现有提交应用更改，并从当前 HEAD 创建一个新的提交。
 * <p>
 * 与 CLI 中的 {@link ../../cli/cli.md#dolt-cherry-pick dolt cherry-pick命令} 完全一致，并具有相同的注意事项和限制。
 * <p>
 * 示例用法:
 *
 * <pre>{@code
 * CALL DOLT_CHERRY_PICK('my-existing-branch~2');
 * CALL DOLT_CHERRY_PICK('qj6ouhjvtrnp1rgbvajaohmthoru2772');
 * }</pre>
 *
 * <h3>选项</h3>
 *
 * <ul>
 *     <li><strong>--abort</strong>:<br>
 *     中止当前的冲突解决过程，并恢复 cherry-pick 操作过程中所有的更改。</li>
 *
 *     <li><strong>--allow-empty</strong>:<br>
 *     允许 cherry-pick 空提交。请注意，使用此选项仅保留最初为空的提交。由于之前的提交导致变为空的提交，将会导致 cherry-pick 失败。</li>
 * </ul>
 *
 * <h3>输出模式</h3>
 *
 * <pre>{@code
 * +-----------------------+------+---------------------------------+
 * | Field                 | Type | Description                     |
 * +-----------------------+------+---------------------------------+
 * | hash                  | text | 应用提交的哈希值                |
 * | data_conflicts        | int  | 数据冲突的数量                  |
 * | schema_conflicts      | int  | 模式冲突的数量                  |
 * | constraint_violations | int  | 约束冲突的数量                  |
 * +-----------------------+------+---------------------------------+
 * }</pre>
 */
public class DoltCherryPick extends DoltRepository implements DoltProcedure {

    private static volatile DoltCherryPick instance;

    protected DoltCherryPick(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltCherryPick instance(DoltConnectionManager connectionManager) {
        if (instance == null) {
            synchronized (DoltCherryPick.class) {
                if (instance == null) {
                    instance = new DoltCherryPick(connectionManager);
                }
            }
        }
        return instance;
    }

    public ProcedureResult abort() {
        return call("--abort");
    }

    public ProcedureResult callAllowEmpty(String sourceRef) {
        return call("--allow-empty", sourceRef);
    }

    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.SQL_PROCEDURE_DOLT_CHERRY_PICK, params);
    }
}






















