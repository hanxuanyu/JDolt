package com.hxuanyu.jdolt.client.function;

import com.hxuanyu.jdolt.client.DoltClientTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltHashOfDbTest extends DoltClientTest {
    @Test
    public void testHashOfDbNoArgs() {
        versionControl.doltHashOfDB()
                .prepare()
                .execute();
    }


    @Test
    public void testHashOfDbWithBranch() {
        versionControl.doltHashOfDB()
                .prepare()
                .withBranch("testAddData")
                .execute();
    }

    @Test
    public void testHashOfDbWithHead() {
        versionControl.doltHashOfDB()
                .prepare()
                .withHead()
                .execute();
    }

    @Test
    public void testHashOfDbWithWorking() {
        versionControl.doltHashOfDB()
                .prepare()
                .withWorking()
                .execute();
    }

    @Test
    public void testHashOfDbWithStaged() {
        versionControl.doltHashOfDB()
                .prepare()
                .withStaged()
                .execute();
    }
}
