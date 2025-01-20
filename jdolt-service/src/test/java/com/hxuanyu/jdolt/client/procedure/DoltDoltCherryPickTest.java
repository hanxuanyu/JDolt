package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.VersionControl;
import com.hxuanyu.jdolt.core.procedure.DoltCherryPick;
import com.hxuanyu.jdolt.model.ProcedureResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltDoltCherryPickTest extends DoltClientTest {

    @Test
    public void testCall() {
        DoltCherryPick doltCherryPick = versionControl.doltCherryPick();
        ProcedureResult result = doltCherryPick.call("rqpj01210nga56vn3bcbisgo3copalp5");
        log.info("result: {}", result);
    }

}
