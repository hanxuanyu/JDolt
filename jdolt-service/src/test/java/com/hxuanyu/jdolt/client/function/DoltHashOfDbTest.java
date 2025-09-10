package com.hxuanyu.jdolt.client.function;

import com.hxuanyu.jdolt.client.DoltClientTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltHashOfDbTest extends DoltClientTest {
    @Test
    public void testHashOfDbNoArgs() {
        versionControl.function().doltHashOfDB()
                .prepare()
                .execute();
    }


    @Test
    public void testHashOfDbWithBranch() {
        versionControl.function().doltHashOfDB()
                .prepare()
                .withBranch("testAddData")
                .execute();
    }

    @Test
    public void testHashOfDbWithHead() {
        versionControl.function().doltHashOfDB()
                .prepare()
                .withHead()
                .execute();
    }

    @Test
    public void testHashOfDbWithWorking() {
        versionControl.function().doltHashOfDB()
                .prepare()
                .withWorking()
                .execute();
    }

    @Test
    public void testHashOfDbWithStaged() {
        versionControl.function().doltHashOfDB()
                .prepare()
                .withStaged()
                .execute();
    }
}
