package com.hxuanyu.jdolt.client.function;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.function.table.DoltLog;
import com.hxuanyu.jdolt.model.SqlExecuteResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * TODO
 *
 * @author hanxuanyu
 * @version 1.0
 */
@Slf4j
public class DoltLogTest extends DoltClientTest {

    @Test
    public void testDoltLog() {
        DoltLog doltLog = versionControl.doltLog();
        SqlExecuteResult execute = doltLog.prepare()
                .decorate("full")
                .not("oqo3l2g7adr83a38q6i7ccd7d9a0i5ms")
                .execute();
        execute.print();
    }

}
