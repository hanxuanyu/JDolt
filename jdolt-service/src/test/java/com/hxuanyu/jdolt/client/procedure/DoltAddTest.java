package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.procedure.DoltAdd;
import com.hxuanyu.jdolt.model.SqlExecuteResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltAddTest extends DoltClientTest {

    @Test
    public void testDoltAddAll() {

        DoltAdd doltAdd = versionControl.procedure().doltAdd();
        SqlExecuteResult result = doltAdd.prepare()
                .addAll()
                .execute();

        log.info(result.toString());
    }

    @Test
    public void testDoltAddCurrent() {

        DoltAdd doltAdd = versionControl.procedure().doltAdd();
        SqlExecuteResult result = doltAdd
                .prepare()
                .addCurrent()
                .execute();

        log.info(result.toString());
    }

    @Test
    public void testDoltAddTable() {

        DoltAdd doltAdd = versionControl.procedure().doltAdd();
        SqlExecuteResult result = doltAdd.prepare()
                .withTable("test_table")
                .execute();

        log.info(result.toString());
    }

}
