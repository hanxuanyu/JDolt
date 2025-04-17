package com.hxuanyu.jdolt.core.function.table;

import com.hxuanyu.jdolt.annotation.MethodDependsOn;
import com.hxuanyu.jdolt.annotation.MethodInvokeRequiredGroup;
import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.interfaces.DoltTableFunction;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractTableFunctionParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;


import java.util.concurrent.ConcurrentHashMap;
/**
 * ## {@code DOLT_PATCH()}
 *
 * 生成从起始版本到目标版本修补表（或所有表）所需的SQL语句。这在您希望从外部来源导入数据到Dolt、比较差异并生成修补原始数据源所需的SQL语句时非常有用。此命令等同于  
 * {@link ../../cli/cli.md#dolt-diff dolt diff -r sql} CLI命令。如果适用，将返回架构和/或数据差异语句。  
 * 一些由于架构不兼容更改而无法生成的数据差异将显示为包含发生此问题的表的警告。  
 *
 * <p>语句的顺序是先生成架构修补语句，然后是数据修补语句。如果修补所有表，建议在按照返回的顺序应用这些修补语句之前关闭外键检查（{@code SET foreign_key_checks=0;}），以避免冲突。  
 *
 * <p>获取SQL修补语句目前仅作为表函数提供；未来将支持CLI命令{@code dolt patch}。  
 *
 * <h3>权限</h3>  
 * <p>  
 * {@code DOLT_PATCH()}表函数需要对所有表（如果未定义表）或仅对定义的表具有{@code SELECT}权限。  
 * </p>  
 *
 * <h3>选项</h3>  
 * <pre>{@code
 * DOLT_PATCH(<from_revision>, <to_revision>, <optional_tablename>)
 * DOLT_PATCH(<from_revision..to_revision>, <optional_tablename>)
 * DOLT_PATCH(<from_revision...to_revision>, <optional_tablename>)
 * }</pre>  
 *
 * <p>{@code DOLT_PATCH()}表函数接受以下参数：</p>  
 * <ul>  
 *   <li>{@code from_revision} — 修补起始版本的表数据版本。此参数为必填项。可以是提交、标签、分支名称或其他版本标识符（例如："main~"，"WORKING"，"STAGED"）。</li>  
 *   <li>{@code to_revision} — 修补目标版本的表数据版本。此参数为必填项。可以是提交、标签、分支名称或其他版本标识符（例如："main~"，"WORKING"，"STAGED"）。</li>  
 *   <li>{@code from_revision..to_revision} — 获取两点修补或{@code from_revision}和{@code to_revision}之间的表数据版本。这等同于{@code dolt_patch(<from_revision>, <to_revision>, <tablename>)}。</li>  
 *   <li>{@code from_revision...to_revision} — 获取三点修补或{@code from_revision}和{@code to_revision}之间的表数据版本，<em>从最后一个共同提交开始</em>。</li>  
 *   <li>{@code tablename} — 包含需要修补的数据和/或架构的表名称。此参数为可选项。如果未定义，将返回所有具有数据和/或架构修补的表。</li>  
 * </ul>  
 *
 * <h3>架构</h3>  
 * <pre>  
 * +------------------+--------+  
 * | 字段             | 类型   |  
 * +------------------+--------+  
 * | statement_order  | BIGINT |  
 * | from_commit_hash | TEXT   |  
 * | to_commit_hash   | TEXT   |  
 * | table_name       | TEXT   |  
 * | diff_type        | TEXT   |  
 * | statement        | TEXT   |  
 * +------------------+--------+  
 * </pre>  
 *
 * <h3>示例</h3>  
 * <p>  
 * 假设我们从{@code main}分支的数据库中的表{@code inventory}开始。当我们进行任何更改时，可以使用{@code DOLT_PATCH()}函数获取特定提交之间表数据或所有表数据更改的SQL修补语句。  
 * </p>  
 *
 * <p>以下是{@code main}分支顶部的{@code inventory}表架构：</p>  
 * <pre>  
 * +----------+-------------+------+-----+---------+-------+  
 * | 字段     | 类型        | 空值 | 键  | 默认值  | 额外  |  
 * +----------+-------------+------+-----+---------+-------+  
 * | pk       | int         | NO   | PRI | NULL    |       |  
 * | name     | varchar(50) | YES  |     | NULL    |       |  
 * | quantity | int         | YES  |     | NULL    |       |  
 * +----------+-------------+------+-----+---------+-------+  
 * </pre>  
 *
 * <p>以下是{@code main}分支顶部{@code inventory}表的数据：</p>  
 * <pre>  
 * +----+-------+----------+  
 * | pk | name  | quantity |  
 * +----+-------+----------+  
 * | 1  | shirt | 15       |  
 * | 2  | shoes | 10       |  
 * +----+-------+----------+  
 * </pre>  
 *
 * <p>我们对{@code inventory}表进行了一些更改并创建了一个新的无主键表：</p>  
 * <pre>  
 * INSERT INTO inventory VALUES (3, 'hat', 6);  
 * UPDATE inventory SET quantity=0 WHERE pk=1;  
 * CREATE TABLE items (name varchar(50));  
 * INSERT INTO items VALUES ('shirt'),('pants');  
 * </pre>  
 *
 * <p>以下是当前工作集中的{@code inventory}表数据：</p>  
 * <pre>  
 * +----+-------+----------+  
 * | pk | name  | quantity |  
 * +----+-------+----------+  
 * | 1  | shirt | 0        |  
 * | 2  | shoes | 10       |  
 * | 3  | hat   | 6        |  
 * +----+-------+----------+  
 * </pre>  
 *
 * <p>要获取SQL修补语句，我们运行以下查询：</p>  
 * <pre>{@code
 * SELECT * FROM DOLT_PATCH('main', 'WORKING');
 * }</pre>  
 *
 * <p>从{@code DOLT_PATCH()}返回的结果显示从{@code main}分支顶部到当前工作集的数据如何更改：</p>  
 * <pre>  
 * +-----------------+----------------------------------+----------------+------------+-----------+----------------------------------------------------------------------+  
 * | statement_order | from_commit_hash                 | to_commit_hash | table_name | diff_type | statement                                                            |  
 * +-----------------+----------------------------------+----------------+------------+-----------+----------------------------------------------------------------------+  
 * | 1               | gg4kasjl6tgrtoag8tnn1der09sit4co | WORKING        | inventory  | data      | UPDATE `inventory` SET `quantity`=0 WHERE `pk`=1;                    |  
 * | 2               | gg4kasjl6tgrtoag8tnn1der09sit4co | WORKING        | inventory  | data      | INSERT INTO `inventory` (`pk`,`name`,`quantity`) VALUES (3,'hat',6); |  
 * | 3               | gg4kasjl6tgrtoag8tnn1der09sit4co | WORKING        | items      | schema    | CREATE TABLE `items` (                                               |  
 * |                 |                                  |                |            |           |   `name` varchar(50)                                                 |  
 * |                 |                                  |                |            |           | ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_bin;    |  
 * | 4               | gg4kasjl6tgrtoag8tnn1der09sit4co | WORKING        | items      | data      | INSERT INTO `items` (`name`) VALUES ('shirt');                       |  
 * | 5               | gg4kasjl6tgrtoag8tnn1der09sit4co | WORKING        | items      | data      | INSERT INTO `items` (`name`) VALUES ('pants');                       |  
 * +-----------------+----------------------------------+----------------+------------+-----------+----------------------------------------------------------------------+  
 * </pre>  
 *
 * <p>要获取从当前工作集到{@code main}分支顶部的特定表架构修补语句，我们运行以下查询：</p>  
 * <pre>{@code
 * SELECT * FROM DOLT_PATCH('WORKING', 'main', 'items') WHERE diff_type = 'schema';
 * }</pre>  
 *
 * <p>结果为单行：</p>  
 * <pre>  
 * +-----------------+------------------+----------------------------------+------------+-----------+---------------------+  
 * | statement_order | from_commit_hash | to_commit_hash                   | table_name | diff_type | statement           |  
 * +-----------------+------------------+----------------------------------+------------+-----------+---------------------+  
 * | 1               | WORKING          | gg4kasjl6tgrtoag8tnn1der09sit4co | items      | schema    | DROP TABLE `items`; |  
 * +-----------------+------------------+----------------------------------+------------+-----------+---------------------+  
 * </pre>  
 */
