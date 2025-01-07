package com.hxuanyu.jdolt.client;

import com.hxuanyu.jdolt.api.DoltClient;
import com.hxuanyu.jdolt.core.VersionControl;
import com.hxuanyu.jdolt.model.ProcedureResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * dolt客户端测试
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
    public void testDoltAdd() {
        VersionControl versionControl = doltClient.versionControl();
        log.info("addAll: {}", versionControl.doltAdd().addAll(Object.class));
        log.info("addAll: {}", versionControl.doltAdd().addAll());
        log.info("call: {}", versionControl.doltAdd().call(Object.class,"."));
        log.info("call: {}", versionControl.doltAdd().call("."));
    }

    @Test
    public void testCommonSql() {
        String sqlTemplate = "call dolt_add('.')";

    }
}
