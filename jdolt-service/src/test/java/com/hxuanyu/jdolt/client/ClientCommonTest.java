package com.hxuanyu.jdolt.client;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * TODO
 *
 * @author hanxuanyu
 * @version 1.0
 */
@Slf4j
public class ClientCommonTest extends DoltClientTest {

    @Test
    public void testDoltClientStatus() {
        boolean initialized = doltClient.isInitialized();
        log.info("initialized:{}", initialized);
    }

}
