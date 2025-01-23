package com.hxuanyu.jdolt.client;

import com.hxuanyu.jdolt.core.api.DoltClient;
import com.hxuanyu.jdolt.core.api.VersionControl;
import lombok.extern.slf4j.Slf4j;
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
public class DoltClientTest {

    protected DoltClient doltClient;
    protected VersionControl versionControl;

    @Autowired
    public void setDoltClient(DoltClient doltClient) {
        this.doltClient = doltClient;
        this.versionControl = doltClient.versionControl();
    }
}
