package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.procedure.DoltBranch;
import com.hxuanyu.jdolt.model.ProcedureResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * TODO
 *
 * @author hanxuanyu
 * @version 1.0
 */
@Slf4j
public class DoltBranchTest extends DoltClientTest {
    @Test
    public void testCreateBranch() {
        DoltBranch doltBranch = versionControl.doltBranch();
        DoltBranch.Params params = doltBranch.prepare()
                .create("test_new_branch")
                .force()
                .build();
        ProcedureResult result = doltBranch.execute(params);
        log.info("result: {}", result);
    }

    @Test
    public void testMoveBranch() {
        DoltBranch doltBranch = versionControl.doltBranch();
        DoltBranch.Params params = doltBranch.prepare()
                .move("test_new_branch", "test_new_branch_new")
                .build();
        ProcedureResult result = doltBranch.execute(params);
        log.info("result: {}", result);
    }

    @Test
    public void testRemoveBranch() {
        DoltBranch doltBranch = versionControl.doltBranch();
        DoltBranch.Params params = doltBranch.prepare()
                .delete("test_new_branch_new")
                .build();
        ProcedureResult result = doltBranch.execute(params);
        log.info("result: {}", result);
    }
}
