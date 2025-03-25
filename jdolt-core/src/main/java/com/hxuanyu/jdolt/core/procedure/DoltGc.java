package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;
import com.hxuanyu.jdolt.util.builder.AbstractProcedureParamBuilder;

import java.util.concurrent.ConcurrentHashMap;


/**
 * ## `DOLT_GC()`
 * <p>
 * 清理数据库中未被引用的数据。在 Dolt sql-server 上运行 `dolt_gc` 过程时，垃圾回收进行期间将阻止所有写操作。
 *
 * <pre>
 * CALL DOLT_GC();
 * CALL DOLT_GC('--shallow');
 * </pre>
 * <p>
 * ### 选项
 * <p>
 * `--shallow` 执行更快但不够彻底的垃圾回收。
 * <p>
 * ### 输出模式
 *
 * <pre>
 * +--------+------+---------------------------+
 * | Field  | Type | Description               |
 * +--------+------+---------------------------+
 * | status | int  | 成功为 0，失败为 1        |
 * +--------+------+---------------------------+
 * </pre>
 * <p>
 * ### 注意事项
 * <p>
 * 为了防止并发写入可能引用垃圾回收的块，运行 `call dolt_gc()` 将会断开所有与运行服务器的开放连接。
 * 这些连接上的正在执行的查询可能会失败，需要重新尝试。在连接断开后重新建立连接是安全的。
 * <p>
 * 运行结束时，执行 `call dolt_gc()` 的连接将保持打开状态，以便交付操作结果。该连接将处于终端失效状态，
 * 任何尝试在其上运行查询的操作都会产生以下错误：
 *
 * <pre>
 * ERROR 1105 (HY000): this connection was established when this server performed an online
 * garbage collection. this connection can no longer be used. please reconnect.
 * </pre>
 * <p>
 * 此连接应被关闭。在某些连接池中，关闭单个连接可能会比较麻烦。如果需要以编程方式运行 `call dolt_gc()`，
 * 一种解决方法是使用一个大小为 1 的单独连接池，在运行成功后可以关闭该连接池。
 */
public class DoltGc extends DoltRepository implements DoltProcedure<DoltGc.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltGc> INSTANCES = new ConcurrentHashMap<>();

    private DoltGc(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltGc getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltGc(connectionManager));
    }

    public static class Params extends AbstractProcedureParamBuilder<Params> {

        protected Params(DoltProcedure<Params> doltProcedure) {
            super(Params.class, doltProcedure);
        }

        @MethodMutexGroup({"shallow"})
        public Params shallow() {
            validator.checkAndMark("shallow");
            addFlags("--shallow");
            return this;
        }


    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getProcedureTemplate("dolt_gc"), params);
    }

}