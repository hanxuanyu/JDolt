package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.annotation.MethodInvokeRequired;
import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.AbstractProcedureParamBuilder;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;

import java.util.concurrent.ConcurrentHashMap;


public class DoltRevert extends DoltRepository implements DoltProcedure<DoltRevert.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltRevert> INSTANCES = new ConcurrentHashMap<>();

    private DoltRevert(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltRevert getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltRevert(connectionManager));
    }

    public static class Params extends AbstractProcedureParamBuilder<Params> {

        protected Params(DoltProcedure<Params> doltProcedure) {
            super(Params.class, doltProcedure);
        }


        @MethodInvokeRequired
        public Params withRevision(String revision) {
            validator.checkAndMark("withRevision");
            addFlags(revision);
            return this;
        }

        @MethodMutexGroup({"withRelativeRevision"})
        public Params withRelativeRevision(String revision, int generation) {
            validator.checkAndMark("withRelativeRevision");
            addFlags(revision + "~" + generation);
            return this;
        }

        @MethodMutexGroup({"withAuthor"})
        public Params withAuthor(String author, String mail) {
            validator.checkAndMark("withAuthor");
            addFlags("--author", author + " <" + mail + ">");
            return this;
        }

    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getProcedureTemplate("dolt_revert"), params);
    }

}