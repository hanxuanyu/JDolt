package com.hxuanyu.jdolt.client.procedure;


import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.procedure.DoltCheckout;
import com.hxuanyu.jdolt.model.ProcedureResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltCheckoutTest extends DoltClientTest {

    @Test
    public void testCheckout() {
        DoltCheckout doltCheckout = versionControl.doltCheckout();

        ProcedureResult result = doltCheckout.prepare().newBranch("test_newBranch_01").execute();

        log.info(result.toString());

    }

}
