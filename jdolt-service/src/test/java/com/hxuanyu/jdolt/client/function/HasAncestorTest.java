package com.hxuanyu.jdolt.client.function;

import com.hxuanyu.jdolt.client.DoltClientTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class HasAncestorTest extends DoltClientTest {
    @Test
    public void testHasAncestor() {

        versionControl.function().hasAncestor()
                .prepare()
                .check("HEAD", "testAddData")
                .execute();

    }
}
