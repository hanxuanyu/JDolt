package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.procedure.DoltBackup;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltBackupTest extends DoltClientTest {

    @Test
    public void testAddUrl() {
        DoltBackup doltBackup = versionControl.procedure().doltBackup();

        doltBackup.prepare()
                .addSyncUrl("testUrl", "file:/home/dolt/doltBackupTest")
                .execute();
    }

    @Test
    public void testBackup() {
        DoltBackup doltBackup = versionControl.procedure().doltBackup();
        doltBackup.prepare()
                .sync("testUrl")
                .execute();
    }


    @Test
    public void testRestore() {
        DoltBackup doltBackup = versionControl.procedure().doltBackup();
        doltBackup.prepare()
                .restore("file:/home/dolt/doltBackupTest", "restoredDb2")
                .execute();
    }

    @Test
    public void testRemoveUrl() {
        DoltBackup doltBackup = versionControl.procedure().doltBackup();

        doltBackup.prepare()
                .removeUrl("testUrl")
                .execute();
    }

}
