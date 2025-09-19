package com.hxuanyu.jdolt.client.api;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.model.SqlExecuteResult;
import com.hxuanyu.jdolt.model.api.BranchInfo;
import com.hxuanyu.jdolt.model.api.CommitInfo;
import com.hxuanyu.jdolt.model.api.DoltLogInfo;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    public void testDoltBranches() {
        List<BranchInfo> branches = doltClient.api().branches();
        log.info("branches:{}", branches);
    }

    @Test
    public void testDoltCommits() {
        List<CommitInfo> commits = doltClient.api().commits();
        log.info("commits:{}", commits);
    }

    @Test
    public void testDoltActiveBranch() {
        String activeBranch = doltClient.api("feature/pricing-strategy").activeBranch();
        log.info("activeBranch:{}", activeBranch);
    }

    @Test
    public void testDoltLogs() {
        List<DoltLogInfo> logs = doltClient.api("main").logs("main");
        log.info("logs:{}", logs);
    }

    @Test
    public void testCommonQuery() {

        SqlBuilder.SqlTemplate products = SqlBuilder.select("*").from("products").limit(10).build();

        log.info("products:{}", products);

        SqlExecuteResult main = doltClient.api("main").commonSql("SELECT * FROM products LIMIT ?, ? ", "8", "5");
        main.print();

    }

}
