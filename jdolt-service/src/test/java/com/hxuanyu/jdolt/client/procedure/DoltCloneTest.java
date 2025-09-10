package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.procedure.DoltClone;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltCloneTest extends DoltClientTest {

    @Test
    public void testClone() {

        DoltClone doltClone = versionControl.procedure().doltClone();

        doltClone.prepare()
                .withUrl("file:/home/dolt/data/test_database/.dolt/noms")
                .withBranch("testAddData")
                .specifyDB("clonedDbName")
                .specifyRemoteName("testRemote")
                .execute();
    }

}
