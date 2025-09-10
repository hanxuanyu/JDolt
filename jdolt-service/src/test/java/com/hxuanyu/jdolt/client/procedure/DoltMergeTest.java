package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltMergeTest extends DoltClientTest {

    @Test
    public void testDoltMergeSquashed() {
        versionControl.procedure().doltMerge().prepare()
                .withSquash()
                .withBranch("testAddData")
                .execute();
    }

    @Test
    public void testDoltMergeNoFF() {
        versionControl.procedure().doltMerge().prepare()
                .noFastForward()
                .withBranch("testAddData")
                .execute();
    }

    @Test
    public void testDoltMergeNoFFWithCommitDesc() {
        versionControl.procedure().doltMerge().prepare()
                .noFastForward()
                .withBranch("testAddData")
                .withAuthor("hxuanyu", "hxuanyu@hxuanyu.com")
                .withMessage("testMerge")
                .execute();
    }

}
