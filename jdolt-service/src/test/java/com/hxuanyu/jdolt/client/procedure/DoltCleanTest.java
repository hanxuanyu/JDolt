package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.procedure.DoltClean;
import com.hxuanyu.jdolt.model.ProcedureResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltCleanTest extends DoltClientTest {

    @Test
    public void testDoltCleanNoArgs() {
        DoltClean doltClean = versionControl.doltClean();
        ProcedureResult testResult = doltClean.test();
        log.info("testResult: {}", testResult);
        ProcedureResult result = doltClean.call();
        log.info("result: {}", result);
    }


    @Test
    public void testDoltClean() {
        DoltClean doltClean = versionControl.doltClean();
        ProcedureResult testResult = doltClean.test("test_table");
        log.info("testResult: {}", testResult);
        ProcedureResult result = doltClean.call("test_table");
        log.info("result: {}", result);
    }


}
