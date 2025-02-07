package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.procedure.DoltCherryPick;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltCherryPickTest extends DoltClientTest {


    @Test
    public void testPickByHash() {
        DoltCherryPick doltCherryPick = versionControl.doltCherryPick();
        DoltCherryPick.Params params = doltCherryPick.prepare()
                .withCommitHash("mn0a0va835bg9vihruu5v7t9blp64pj9");
        params.execute();

    }

    @Test
    public void testPickByBranchRef() {
        DoltCherryPick doltCherryPick = versionControl.doltCherryPick();
        DoltCherryPick.Params params = doltCherryPick.prepare()
                .withRelativeRef("testAddData", 1)
                .allowEmpty();
        params.execute();

    }

}
