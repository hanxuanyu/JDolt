package com.hxuanyu.jdolt.core.function.info;

import com.hxuanyu.jdolt.interfaces.DoltInfoFunction;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractFunctionParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;

import java.util.concurrent.ConcurrentHashMap;

/**
 * `LAST_INSERT_UUID()`
 *
 * <p>`last_insert_uuid()` 函数返回当前会话中最后执行语句插入的第一行的 UUID。
 * 这是 <a href="https://dev.mysql.com/doc/refman/8.3/en/information-functions.html#function_last-insert-id">MySQL 的 `LAST_INSERT_ID()`</a>
 * 函数的 UUID 对应版本。我们
 * <a href="https://www.dolthub.com/blog/2023-10-27-uuid-keys/">建议在主键中使用 UUID 而不是 auto_increment 值</a>，
 * 因为它在支持分布式数据库克隆的值合并方面表现更好。
 *
 * <p>`last_insert_id()` 通过检测列上是否存在 `auto_increment` 修饰符来确定返回哪个自动生成的键值，
 * 而 `last_insert_uuid()` 则依赖于列的特定定义。为了让 `last_insert_uuid()` 获取插入的 UUID 值，列
 * <strong>必须</strong> 是表主键的一部分，并且 <strong>必须</strong> 满足以下类型定义之一：
 *
 * <ul>
 *   <li>`VARCHAR(36)` 或 `CHAR(36)`，且默认值表达式为 `(UUID())`
 *   <li>`VARBINARY(16)` 或 `BINARY(16)`，且默认值表达式为 `(UUID_TO_BIN(UUID()))`
 * </ul>
 *
 * <p>当列被定义为 `VARBINARY` 或 `BINARY` 并在默认值表达式中使用 `UUID_TO_BIN()` 函数时，
 * <a href="https://dev.mysql.com/doc/refman/8.3/en/miscellaneous-functions.html#function_uuid-to-bin">可以选择性地指定</a>
 * <a href="https://dev.mysql.com/doc/refman/8.3/en/miscellaneous-functions.html#function_uuid-to-bin">`UUID_TO_BIN`</a> 的
 * <a href="https://dev.mysql.com/doc/refman/8.3/en/miscellaneous-functions.html#function_uuid-to-bin">swap_flag</a>。
 *
 * <p>以下代码展示了如何创建符合上述要求的表，并演示如何使用 `last_insert_uuid()`：
 *
 * <pre>{@code
 * > create table t (pk binary(16) primary key default (UUID_to_bin(UUID())), c1 varchar(100));
 *
 * > insert into t (c1) values ("one"), ("two");
 * Query OK, 2 rows affected (0.00 sec)
 *
 * > select last_insert_uuid();
 * +--------------------------------------+
 * | last_insert_uuid()                   |
 * +--------------------------------------+
 * | 6cd58555-bb3f-45d8-9302-d32d94d8e28a |
 * +--------------------------------------+
 *
 * > select c1 from t where pk = uuid_to_bin(last_insert_uuid());
 * +-----+
 * | c1  |
 * +-----+
 * | one |
 * +-----+
 * }</pre>
 */
public class LastInsertUUID extends DoltRepository implements DoltInfoFunction<LastInsertUUID.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, LastInsertUUID> INSTANCES = new ConcurrentHashMap<>();

    private LastInsertUUID(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static LastInsertUUID getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new LastInsertUUID(connectionManager));
    }

    public static class Params extends AbstractFunctionParamBuilder<Params> {

        protected Params(DoltInfoFunction<Params> doltInfoFunction) {
            super(Params.class, doltInfoFunction);
        }

    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public SqlBuilder.SqlTemplate buildSqlTemplate(String... params) {
        return SqlBuilder.selectFunction("last_insert_uuid")
                .withParams(params)
                .build();
    }

}