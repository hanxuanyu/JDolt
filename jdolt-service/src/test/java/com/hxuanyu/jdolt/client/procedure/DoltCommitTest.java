package com.hxuanyu.jdolt.client.procedure;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.procedure.DoltCommit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

@Slf4j
public class DoltCommitTest extends DoltClientTest {

    @Test
    public void testDoltCommit() {

        DoltCommit doltCommit = versionControl.procedure().doltCommit();
        doltCommit.prepare()
                .allowEmpty()
//                .skipEmpty()
                .stageAll()
                .stageAllWithNewTable()
                .withAuthor("hxuanyu", "hxuanyu@hxuanyu.com")
                .withDate(LocalDateTime.now())
                .message("testCommit")
                .execute();


    }

}
