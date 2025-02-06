package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.model.ProcedureResult;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * `DOLT_COMMIT()`
 * <p>
 * 将已暂存的表提交到 HEAD。其功能与 `dolt commit` 完全相同，每个参数紧跟在标志后面。
 * <p>
 * `DOLT_COMMIT()` 还会提交当前事务。
 * <p>
 * 示例用法：
 * <pre>{@code
 * CALL DOLT_COMMIT('-a', '-m', '这是一次提交');
 * CALL DOLT_COMMIT('-m', '这是一次提交');
 * CALL DOLT_COMMIT('-m', '这是一次提交', '--author', 'John Doe <johndoe@example.com>');
 * }</pre>
 *
 * <h2>选项</h2>
 * <ul>
 *   <li><code>-m</code>, <code>--message</code>：使用给定的 <code>&lt;msg&gt;</code> 作为提交信息。<strong>必需</strong></li>
 *   <li><code>-a</code>, <code>--all</code>：在提交之前暂存所有已修改的表（但不包括新创建的表）。</li>
 *   <li><code>-A</code>, <code>--ALL</code>：在提交之前暂存所有表（包括新创建的表）。</li>
 *   <li><code>--allow-empty</code>：允许记录与其唯一父提交拥有完全相同数据的提交。
 *       通常这是一种错误，因此默认情况下会禁用此功能。此选项可绕过该安全限制。</li>
 *   <li><code>--skip-empty</code>：仅在有更改需要提交时记录提交。
 *       如果没有暂存的更改需要提交，提交操作将不会执行，而不是报错。
 *       如果 <code>--skip-empty</code> 与 <code>--allow-empty</code> 一起使用，将会抛出错误。</li>
 *   <li><code>--date</code>：指定提交中使用的日期。如果未指定，则使用当前系统时间。</li>
 *   <li><code>--author</code>：使用标准格式 "A U Thor author@example.com" 指定提交的作者。</li>
 * </ul>
 *
 * <h2>输出模式</h2>
 * <pre>
 * +-------+------+----------------------------+
 * | Field | Type | Description                |
 * +-------+------+----------------------------+
 * | hash  | text | 创建的提交的哈希值         |
 * +-------+------+----------------------------+
 * </pre>
 *
 * <h2>示例</h2>
 * <pre>{@code
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
 * }</pre>
 */
public class DoltCommit extends DoltRepository implements DoltProcedure {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltCommit> INSTANCES = new ConcurrentHashMap<>();

    private DoltCommit(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltCommit getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltCommit(connectionManager));
    }

    // 参数包装类作为静态内部类
    public static class Params {
        private final List<String> flags;

        private Params(Builder builder) {
            this.flags = List.copyOf(builder.flags);
        }

        public static class Builder {
            private final List<String> flags = new ArrayList<>();
            private String message;

            public Builder allowEmpty() {
                flags.add("--allow-empty");
                return this;
            }

            public Builder skipEmpty() {
                flags.add("--skip-empty");
                return this;
            }

            /**
             * 添加 "--all" 参数，表示提交所有更改
             */
            public Builder withAllChanges() {
                flags.add("--all");
                return this;
            }

            /**
             * 添加 "--All" 参数，表示提交所有更改
             */
            public Builder withAllChangesIncludeNewTable() {
                flags.add("--All");
                return this;
            }

            /**
             * 添加提交信息 "-m <message>"
             */
            public Builder withMessage(String message) {
                this.message = message;
                flags.add("--message");
                flags.add(message);
                return this;
            }

            /**
             * 添加提交作者 "--author <author>"
             */
            public Builder withAuthor(String author) {
                flags.add("--author");
                flags.add(author);
                return this;
            }

            /**
             * 添加提交作者 "--author <author>"
             */
            public Builder withDate(Date date) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                flags.add("--date");
                flags.add(sdf.format(date));
                return this;
            }

            /**
             * 构建参数对象
             */
            public Params build() {
                if (message == null || message.isBlank()) {
                    throw new IllegalStateException("Commit message is required.");
                }
                return new Params(this);
            }
        }

        /**
         * 将参数转换为存储过程调用所需的字符串数组
         */
        String[] toProcedureArgs() {
            return flags.toArray(new String[0]);
        }
    }

    /**
     * 准备参数构建器
     */
    public Params.Builder prepare() {
        return new Params.Builder();
    }

    /**
     * 执行存储过程
     *
     * @return
     */
    public ProcedureResult execute(Params params) {
        return call(params.toProcedureArgs());
    }

    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getProcedureTemplate("dolt_commit"), params);
    }

}