package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.annotation.MethodDependsOn;
import com.hxuanyu.jdolt.annotation.MethodInvokeRequired;
import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractProcedureParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;

import java.util.concurrent.ConcurrentHashMap;


/**
 * `DOLT_CLONE()`
 * <p>
 * 在当前 Dolt 环境中，将现有的 Dolt 数据库克隆为一个新的数据库。必须将现有数据库作为参数指定，
 * 可以是指向磁盘上现有 Dolt 数据库的文件 URL，或者是远程托管数据库的 `doltremote` URL
 * （例如托管在 DoltHub 或 DoltLab 上的数据库），也可以使用 `<org>/<database>` 的简写形式
 * （例如 `dolthub/us-jails`）来表示托管在 DoltHub 上的数据库。可以选择性地提供一个额外参数来
 * 指定新克隆数据库的名称，否则将使用现有数据库的当前名称。
 * <p>
 * 注意：从文件 URL 克隆时，目前必须包含 `.dolt/noms` 子目录。更多详细信息请参阅 GitHub 跟踪问题
 * [dolt#1860](https://github.com/dolthub/dolt/issues/1860)。
 * <p>
 * 示例代码：
 * <pre>{@code
 * CALL DOLT_CLONE('file:///myDatabasesDir/database/.dolt/noms');
 * CALL DOLT_CLONE('dolthub/us-jails', 'myCustomDbName');
 * }</pre>
 * <p>
 * ### 选项
 * <p>
 * `--remote`：要添加到新克隆数据库的远程名称。默认值为 `origin`。
 * <p>
 * `-b`, `--branch`：要克隆的分支。如果未指定，将克隆所有分支。
 * <p>
 * `--depth`：仅克隆单个分支，并将历史记录限制为指定的提交深度。
 * <p>
 * ### 输出模式
 * <pre>{@code
 * +--------+------+---------------------------+
 * | Field  | Type | Description               |
 * +--------+------+---------------------------+
 * | status | int  | 0 表示成功，1 表示失败       |
 * +--------+------+---------------------------+
 * }</pre>
 * <p>
 * ### 示例
 * <p>
 * 使用 `<org>/<database>` 的简写形式，从 DoltHub 克隆 dolthub/us-jails 数据库：
 * <pre>{@code
 * CALL DOLT_CLONE('dolthub/us-jails');
 *
 * -- 使用新克隆的数据库
 * -- 注意：对于带有连字符的数据库名称，需要使用反引号
 * USE `us-jails`;
 * SHOW TABLES;
 * +-----------------------------+
 * | Tables_in_us-jails          |
 * +-----------------------------+
 * | incidents                   |
 * | inmate_population_snapshots |
 * | jails                       |
 * +-----------------------------+
 * }</pre>
 * <p>
 * 克隆 dolthub/museum-collections 数据库，这次使用 doltremoteapi URL，仅克隆一个分支，
 * 自定义远程名称，并提供一个自定义数据库名称：
 * <pre>{@code
 * CALL DOLT_CLONE('-branch', 'prod', '-remote', 'dolthub',
 *                 'https://doltremoteapi.dolthub.com/dolthub/ge-taxi-demo', 'taxis');
 *
 * -- 验证是否只克隆了 prod 分支
 * USE taxis;
 * SELECT * FROM DOLT_BRANCHES;
 * +------+----------------------------------+------------------+------------------------+-------------------------+------------------------------+
 * | name | hash                             | latest_committer | latest_committer_email | latest_commit_date      | latest_commit_message        |
 * +------+----------------------------------+------------------+------------------------+-------------------------+------------------------------+
 * | prod | 1s61u4rbbd26u0tlpdhb46cuejd1dogj | oscarbatori      | oscarbatori@gmail.com  | 2021-06-14 17:52:58.702 | Added first cut of trip data |
 * +------+----------------------------------+------------------+------------------------+-------------------------+------------------------------+
 *
 * -- 验证新克隆数据库的默认远程名称是否为 "dolthub"（而不是 "origin"）
 * SELECT * FROM DOLT_REMOTES;
 * +---------+--------------------------------------------------------+-----------------------------------------+--------+
 * | name    | url                                                    | fetch_specs                             | params |
 * +---------+--------------------------------------------------------+-----------------------------------------+--------+
 * | dolthub | https://doltremoteapi.dolthub.com/dolthub/ge-taxi-demo | ["refs/heads/*:refs/remotes/dolthub/*"] | {}     |
 * +---------+--------------------------------------------------------+-----------------------------------------+--------+
 * }</pre>
 */
public class DoltClone extends DoltRepository implements DoltProcedure<DoltClone.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltClone> INSTANCES = new ConcurrentHashMap<>();

    private DoltClone(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltClone getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltClone(connectionManager));
    }

    public static class Params extends AbstractProcedureParamBuilder<Params> {

        protected Params(DoltProcedure<Params> doltProcedure) {
            super(Params.class, doltProcedure);
        }

        @MethodMutexGroup({"specifyRemoteName"})
        public Params specifyRemoteName(String remoteName) {
            validator.checkAndMark("specifyRemoteName");
            addFlags("-remote", remoteName);
            return this;
        }

        @MethodMutexGroup({"specifyDB"})
        @MethodDependsOn("withUrl")
        public Params specifyDB(String databaseName) {
            validator.checkAndMark("specifyDB");
            addFlags(databaseName);
            return this;
        }

        @MethodMutexGroup({"withBranch"})
        public Params withBranch(String branch) {
            validator.checkAndMark("withBranch");
            addFlags("--branch", branch);
            return this;
        }

        @MethodInvokeRequired
        @MethodMutexGroup({"withUrl"})
        public Params withUrl(String url) {
            validator.checkAndMark("withUrl");
            addFlags(url);
            return this;
        }





    }

    @Override
    public Params prepare() {
        return new Params(this);
    }



    @Override
    public SqlBuilder.SqlTemplate buildSqlTemplate(String... params) {
        return SqlBuilder.callProcedure("dolt_clone")
                .withParams(params)
                .build();
    }
}