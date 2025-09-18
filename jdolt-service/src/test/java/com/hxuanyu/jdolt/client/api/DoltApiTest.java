package com.hxuanyu.jdolt.client.api;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.model.api.BranchInfo;
import com.hxuanyu.jdolt.model.api.CommitInfo;
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


}
