package com.hxuanyu.jdolt.client.function;

import com.hxuanyu.jdolt.client.DoltClientTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltHashOfTableTest extends DoltClientTest {
    @Test
    public void testHashOfTable() {
        versionControl.doltHashOfTable()
                .prepare()
                .withTable("test_table")
                .execute();
    }
}
