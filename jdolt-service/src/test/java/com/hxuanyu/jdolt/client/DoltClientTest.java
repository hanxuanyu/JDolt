package com.hxuanyu.jdolt.client;

import com.hxuanyu.jdolt.api.DoltClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * TODO
 *
 * @author hanxuanyu
 * @version 1.0
 */
@SpringBootTest
@Slf4j
public class DoltClientTest {

    @Autowired
    DoltClient doltClient;

    @Test
    public void testDoltClient() {
        List<String> databases = doltClient.versionControl().showDatabases();
        log.info("databases: {}", databases);
    }
}
