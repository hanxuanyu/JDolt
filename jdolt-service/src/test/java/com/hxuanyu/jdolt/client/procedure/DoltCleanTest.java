package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.procedure.DoltClean;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DoltCleanTest extends DoltClientTest {

    @Test
    public void testCleanUntracked() {
        DoltClean doltClean = versionControl.procedure().doltClean();
        DoltClean.Params params = doltClean.prepare()
                .withTableName("untracked");
        params.execute();
    }

    @Test
    public void testClean() {
        DoltClean doltClean = versionControl.procedure().doltClean();
        DoltClean.Params params = doltClean.prepare();
        params.execute();
    }

    @Test
    public void testDryRun() {
        DoltClean doltClean = versionControl.procedure().doltClean();
        DoltClean.Params params = doltClean.prepare();
        params.dryRun();
    }


}