@MethodInvokeRequiredGroup(value = {"fromRevision", "twoDot", "threeDot"}, allRequired = false)
public class DoltPatch extends DoltRepository implements DoltTableFunction<DoltPatch.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltPatch> INSTANCES = new ConcurrentHashMap<>();

    private DoltPatch(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltPatch getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltPatch(connectionManager));
    }

    public static class Params extends AbstractTableFunctionParamBuilder<Params> {

        protected Params(DoltTableFunction<Params> doltTableFunction) {
            super(Params.class, doltTableFunction);
        }

        /**
         * 设置修补起始版本
         * 
         * @param fromRevision 修补起始版本的表数据版本
         * @return 当前参数构建器实例
         */
        @MethodMutexGroup({"twoDot", "threeDot"})
        public Params fromRevision(String fromRevision) {
            validator.checkAndMark("fromRevision");
            addFlags(fromRevision);
            return this;
        }

        /**
         * 设置修补目标版本
         * 
         * @param toRevision 修补目标版本的表数据版本
         * @return 当前参数构建器实例
         */
        @MethodDependsOn({"fromRevision"})
        public Params toRevision(String toRevision) {
            validator.checkAndMark("toRevision");
            addFlags(toRevision);
            return this;
        }

        /**
         * 设置表名称
         * 
         * @param tableName 包含需要修补的数据和/或架构的表名称
         * @return 当前参数构建器实例
         */
        @MethodDependsOn({"toRevision", "twoDot", "threeDot"})
        public Params withTable(String tableName) {
            validator.checkAndMark("withTable");
            addFlags(tableName);
            return this;
        }

        /**
         * 设置两点修补范围
         * 
         * @param from 修补起始版本
         * @param to 修补目标版本
         * @return 当前参数构建器实例
         */
        @MethodMutexGroup({"fromRevision", "threeDot"})
        public Params twoDot(String from, String to) {
            validator.checkAndMark("twoDot");
            addFlags(from + ".." + to);
            return this;
        }

        /**
         * 设置三点修补范围（从最后一个共同提交开始）
         * 
         * @param from 修补起始版本
         * @param to 修补目标版本
         * @return 当前参数构建器实例
         */
        @MethodMutexGroup({"fromRevision", "twoDot"})
        public Params threeDot(String from, String to) {
            validator.checkAndMark("threeDot");
            addFlags(from + "..." + to);
            return this;
        }
    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public SqlBuilder.SqlTemplate buildSqlTemplate(String... params) {
        return SqlBuilder.select()
                .fromFunction("DOLT_PATCH")
                .withParams(params)
                .build();
    }

}
