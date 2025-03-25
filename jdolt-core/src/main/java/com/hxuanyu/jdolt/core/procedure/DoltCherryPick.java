package com.hxuanyu.jdolt.core.procedure;


import com.hxuanyu.jdolt.annotation.MethodAllowGroup;
import com.hxuanyu.jdolt.annotation.MethodDependsOn;
import com.hxuanyu.jdolt.annotation.MethodExclusive;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;
import com.hxuanyu.jdolt.util.builder.AbstractProcedureParamBuilder;

import java.util.concurrent.ConcurrentHashMap;

/**
 * DOLT_CHERRY_PICK()
 * <p>
 * 应用现有提交引入的更改。
 * <p>
 * 从现有提交应用更改，并从当前 HEAD 创建一个新的提交。
 * <p>
 * 与 CLI 中的 {@link ../../cli/cli.md#dolt-cherry-pick dolt cherry-pick}[命令](../../cli/cli.md#dolt-cherry-pick)
 * 完全一致，并具有相同的注意事项和限制。
 *
 * <pre>{@code
 * CALL DOLT_CHERRY_PICK('my-existing-branch~2');
 * CALL DOLT_CHERRY_PICK('qj6ouhjvtrnp1rgbvajaohmthoru2772');
 * }</pre>
 *
 * <h3>选项</h3>
 *
 * <ul>
 * <li><code>--abort</code>：<br>中止当前的冲突解决过程，并恢复 cherry-pick 操作过程中所有的更改。</li>
 * <li><code>--allow-empty</code>：<br>允许 cherry-pick 空提交。请注意，使用此选项仅保留最初为空的提交。
 * 由于之前的提交导致变为空的提交，将会导致 cherry-pick 失败。</li>
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
 *
 * <h3>示例</h3>
 * <p>
 * 以下示例展示了 <code>main</code> 和 <code>mybranch</code> 分支的设置：
 *
 * <pre>{@code
 * -- 切换到 main 分支
 * CALL DOLT_CHECKOUT('main');
 *
 * -- 查看提交日志
 * SELECT commit_hash, message FROM dolt_log;
 * +----------------------------------+----------------------------+
 * | commit_hash                      | message                    |
 * +----------------------------------+----------------------------+
 * | 7e2q0hibo2m2af874i4e7isgnum74j4m | 创建新表                   |
 * | omuqq67att6vfnka94drdallu4983gnr | 初始化数据仓库             |
 * +----------------------------------+----------------------------+
 * 2 rows in set (0.00 sec)
 *
 * -- 查看表
 * SELECT * FROM mytable;
 * Empty set (0.00 sec)
 *
 * -- 切换到新分支
 * CALL DOLT_CHECKOUT('mybranch');
 *
 * -- 查看提交日志
 * SELECT commit_hash, message FROM dolt_log;
 * +----------------------------------+----------------------------+
 * | commit_hash                      | message                    |
 * +----------------------------------+----------------------------+
 * | 577isdjbq1951k2q4dqhli06jlauo51p | 向表中添加 3、4、5         |
 * | k318tpmqn4l97ofpaerato9c3m70lc14 | 向表中添加 1、2            |
 * | 7e2q0hibo2m2af874i4e7isgnum74j4m | 创建新表                   |
 * | omuqq67att6vfnka94drdallu4983gnr | 初始化数据仓库             |
 * +----------------------------------+----------------------------+
 * 4 rows in set (0.00 sec)
 *
 * -- 查看表
 * SELECT * FROM mytable;
 * +---+
 * | a |
 * +---+
 * | 1 |
 * | 2 |
 * | 3 |
 * | 4 |
 * | 5 |
 * +---+
 * 5 rows in set (0.00 sec)
 * }</pre>
 * <p>
 * 我们希望仅 cherry-pick 提交哈希 <code>'k318tpmqn4l97ofpaerato9c3m70lc14'</code> 引入的更改，
 * 该提交将 <code>1</code> 和 <code>2</code> 插入到表中。指定 <code>'mybranch~1'</code> 代替提交哈希也可以。
 *
 * <pre>{@code
 * -- 切换到 main 分支
 * CALL DOLT_CHECKOUT('main');
 *
 * -- 执行 cherry-pick 操作
 * CALL DOLT_CHERRY_PICK('k318tpmqn4l97ofpaerato9c3m70lc14');
 * +----------------------------------+
 * | hash                             |
 * +----------------------------------+
 * | mh518gdgbsut8m705b7b5rie9neq9uaj |
 * +----------------------------------+
 * 1 row in set (0.02 sec)
 *
 * mydb> SELECT * FROM mytable;
 * +---+
 * | a |
 * +---+
 * | 1 |
 * | 2 |
 * +---+
 * 2 rows in set (0.00 sec)
 *
 * mydb> SELECT commit_hash, message FROM dolt_log;
 * +----------------------------------+----------------------------+
 * | commit_hash                      | message                    |
 * +----------------------------------+----------------------------+
 * | mh518gdgbsut8m705b7b5rie9neq9uaj | 向表中添加 1、2            |
 * | 7e2q0hibo2m2af874i4e7isgnum74j4m | 创建新表                   |
 * | omuqq67att6vfnka94drdallu4983gnr | 初始化数据仓库             |
 * +----------------------------------+----------------------------+
 * 3 rows in set (0.00 sec)
 * }</pre>
 */
public class DoltCherryPick extends DoltRepository implements DoltProcedure<DoltCherryPick.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltCherryPick> INSTANCES = new ConcurrentHashMap<>();

    private DoltCherryPick(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltCherryPick getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltCherryPick(connectionManager));
    }

    public static class Params extends AbstractProcedureParamBuilder<Params> {

        protected Params(DoltProcedure<Params> doltProcedure) {
            super(Params.class, doltProcedure);
        }


        @MethodAllowGroup({"withCommitHash", "allowEmpty"})
        public Params withCommitHash(String commitId) {
            validator.checkAndMark("withCommitHash");
            addFlags(commitId);
            return this;
        }

        @MethodAllowGroup({"withRelativeRef", "allowEmpty"})
        public Params withRelativeRef(String branch, int generation) {
            validator.checkAndMark("withRelativeRef");
            addFlags(branch + "~" + generation);
            return this;
        }

        @MethodExclusive
        public Params abort() {
            validator.checkAndMark("abort");
            addFlags("--abort");
            return this;
        }

        @MethodDependsOn({"withRelativeRef", "withCommitHash"})
        public Params allowEmpty() {
            validator.checkAndMark("allowEmpty");
            addFlags("--allow-empty");
            return this;
        }


    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getProcedureTemplate("dolt_cherry_pick"), params);
    }

}
