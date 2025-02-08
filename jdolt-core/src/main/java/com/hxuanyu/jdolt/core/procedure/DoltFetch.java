package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.annotation.MethodDependsOn;
import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.AbstractProcedureParamBuilder;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ## `DOLT_FETCH()`
 * <p>
 * 获取引用（refs）以及完成其历史记录所需的对象，并更新远程跟踪分支。
 * 其功能与 CLI 中的 `dolt fetch` 完全相同，并接受相同的参数。
 *
 * <pre>{@code
 * CALL DOLT_FETCH('origin', 'main');
 * CALL DOLT_FETCH('origin', 'feature-branch');
 * CALL DOLT_FETCH('origin', 'refs/heads/main:refs/remotes/origin/main');
 * CALL DOLT_FETCH('origin', NULL);
 * CALL DOLT_FETCH('origin');
 * }</pre>
 * <p>
 * ### 选项
 * <p>
 * 此过程没有选项。
 * <p>
 * ### 输出模式
 *
 * <pre>{@code
 * +--------+------+---------------------------+
 * | 字段   | 类型 | 描述                      |
 * +--------+------+---------------------------+
 * | status | int  | 成功为 0，不成功为 1       |
 * +--------+------+---------------------------+
 * }</pre>
 * <p>
 * ### 示例
 *
 * <pre>{@code
 * -- 获取远程 main 分支
 * CALL DOLT_FETCH('origin', 'main');
 *
 * -- 查看获取的远程分支的哈希值
 * SELECT HASHOF('origin/main');
 *
 * -- 将远程 main 分支与当前分支合并
 * CALL DOLT_MERGE('origin/main');
 * }</pre>
 * <p>
 * ### 注意事项
 * <p>
 * 省略第二个参数或传递 NULL，将使用默认的 refspec。
 */
public class DoltFetch extends DoltRepository implements DoltProcedure<DoltFetch.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltFetch> INSTANCES = new ConcurrentHashMap<>();

    private DoltFetch(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltFetch getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltFetch(connectionManager));
    }

    public static class Params extends AbstractProcedureParamBuilder<Params> {

        protected Params(DoltProcedure<Params> doltProcedure) {
            super(Params.class, doltProcedure);
        }


        @MethodMutexGroup({"withRemote"})
        public Params withRemote(String remote) {
            validator.checkAndMark("withRemote");
            addFlags(remote);
            return this;
        }

        @MethodMutexGroup({"withRef"})
        @MethodDependsOn({"withRemote"})
        public Params withRef(String ref) {
            validator.checkAndMark("withRef");
            addFlags(ref);
            return this;
        }


    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getProcedureTemplate("dolt_fetch"), params);
    }

}