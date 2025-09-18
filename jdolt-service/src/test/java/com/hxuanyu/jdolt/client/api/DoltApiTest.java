package com.hxuanyu.jdolt.client.api;

import com.hxuanyu.jdolt.client.DoltClientTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * 测试DoltApi
 *
 * @author hanxuanyu
 * @version 1.0
 */
@Slf4j
public class DoltApiTest extends DoltClientTest {

    @Test
    public void testDoltVersion() {
        String doltVersion = doltClient.api().doltVersion();
        log.info("doltVersion:{}", doltVersion);
    }

}
