package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltPurgeDroppedDatabasesTest extends DoltClientTest {

    @Test
    public void testPurge() {
        versionControl.procedure().doltPurgeDroppedDatabases().prepare().execute();
    }
}
