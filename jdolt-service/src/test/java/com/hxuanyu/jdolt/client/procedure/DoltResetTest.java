package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltResetTest extends DoltClientTest {
    @Test
    public void testResetNoArgs() {
        versionControl.doltReset().prepare().execute();
    }

    @Test
    public void testResetToRevision() {
        versionControl.doltReset()
                .prepare()
                .withRevision("rqpj01210nga56vn3bcbisgo3copalp5")
                .execute();
    }

    @Test
    public void testResetToRevisionHard() {
        versionControl.doltReset()
                .prepare()
                .withRevision("rqpj01210nga56vn3bcbisgo3copalp5")
                .hard()
                .execute();
    }
}
