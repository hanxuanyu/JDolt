package com.hxuanyu.jdolt.core.function;

import com.hxuanyu.jdolt.annotation.MethodDependsOn;
import com.hxuanyu.jdolt.annotation.MethodInvokeRequiredGroup;
import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.interfaces.DoltInfoFunction;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.builder.AbstractFunctionParamBuilder;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;

import java.util.concurrent.ConcurrentHashMap;


/**
 * `HAS_ANCESTOR()`
 * <p>
 * `HAS_ANCESTOR(target, ancestor)` 函数返回一个 `boolean` 值，用于指示候选的 `ancestor` 提交是否存在于 `target` 引用的提交图中。
 * <p>
 * 请参考上面的示例提交图：
 *
 * <pre>
 *       A---B---C feature
 *      /
 * D---E---F---G main
 * </pre>
 * <p>
 * 一个将字母替换为提交哈希值的假设示例如下：
 *
 * <pre>
 * select has_ancestor('feature', 'A'); // true
 * select has_ancestor('feature', 'E'); // true
 * select has_ancestor('feature', 'F'); // false
 * select has_ancestor('main', 'E');    // true
 * select has_ancestor('G', 'main');    // true
 * </pre>
 */
public class HasAncestor extends DoltRepository implements DoltInfoFunction<HasAncestor.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, HasAncestor> INSTANCES = new ConcurrentHashMap<>();

    private HasAncestor(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static HasAncestor getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new HasAncestor(connectionManager));
    }

    @MethodInvokeRequiredGroup(value = {"withAncestor", "check"}, allRequired = false)
    public static class Params extends AbstractFunctionParamBuilder<Params> {

        protected Params(DoltInfoFunction<Params> doltInfoFunction) {
            super(Params.class, doltInfoFunction);
        }

        @MethodMutexGroup({"targetRef"})
        public Params targetRef(String ref) {
            validator.checkAndMark("targetRef");
            addFlags(ref);
            return this;
        }

        @MethodDependsOn("targetRef")
        @MethodMutexGroup({"withAncestor"})
        public Params withAncestor(String ref) {
            validator.checkAndMark("withAncestor");
            addFlags(ref);
            return this;
        }

        @MethodMutexGroup({"check"})
        public Params check(String targetRef, String ancestorRef) {
            validator.checkAndMark("check");
            addFlags(targetRef, ancestorRef);
            return this;
        }


    }

    @Override
    public Params prepare() {
        return new Params(this);
    }



    @Override
    public SqlBuilder.SqlTemplate buildSqlTemplate(String... params) {
        return SqlBuilder.selectFunction("has_ancestor")
                .withParams(params)
                .build();
    }
}