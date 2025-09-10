package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltPullTest extends DoltClientTest {

    @Test
    public void testPullNoArgs() {
        versionControl.procedure().doltPull().call();
    }

    @Test
    public void testPullOriginAndBranch() {
        versionControl.procedure().doltPull().prepare()
                .withRemote("testRemote")
                .withBranch("testAddData")
                .execute();
    }

    @Test
    public void testPullOriginAndBranchNoFF() {
        versionControl.procedure().doltPull().prepare()
                .withRemote("testRemote")
                .withBranch("testAddData")
                .noFastForward()
                .execute();
    }

}
