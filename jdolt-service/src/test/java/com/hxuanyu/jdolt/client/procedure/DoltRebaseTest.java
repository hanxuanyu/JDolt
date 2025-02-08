package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltRebaseTest extends DoltClientTest {
    @Test
    public void testDoltRebase() {
        versionControl.doltRebase()
                .prepare()
                .withUpstream("testAddData")
                .start()
                .execute();
    }
}
