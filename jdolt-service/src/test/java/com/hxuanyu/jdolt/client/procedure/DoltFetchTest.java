package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltFetchTest extends DoltClientTest {
    @Test
    public void testFetchNoArgs() {
        versionControl.doltFetch().prepare().execute();
    }

    @Test
    public void testFetch() {
        versionControl.doltFetch().prepare()
                .withRemote("origin")
                .withRef("main")
                .execute();
    }
}
