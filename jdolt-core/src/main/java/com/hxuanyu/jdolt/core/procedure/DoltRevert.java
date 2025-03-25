package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.annotation.MethodInvokeRequired;
import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractProcedureParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;

import java.util.concurrent.ConcurrentHashMap;


/**
 * ## `DOLT_REVERT()`
 * <p>
 * 撤销一个提交或一组提交中引入的更改。从当前的 HEAD 创建一个新提交，该提交会逆转所有指定提交中的更改。
 * 如果提供了多个提交，则按给定顺序应用它们。
 *
 * <pre>{@code
 * CALL DOLT_REVERT('gtfv1qhr5le61njimcbses9oom0de41e');
 * CALL DOLT_REVERT('HEAD~2');
 * CALL DOLT_REVERT('HEAD', '--author=reverter@rev.ert');
 * }</pre>
 * <p>
 * ### 选项
 * <p>
 * `--author=<author>`：使用标准格式 `A U Thor <author@example.com>` 指定明确的作者。
 * <p>
 * ### 输出模式
 *
 * <pre>{@code
 * +--------+------+---------------------------+
 * | 字段   | 类型 | 描述                      |
 * +--------+------+---------------------------+
 * | status | int  | 成功为 0，失败为 1         |
 * +--------+------+---------------------------+
 * }</pre>
 * <p>
 * ### 示例
 *
 * <pre>{@code
 * -- 创建一个表并通过多个提交添加数据
 * CREATE TABLE t1(pk INT PRIMARY KEY, c VARCHAR(255));
 * CALL dolt_add("t1")
 * CALL dolt_commit("-m", "创建表 t1");
 * INSERT INTO t1 VALUES(1, "a"), (2, "b"), (3, "c");
 * CALL dolt_commit("-am", "添加一些数据");
 * insert into t1 VALUES(10, "aa"), (20, "bb"), (30, "cc");
 * CALL dolt_commit("-am", "添加更多数据");
 *
 * -- 检查当前 HEAD 提交之前的提交中所做的更改
 * SELECT to_pk, to_c, to_commit, diff_type FROM dolt_diff_t1 WHERE to_commit=hashof("HEAD~1");
 * +-------+------+----------------------------------+-----------+
 * | to_pk | to_c | to_commit                        | diff_type |
 * +-------+------+----------------------------------+-----------+
 * | 1     | a    | fc4fks6jutcnee9ka6458nmuot7rl1r2 | added     |
 * | 2     | b    | fc4fks6jutcnee9ka6458nmuot7rl1r2 | added     |
 * | 3     | c    | fc4fks6jutcnee9ka6458nmuot7rl1r2 | added     |
 * +-------+------+----------------------------------+-----------+
 *
 * -- 撤销当前 HEAD 提交之前的提交
 * CALL dolt_revert("HEAD~1");
 *
 * -- 查看由 dolt_revert 创建的新提交
 * SELECT commit_hash, message FROM dolt_log limit 1;
 * +----------------------------------+---------------------------+
 * | commit_hash                      | message                   |
 * +----------------------------------+---------------------------+
 * | vbevrdghj3in3napcgdsch0mq7f8en4v | Revert "Adding some data" |
 * +----------------------------------+---------------------------+
 *
 * -- 查看撤销提交所做的具体更改
 * SELECT from_pk, from_c, to_commit, diff_type FROM dolt_diff_t1 WHERE to_commit=hashof("HEAD");
 * +---------+--------+----------------------------------+-----------+
 * | from_pk | from_c | to_commit                        | diff_type |
 * +---------+--------+----------------------------------+-----------+
 * | 1       | a      | vbevrdghj3in3napcgdsch0mq7f8en4v | removed   |
 * | 2       | b      | vbevrdghj3in3napcgdsch0mq7f8en4v | removed   |
 * | 3       | c      | vbevrdghj3in3napcgdsch0mq7f8en4v | removed   |
 * +---------+--------+----------------------------------+-----------+
 * }</pre>
 */
public class DoltRevert extends DoltRepository implements DoltProcedure<DoltRevert.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltRevert> INSTANCES = new ConcurrentHashMap<>();

    private DoltRevert(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltRevert getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltRevert(connectionManager));
    }

    public static class Params extends AbstractProcedureParamBuilder<Params> {

        protected Params(DoltProcedure<Params> doltProcedure) {
            super(Params.class, doltProcedure);
        }


        @MethodInvokeRequired
        public Params withRevision(String revision) {
            validator.checkAndMark("withRevision");
            addFlags(revision);
            return this;
        }

        @MethodMutexGroup({"withRelativeRevision"})
        public Params withRelativeRevision(String revision, int generation) {
            validator.checkAndMark("withRelativeRevision");
            addFlags(revision + "~" + generation);
            return this;
        }

        @MethodMutexGroup({"withAuthor"})
        public Params withAuthor(String author, String mail) {
            validator.checkAndMark("withAuthor");
            addFlags("--author", author + " <" + mail + ">");
            return this;
        }

    }

    @Override
    public Params prepare() {
        return new Params(this);
    }



    @Override
    public SqlBuilder.SqlTemplate buildSqlTemplate(String... params) {
        return SqlBuilder.callProcedure("dolt_revert")
                .withParams(params)
                .build();
    }
}