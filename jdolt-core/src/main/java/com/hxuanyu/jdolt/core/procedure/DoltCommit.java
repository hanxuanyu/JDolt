package com.hxuanyu.jdolt.core.procedure;

import com.hxuanyu.jdolt.annotation.MethodMutexGroup;
import com.hxuanyu.jdolt.manager.DoltConnectionManager;
import com.hxuanyu.jdolt.interfaces.DoltProcedure;
import com.hxuanyu.jdolt.repository.DoltRepository;
import com.hxuanyu.jdolt.util.AbstractParamBuilder;
import com.hxuanyu.jdolt.util.DoltSqlTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class DoltCommit extends DoltRepository implements DoltProcedure<DoltCommit.Params> {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltCommit> INSTANCES = new ConcurrentHashMap<>();

    private DoltCommit(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltCommit getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltCommit(connectionManager));
    }

    public static class Params extends AbstractParamBuilder<Params> {

        protected Params(DoltProcedure<Params> doltProcedure) {
            super(Params.class, doltProcedure);
        }

        @MethodMutexGroup({"message"})
        public Params message(String commitMessage) {
            validator.checkAndMark("message");
            addFlags("--message", commitMessage);
            return this;
        }

        @MethodMutexGroup({"stageAll"})
        public Params stageAll() {
            validator.checkAndMark("stageAll");
            addFlags("--all");
            return this;
        }

        @MethodMutexGroup({"stageAllWithNewTable"})
        public Params stageAllWithNewTable() {
            validator.checkAndMark("stageAllWithNewTable");
            addFlags("--ALL");
            return this;
        }

        @MethodMutexGroup({"allowEmpty", "skipEmpty"})
        public Params allowEmpty() {
            validator.checkAndMark("allowEmpty");
            addFlags("--allow-empty");
            return this;
        }

        @MethodMutexGroup({"skipEmpty", "allowEmpty"})
        public Params skipEmpty() {
            validator.checkAndMark("skipEmpty");
            addFlags("--skip-empty");
            return this;
        }

        @MethodMutexGroup({"withDate"})
        public Params withDate(Date date) {
            validator.checkAndMark("withDate");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
            addFlags("--date", simpleDateFormat.format(date));
            return this;
        }

        @MethodMutexGroup({"withAuthor"})
        public Params withAuthor(String authorName, String mail) {
            validator.checkAndMark("withAuthor");
            addFlags("--author", (authorName + " <" + mail + ">"));
            return this;
        }


    }

    @Override
    public Params prepare() {
        return new Params(this);
    }


    @Override
    public String buildSql(String... params) {
        return DoltSqlTemplate.buildSqlTemplate(DoltSqlTemplate.getProcedureTemplate("dolt_commit"), params);
    }

}
