package com.hxuanyu.jdolt.client.function;

import com.hxuanyu.jdolt.client.DoltClientTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltMergeBaseTest extends DoltClientTest {

    @Test
    public void testDoltMergeBase() {

        versionControl.function().doltMergeBase().prepare()
                .withBranch("main", "testAddData").execute();

    }
}
