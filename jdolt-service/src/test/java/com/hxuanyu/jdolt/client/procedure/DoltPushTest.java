package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltPushTest extends DoltClientTest {
    @Test
    public void testPushNoArgs() {
        versionControl.doltPush().call();
    }


    @Test
    public void testPushWithBranch() {
        versionControl.doltPush().prepare()
                .withRemote("testRemote")
                .withRefSpec("testAddData")
                .execute();
    }

    @Test
    public void testPushWithBranchForced() {
        versionControl.doltPush().prepare()
                .withRemote("testRemote")
                .pushAll()
                .force()
                .setUpStream()
                .execute();
    }
}
