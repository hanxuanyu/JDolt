package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltUnDropTest extends DoltClientTest {

    @Test
    public void testUnDropNoArgs() {

        versionControl.procedure().doltUnDrop().prepare().execute();

    }

    @Test
    public void testUnDrop() {

        versionControl.procedure().doltUnDrop().prepare()
                .withDatabase("database1")
                .execute();

    }

}
