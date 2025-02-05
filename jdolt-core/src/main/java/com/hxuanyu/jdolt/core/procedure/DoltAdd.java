package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.model.ProcedureResult;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


/**
 * ## `DOLT_ADD()`
 *
 * 将当前会话中的工作区更改添加到暂存区。功能与CLI中的`dolt add`完全相同，并接受相同的参数。
 *
 * 将表添加到暂存区后，可以使用`DOLT_COMMIT()`提交这些更改。
 *
 * <pre>
 * {@code
 * CALL DOLT_ADD('-A');
 * CALL DOLT_ADD('.');
 * CALL DOLT_ADD('table1', 'table2');
 * }
 * </pre>
 *
 * ### 选项
 *
 * - `table`：要添加到暂存区的表。可以使用缩写`.`来添加所有表。
 * - `-A`：暂存所有有更改的表。
 *
 * ### 输出模式
 *
 * <pre>
 * {@code
 * +--------+------+---------------------------+
 * | Field  | Type | Description               |
 * +--------+------+---------------------------+
 * | status | int  | 成功返回0，失败返回1      |
 * +--------+------+---------------------------+
 * }
 * </pre>
 *
 * ### 示例
 *
 * <pre>
 * {@code
 * -- 设置当前会话的数据库
 * USE mydb;
 *
 * -- 进行修改
 * UPDATE table
 * SET column = "new value"
 * WHERE pk = "key";
 *
 * -- 暂存所有更改
 * CALL DOLT_ADD('-A');
 *
 * -- 提交更改
 * CALL DOLT_COMMIT('-m', 'committing all changes');
 * }
 * </pre>
 */
public class DoltAdd extends DoltRepository implements DoltProcedure {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltAdd> INSTANCES = new ConcurrentHashMap<>();

    private DoltAdd(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltAdd getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltAdd(connectionManager));
    }

    // 参数包装类作为静态内部类
    public static class Params {
        private final List<String> flags;

        private Params(Builder builder) {
            this.flags = List.copyOf(builder.flags);
        }

        public static class Builder {
            private final List<String> flags = new ArrayList<>();
            private boolean addAllFlags = false;
            private boolean addCurrentFlags = false;
            private List<String> tables = new ArrayList<>();

            public Builder addAll() {
                addAllFlags = true;
                flags.add("-A");
                return this;
            }

            public Builder addCurrent() {
                addCurrentFlags = true;
                flags.add(".");
                return this;
            }

            public Builder withTable(String table) {
                this.tables.add(table);
                this.flags.add(table);
                return this;
            }

            public Builder withTables(String... tables) {
                List<String> list = Arrays.asList(tables);
                this.tables.addAll(list);
                flags.addAll(list);
                return this;
            }

            public Builder addFlag(String flag) {
                flags.add(flag);
                return this;
            }

            public Builder addFlags(List<String> flags) {
                this.flags.addAll(flags);
                return this;
            }


            /**
             * 构建参数对象
             */
            public Params build() {
                // addAll 和 addCurrent不允许同时使用
                if (addAllFlags && addCurrentFlags) {
                    throw new IllegalArgumentException("addAllFlags and addCurrentFlags cannot be used at the same time");
                }

                // table非空时，不允许使用addAll和addCurrent
                if (!tables.isEmpty() && (addAllFlags || addCurrentFlags)) {
                    throw new IllegalArgumentException("When tables are not empty, addAllFlags and addCurrentFlags cannot be used");
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
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.SQL_PROCEDURE_DOLT_ADD, params);
    }

}