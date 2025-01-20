package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.VersionControl;
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
        doltBranch.newBranch("testNewBranch");
        doltBranch.newBranch("testNewBranchWithParent", "main");
    }

    @Test
    public void testRenameBranch() {
        DoltBranch doltBranch = versionControl.doltBranch();
        ProcedureResult result = doltBranch.call("-m", "testNewBranch", "tetNewBranchRenamed");
        log.info("result: {}", result);

    }

    @Test
    public void testDeleteBranch() {
        DoltBranch doltBranch = versionControl.doltBranch();
        doltBranch.deleteBranch("tetNewBranchRenamed");
        doltBranch.deleteBranch("new_branch");
    }
}
