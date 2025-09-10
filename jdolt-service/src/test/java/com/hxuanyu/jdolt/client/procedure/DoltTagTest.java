package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltTagTest extends DoltClientTest {

    @Test
    @Order(1)
    public void testDoltTagAdd() {
        versionControl.procedure().doltTag()
                .prepare()
                .withName("testTag01")
                .execute();
    }

    @Test
    @Order(2)
    public void testDoltAddWithRef() {
        versionControl.procedure().doltTag()
                .prepare()
                .withName("testTag02")
                .withRef("mn0a0va835bg9vihruu5v7t9blp64pj9")
                .withMessage("add tag testTag02")
                .withAuthor("hxuanyu", "hxuanyu@hxuanyu.com")
                .execute();
    }

    @Test
    @Order(3)
    public void testDeleteTag() {
        versionControl.procedure().doltTag()
                .prepare()
                .delete("testTag01")
                .execute();

        versionControl.procedure().doltTag()
                .prepare()
                .delete("testTag02")
                .execute();
    }

}
