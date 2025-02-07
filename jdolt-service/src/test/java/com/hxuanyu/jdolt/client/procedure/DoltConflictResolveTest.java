package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.procedure.DoltConflictsResolve;
import com.hxuanyu.jdolt.model.SqlExecuteResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltConflictResolveTest extends DoltClientTest {

    @Test
    public void testDoltConflictResolve() {
        DoltConflictsResolve doltConflictsResolve = versionControl.doltConflictsResolve();

        SqlExecuteResult result = doltConflictsResolve.prepare()
                .acceptOurs()
                .withTables("test_table")
                .execute();

    }
}
