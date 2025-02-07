package com.hxuanyu.jdolt.client.function;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.function.ActiveBranch;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class ActiveBranchTest extends DoltClientTest {
    @Test
    public void testInvoke() {

        ActiveBranch activeBranch = versionControl.activeBranch();
        activeBranch.prepare().execute();

    }
}
