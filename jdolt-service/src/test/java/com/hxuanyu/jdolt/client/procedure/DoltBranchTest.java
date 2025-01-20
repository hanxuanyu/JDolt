package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.VersionControl;
import com.hxuanyu.jdolt.core.procedure.DoltBranch;
import org.junit.jupiter.api.Test;

/**
 * TODO
 *
 * @author hanxuanyu
 * @version 1.0
 */

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
        doltBranch.renameBranch("testNewBranch", "tetNewBranchRenamed");
        doltBranch.renameBranchForced("newBranch", "tetNewBranchRenamed");
    }

    @Test
    public void testDeleteBranch() {
        DoltBranch doltBranch = versionControl.doltBranch();
        doltBranch.deleteBranch("tetNewBranchRenamed");
        doltBranch.deleteBranch("new_branch");
    }
}
