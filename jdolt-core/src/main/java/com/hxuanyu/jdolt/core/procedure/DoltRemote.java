package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.annotation.MethodExclusive;
import com.hxuanyu.jdolt.annotation.MethodInvokeRequiredGroup;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.AbstractProcedureParamBuilder;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;

import java.util.concurrent.ConcurrentHashMap;

/**
 * `DOLT_REMOTE()`
 * <p>
 * 为数据库添加一个指定URL的远程仓库，或移除一个已有的远程仓库及其远程跟踪分支和配置设置。类似于CLI中的
 * {@link ../../cli/cli.md#dolt-remote dolt remote}[命令](../../cli/cli.md#dolt-remote)，
 * 但不包括云服务提供商的相关标志。要列出现有的远程仓库，请使用
 * {@link ./dolt-system-tables.md#dolt_remotes dolt_remotes}[系统表](./dolt-system-tables.md#dolt_remotes)。
 *
 * <pre>
 * {@code
 * CALL DOLT_REMOTE('add','remote_name','remote_url');
 * CALL DOLT_REMOTE('remove','existing_remote_name');
 * }
 * </pre>
 *
 * <h3>输出模式</h3>
 *
 * <pre>
 * {@code
 * +--------+------+---------------------------+
 * | 字段   | 类型 | 描述                      |
 * +--------+------+---------------------------+
 * | status | int  | 成功为0，失败为1          |
 * +--------+------+---------------------------+
 * }
 * </pre>
 *
 * <h3>示例</h3>
 *
 * <pre>
 * {@code
 * -- 添加一个HTTP远程仓库
 * CALL DOLT_REMOTE('add','origin','https://doltremoteapi.dolthub.com/Dolthub/museum-collections');
 *
 * -- 使用URL的简写形式添加一个HTTP远程仓库
 * CALL DOLT_REMOTE('add','origin1','Dolthub/museum-collections');
 *
 * -- 添加一个基于文件系统的远程仓库
 * CALL DOLT_REMOTE('add','origin2','file:///Users/jennifer/datasets/museum-collections');
 *
 * -- 列出远程仓库以检查
 * SELECT * FROM dolt_remotes;
 * +---------+--------------------------------------------------------------+-----------------------------------------+--------+
 * | name    | url                                                          | fetch_specs                             | params |
 * +---------+--------------------------------------------------------------+-----------------------------------------+--------+
 * | origin  | https://doltremoteapi.dolthub.com/Dolthub/museum-collections | ["refs/heads/*:refs/remotes/origin/*"]  | {}     |
 * | origin1 | https://doltremoteapi.dolthub.com/Dolthub/museum-collections | ["refs/heads/*:refs/remotes/origin1/*"] | {}     |
 * | origin2 | file:///Users/jennifer/datasets/museum-collections           | ["refs/heads/*:refs/remotes/origin2/*"] | {}     |
 * +---------+--------------------------------------------------------------+-----------------------------------------+--------+
 *
 * -- 移除一个远程仓库
 * CALL DOLT_REMOTE('remove','origin1');
 *
 * -- 列出远程仓库以检查
 * SELECT * FROM dolt_remotes;
 * +---------+--------------------------------------------------------------+-----------------------------------------+--------+
 * | name    | url                                                          | fetch_specs                             | params |
 * +---------+--------------------------------------------------------------+-----------------------------------------+--------+
 * | origin  | https://doltremoteapi.dolthub.com/Dolthub/museum-collections | ["refs/heads/*:refs/remotes/origin/*"]  | {}     |
 * | origin2 | file:///Users/jennifer/datasets/museum-collections           | ["refs/heads/*:refs/remotes/origin2/*"] | {}     |
 * +---------+--------------------------------------------------------------+-----------------------------------------+--------+
 * }
 * </pre>
 */
public class DoltRemote extends DoltRepository implements DoltProcedure<DoltRemote.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltRemote> INSTANCES = new ConcurrentHashMap<>();

    private DoltRemote(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltRemote getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltRemote(connectionManager));
    }

    @MethodInvokeRequiredGroup(value = {"add", "remove"}, allRequired = false)
    public static class Params extends AbstractProcedureParamBuilder<Params> {

        protected Params(DoltProcedure<Params> doltProcedure) {
            super(Params.class, doltProcedure);
        }

        @MethodExclusive
        public Params add(String remoteName, String url) {
            validator.checkAndMark("add");
            addFlags("add", remoteName, url);
            return this;
        }

        @MethodExclusive
        public Params remove(String remoteName) {
            validator.checkAndMark("remove");
            addFlags("remove", remoteName);
            return this;
        }
    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getProcedureTemplate("dolt_remote"), params);
    }

}