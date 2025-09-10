package com.hxuanyu.jdolt.client.function;

import com.hxuanyu.jdolt.core.function.table.DoltDiffStat;
import com.hxuanyu.jdolt.core.function.table.DoltDiffSummary;
import com.hxuanyu.jdolt.model.SqlExecuteResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltDiffSummaryTest extends DoltMergeBaseTest {

    @Test
    public void testDoltDiffStat() {
        DoltDiffSummary doltDiffSummary = versionControl.function().doltDiffSummary();
        SqlExecuteResult execute = doltDiffSummary.prepare()
                .fromRevision("oqo3l2g7adr83a38q6i7ccd7d9a0i5ms")
                .toRevision("HEAD")
                .withTable("products")
                .execute();
        log.info("execute: {}", execute);
        execute.print();

    }

    @Test
    public void testTwoDoltDiffStat() {
        DoltDiffSummary doltDiffSummary = versionControl.function().doltDiffSummary();
        SqlExecuteResult execute = doltDiffSummary.prepare()
                .twoDot("oqo3l2g7adr83a38q6i7ccd7d9a0i5ms", "HEAD")
                .withTable("products")
                .execute();
        log.info("execute: {}", execute);
        execute.print();
    }

    @Test
    public void testThreeDotDiffStat() {
        DoltDiffSummary doltDiffSummary = versionControl.function().doltDiffSummary();
        SqlExecuteResult execute = doltDiffSummary.prepare()
                .threeDot("oqo3l2g7adr83a38q6i7ccd7d9a0i5ms", "HEAD")
                .withTable("products")
                .execute();
        log.info("execute: {}", execute);
    }
}
