package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.procedure.DoltBackup;
import com.hxuanyu.jdolt.model.ProcedureResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltBackupTest extends DoltClientTest {

    @Test
    public void testAddUrl() {
        DoltBackup doltBackup = versionControl.doltBackup();

        ProcedureResult result = doltBackup.execute(
                doltBackup.prepare()
                        .addSyncUrl("testUrl", "file:/home/dolt/doltBackupTest")
        );
    }

    @Test
    public void testBackup() {
        DoltBackup doltBackup = versionControl.doltBackup();
        ProcedureResult result = doltBackup.execute(
                doltBackup.prepare()
                        .sync("testUrl")
        );
    }


    @Test
    public void testRestore() {
        DoltBackup doltBackup = versionControl.doltBackup();
        ProcedureResult result = doltBackup.execute(
                doltBackup.prepare()
                        .restore("file:/home/dolt/doltBackupTest", "restoredDb2")
        );
    }

    @Test
    public void testRemoveUrl() {
        DoltBackup doltBackup = versionControl.doltBackup();

        ProcedureResult result = doltBackup.execute(
                doltBackup.prepare()
                        .removeUrl("testUrl")
        );
    }

}
