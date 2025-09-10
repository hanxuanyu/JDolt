package com.hxuanyu.jdolt.client.function;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.function.table.DoltRefLog;
import com.hxuanyu.jdolt.model.SqlExecuteResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * DoltRefLog表函数测试类
 *
 * @author hanxuanyu
 * @version 1.0
 */
@Slf4j
public class DoltRefLogTest extends DoltClientTest {

    /**
     * 测试不带参数的DoltRefLog调用
     * 返回完整的引用日志，按最新到最旧列出所有被跟踪的引用的更改
     */
    @Test
    public void testDoltRefLogNoParams() {
        DoltRefLog doltRefLog = versionControl.function().doltRefLog();
        SqlExecuteResult result = doltRefLog.prepare()
                .execute();
        log.info("DoltRefLog without parameters:");
        result.print();
    }

    /**
     * 测试带--all参数的DoltRefLog调用
     * 显示所有引用，包括隐藏引用，例如DoltHub工作区引用
     */
    @Test
    public void testDoltRefLogWithAllFlag() {
        DoltRefLog doltRefLog = versionControl.function().doltRefLog();
        SqlExecuteResult result = doltRefLog.prepare()
                .all()
                .execute();
        log.info("DoltRefLog with --all flag:");
        result.print();
    }

    /**
     * 测试带引用名称参数的DoltRefLog调用
     * 只返回指定引用的日志
     */
    @Test
    public void testDoltRefLogWithRefName() {
        // 使用main分支作为引用名称
        DoltRefLog doltRefLog = versionControl.function().doltRefLog();
        SqlExecuteResult result = doltRefLog.prepare()
                .refName("main")
                .execute();
        log.info("DoltRefLog with reference name 'main':");
        result.print();
    }

    /**
     * 测试同时使用--all标志和引用名称参数的DoltRefLog调用
     */
    @Test
    public void testDoltRefLogWithAllAndRefName() {
        DoltRefLog doltRefLog = versionControl.function().doltRefLog();
        SqlExecuteResult result = doltRefLog.prepare()
                .all()
                .refName("main")
                .execute();
        log.info("DoltRefLog with --all flag and reference name 'main':");
        result.print();
    }
}