package com.hxuanyu.jdolt.client.function;

import com.hxuanyu.jdolt.core.function.table.DoltDiff;
import com.hxuanyu.jdolt.model.SqlExecuteResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltDiffTest extends DoltMergeBaseTest {

    @Test
    public void testDoltDiff() {
        DoltDiff doltDiff = versionControl.function().doltDiff();
        SqlExecuteResult execute = doltDiff.prepare()
                .fromRevision("oqo3l2g7adr83a38q6i7ccd7d9a0i5ms")
                .toRevision("HEAD")
                .withTable("products")
                .execute();
        log.info("execute: {}", execute);

    }

    @Test
    public void testTwoDoltDiff() {
        DoltDiff doltDiff = versionControl.function().doltDiff();
        SqlExecuteResult execute = doltDiff.prepare()
                .twoDot("oqo3l2g7adr83a38q6i7ccd7d9a0i5ms", "HEAD")
                .withTable("products")
                .execute();
        log.info("execute: {}", execute);
    }

    @Test
    public void testThreeDotDiff() {
        DoltDiff doltDiff = versionControl.function().doltDiff();
        SqlExecuteResult execute = doltDiff.prepare()
                .threeDot("oqo3l2g7adr83a38q6i7ccd7d9a0i5ms", "HEAD")
                .withTable("products")
                .execute();
        log.info("execute: {}", execute);
    }
}
