package com.hxuanyu.jdolt.client;

import com.hxuanyu.jdolt.core.api.DoltClient;
import com.hxuanyu.jdolt.core.api.VersionControl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * dolt客户端测试
 *
 * @author hanxuanyu
 * @version 1.0
 */
@SpringBootTest
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // 按 @Order 注解指定的顺序执行
public class DoltClientTest {

    protected DoltClient doltClient;
    protected VersionControl versionControl;

    @Autowired
    public void setDoltClient(DoltClient doltClient) {
        this.doltClient = doltClient;
        this.versionControl = doltClient.versionControl();
    }
}
