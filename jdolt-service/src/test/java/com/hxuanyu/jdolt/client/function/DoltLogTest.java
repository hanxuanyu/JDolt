package com.hxuanyu.jdolt.client.function;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.function.table.DoltLog;
import com.hxuanyu.jdolt.model.SqlExecuteResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 *
 * @author hanxuanyu
 * @version 1.0
 */
@Slf4j
public class DoltLogTest extends DoltClientTest {

    @Test
    public void testDoltLog() {
        DoltLog doltLog = versionControl.function().doltLog();
        SqlExecuteResult execute = doltLog.prepare()
                .decorate("full")
                .execute();
        execute.print();
    }

}
