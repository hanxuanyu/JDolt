package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.procedure.DoltAdd;
import com.hxuanyu.jdolt.model.ProcedureResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltAddTest extends DoltClientTest {

    @Test
    public void testDoltAddAll() {

        DoltAdd doltAdd = versionControl.doltAdd();
        ProcedureResult result = doltAdd.execute(
                doltAdd.prepare()
                        .addAll()
                        .build()
        );

        log.info(result.toString());
    }

    @Test
    public void testDoltAddCurrent() {

        DoltAdd doltAdd = versionControl.doltAdd();
        ProcedureResult result = doltAdd.execute(
                doltAdd.prepare()
                        .addCurrent()
                        .build()
        );

        log.info(result.toString());
    }

    @Test
    public void testDoltAddTable() {

        DoltAdd doltAdd = versionControl.doltAdd();
        ProcedureResult result = doltAdd.execute(
                doltAdd.prepare()
                        .withTable("test_table")
                        .build()
        );

        log.info(result.toString());
    }

}
