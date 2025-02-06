package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.procedure.DoltBranch;
import com.hxuanyu.jdolt.model.ProcedureResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * DoltBranchTest
 *
 * @author hanxuanyu
 * @version 1.0
 */
@Slf4j
public class DoltBranchTest extends DoltClientTest {
    @Test
    @Order(1)
    public void testCreateBranch() {
        DoltBranch doltBranch = versionControl.doltBranch();
        DoltBranch.Params params = doltBranch.prepare()
                .create("test_new_branch")
                .force();
        ProcedureResult result = doltBranch.execute(params);
        log.info("result: {}, params: {}", result, params.toProcedureArgs());
    }

    @Test
    @Order(2)
    public void testMoveBranch() {
        DoltBranch doltBranch = versionControl.doltBranch();
        DoltBranch.Params params = doltBranch.prepare()
                .move("test_new_branch", "test_new_branch_new")
                .force();
        ProcedureResult result = doltBranch.execute(params);
        log.info("result: {}, params: {}", result, params.toProcedureArgs());
    }

    @Test
    @Order(3)
    public void testRemoveBranch() {
        DoltBranch doltBranch = versionControl.doltBranch();
        DoltBranch.Params params = doltBranch.prepare()
                .delete("test_new_branch_new")
                .force();
        ProcedureResult result = doltBranch.execute(params);
        log.info("result: {}, params: {}", result, params.toProcedureArgs());
    }
}
