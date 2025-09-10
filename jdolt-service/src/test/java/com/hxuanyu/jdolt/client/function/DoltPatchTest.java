package com.hxuanyu.jdolt.client.function;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.model.SqlExecuteResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 *
 * @author hanxuanyu
 * @version 1.0
 */
@Slf4j
public class DoltPatchTest extends DoltClientTest {


    @Test
    public void testDoltPatch() {

        SqlExecuteResult execute = versionControl.function().doltPatch().prepare()
                .fromRevision("oqo3l2g7adr83a38q6i7ccd7d9a0i5ms")
                .toRevision("HEAD")
                .execute();
        execute.print();

    }

}
