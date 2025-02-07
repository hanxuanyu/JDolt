package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.annotation.MethodInvokeRequired;
import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.AbstractProcedureParamBuilder;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;


/**
 * `DOLT_COMMIT()`
 * <p>
 * 将已暂存的表提交到 HEAD。其功能与 `dolt commit` 完全相同，每个参数紧跟在标志后面。
 * <p>
 * `DOLT_COMMIT()` 还会提交当前事务。
 *
 * <pre>
 * CALL DOLT_COMMIT('-a', '-m', '这是一次提交');
 * CALL DOLT_COMMIT('-m', '这是一次提交');
 * CALL DOLT_COMMIT('-m', '这是一次提交', '--author', 'John Doe <johndoe@example.com>');
 * </pre>
 *
 * <h3>选项</h3>
 *
 * <ul>
 *   <li><code>-m</code>, <code>--message</code>：使用给定的 <code>&lt;msg&gt;</code> 作为提交信息。<strong>必需</strong></li>
 *   <li><code>-a</code>, <code>--all</code>：在提交之前暂存所有已修改的表（但不包括新创建的表）。</li>
 *   <li><code>-A</code>, <code>--ALL</code>：在提交之前暂存所有表（包括新创建的表）。</li>
 *   <li><code>--allow-empty</code>：允许记录与其唯一父提交拥有完全相同数据的提交。通常这是一种错误，因此默认情况下会禁用此功能。此选项可绕过该安全限制。</li>
 *   <li><code>--skip-empty</code>：仅在有更改需要提交时记录提交。如果没有暂存的更改需要提交，提交操作将不会执行，而不是报错。如果 <code>--skip-empty</code> 与 <code>--allow-empty</code> 一起使用，将会抛出错误。</li>
 *   <li><code>--date</code>：指定提交中使用的日期。如果未指定，则使用当前系统时间。</li>
 *   <li><code>--author</code>：使用标准格式 "A U Thor author@example.com" 指定提交的作者。</li>
 * </ul>
 *
 * <h3>输出模式</h3>
 *
 * <pre>
 * +-------+------+----------------------------+
 * | Field | Type | Description                |
 * +-------+------+----------------------------+
 * | hash  | text | 创建的提交的哈希值         |
 * +-------+------+----------------------------+
 * </pre>
 *
 * <h3>示例</h3>
 *
 * <pre>
 * -- 设置当前会话的数据库
 * USE mydb;
 *
 * -- 进行修改
 * UPDATE table
 * SET column = "new value"
 * WHERE pk = "key";
 *
 * -- 暂存所有更改并提交
 * CALL DOLT_COMMIT('-a', '-m', '这是一次提交', '--author', 'John Doe <johndoe@example.com>');
 * </pre>
 */
public class DoltCommit extends DoltRepository implements DoltProcedure<DoltCommit.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltCommit> INSTANCES = new ConcurrentHashMap<>();

    private DoltCommit(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltCommit getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltCommit(connectionManager));
    }

    public static class Params extends AbstractProcedureParamBuilder<Params> {

        protected Params(DoltProcedure<Params> doltProcedure) {
            super(Params.class, doltProcedure);
        }


        @MethodInvokeRequired
        @MethodMutexGroup({"message"})
        public Params message(String commitMessage) {
            validator.checkAndMark("message");
            addFlags("--message", commitMessage);
            return this;
        }

        @MethodMutexGroup({"stageAll"})
        public Params stageAll() {
            validator.checkAndMark("stageAll");
            addFlags("--all");
            return this;
        }

        @MethodMutexGroup({"stageAllWithNewTable"})
        public Params stageAllWithNewTable() {
            validator.checkAndMark("stageAllWithNewTable");
            addFlags("--ALL");
            return this;
        }

        @MethodMutexGroup({"allowEmpty", "skipEmpty"})
        public Params allowEmpty() {
            validator.checkAndMark("allowEmpty");
            addFlags("--allow-empty");
            return this;
        }

        @MethodMutexGroup({"skipEmpty", "allowEmpty"})
        public Params skipEmpty() {
            validator.checkAndMark("skipEmpty");
            addFlags("--skip-empty");
            return this;
        }

        @MethodMutexGroup({"withDate"})
        public Params withDate(LocalDateTime dateTime) {
            validator.checkAndMark("withDate");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            addFlags("--date", dateTime.format(formatter));
            return this;
        }

        @MethodMutexGroup({"withAuthor"})
        public Params withAuthor(String authorName, String mail) {
            validator.checkAndMark("withAuthor");
            addFlags("--author", (authorName + " <" + mail + ">"));
            return this;
        }


    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getProcedureTemplate("dolt_commit"), params);
    }

}
