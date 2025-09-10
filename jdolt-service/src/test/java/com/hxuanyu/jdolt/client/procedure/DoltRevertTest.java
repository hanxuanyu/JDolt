package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltRevertTest extends DoltClientTest {
    @Test
    public void testRevert() {

        versionControl.procedure().doltRevert()
                .prepare()
                .withAuthor("hxuanyu", "hxuanyu@hxuanyu.com")
                .withRevision("dksccqfgljn1ssbe93v51o4ggcp2pp80")
                .withRelativeRevision("HEAD", 0)
                .execute();

    }
}
