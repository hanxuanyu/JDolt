package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltRemoteTest extends DoltClientTest {

    @Test
    @Order(1)
    public void testRemoteAdd() {
        versionControl.procedure().doltRemote().prepare()
                .add("testRemote02", "file:/home/dolt/testRemote02")
                .execute();
    }


    @Test
    @Order(2)
    public void testRemoteRemove() {
        versionControl.procedure().doltRemote().prepare()
                .remove("testRemote02")
                .execute();
    }


}
