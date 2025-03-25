package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.annotation.MethodDependsOn;
import com.hxuanyu.jdolt.annotation.MethodExclusive;
import com.hxuanyu.jdolt.annotation.MethodInvokeRequiredGroup;
import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;
import com.hxuanyu.jdolt.util.builder.AbstractProcedureParamBuilder;

import java.util.concurrent.ConcurrentHashMap;


/**
 * ## `DOLT_TAG()`
 * <p>
 * 创建一个指向指定提交引用的新标签，或者删除一个已存在的标签。其功能与命令行中的
 * {@link ../../cli/cli.md#dolt-tag dolt tag} 命令完全相同，并接受除列出标签之外的相同参数。
 * 要列出现有标签，请使用 {@link ./dolt-system-tables.md#dolt_tags dolt_tags} 系统表。
 *
 * <pre>
 * CALL DOLT_TAG('tag_name', 'commit_ref');
 * CALL DOLT_TAG('-m', 'message', 'tag_name', 'commit_ref');
 * CALL DOLT_TAG('-m', 'message', '--author', 'John Doe <johndoe@example.com>', 'tag_name', 'commit_ref');
 * CALL DOLT_TAG('-d', 'tag_name');
 * </pre>
 * <p>
 * ### 选项
 * <p>
 * `-m`：使用指定的消息作为标签信息。
 * <p>
 * `-d`：删除一个标签。
 * <p>
 * `--author`：使用标准格式 "A U Thor author@example.com" 指定明确的作者。
 * <p>
 * ### 输出模式
 *
 * <pre>
 * +--------+------+---------------------------+
 * | Field  | Type | Description               |
 * +--------+------+---------------------------+
 * | status | int  | 成功返回0，失败返回1      |
 * +--------+------+---------------------------+
 * </pre>
 * <p>
 * ### 示例
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
 * -- 暂存并提交所有更改。
 * CALL DOLT_COMMIT('-am', 'committing all changes');
 *
 * -- 为HEAD提交创建一个标签。
 * CALL DOLT_TAG('v1','head','-m','creating v1 tag');
 * </pre>
 */
public class DoltTag extends DoltRepository implements DoltProcedure<DoltTag.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltTag> INSTANCES = new ConcurrentHashMap<>();

    private DoltTag(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltTag getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltTag(connectionManager));
    }

    @MethodInvokeRequiredGroup(value = {"withName", "delete"}, allRequired = false)
    public static class Params extends AbstractProcedureParamBuilder<Params> {

        protected Params(DoltProcedure<Params> doltProcedure) {
            super(Params.class, doltProcedure);
        }

        @MethodMutexGroup({"withName"})
        public Params withName(String tagName) {
            validator.checkAndMark("withName");
            addFlags(tagName);
            return this;
        }

        @MethodMutexGroup({"withRef"})
        @MethodDependsOn({"withName"})
        public Params withRef(String ref) {
            validator.checkAndMark("withRef");
            addFlags(ref);
            return this;
        }

        @MethodMutexGroup({"withMessage"})
        public Params withMessage(String message) {
            validator.checkAndMark("withMessage");
            addFlags("-m", message);
            return this;
        }

        @MethodMutexGroup({"withAuthor"})
        public Params withAuthor(String author, String mail) {
            validator.checkAndMark("withAuthor");
            addFlags("--author", author + " <" + mail + "?");
            return this;
        }


        @MethodExclusive
        public Params delete(String tagName) {
            validator.checkAndMark("delete");
            addFlags("-d", tagName);
            return this;
        }


    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getProcedureTemplate("dolt_tag"), params);
    }

}