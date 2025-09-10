package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltGcTest extends DoltClientTest {

    @Test
    public void testGcDirectCall() {
        versionControl.procedure().doltGc().call();
    }

    @Test
    public void testGcWithParam() {
        versionControl.procedure().doltGc().prepare().execute();
    }

    @Test
    public void testGcWithShallow() {
        versionControl.procedure().doltGc().prepare().shallow().execute();
    }

}
