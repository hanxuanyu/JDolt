package com.hxuanyu.jdolt.client.function;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.function.table.DoltQueryDiff;
import com.hxuanyu.jdolt.model.SqlExecuteResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * DoltQueryDiff 功能测试类
 */
@Slf4j
public class DoltQueryDiffTest extends DoltClientTest {

    @Test
    public void testDoltQueryDiff() {
        DoltQueryDiff doltQueryDiff = versionControl.doltQueryDiff();
        SqlExecuteResult result = doltQueryDiff.prepare()
                .fromQuery("SELECT 1 as id, 'A' as name")
                .toQuery("SELECT 1 as id, 'B' as name")
                .execute();
        log.info("Query diff result: {}", result);
    }

    @Test
    public void testDoltQueryDiffWithLiteralValues() {
        DoltQueryDiff doltQueryDiff = versionControl.doltQueryDiff();
        SqlExecuteResult result = doltQueryDiff.prepare()
                .fromQuery("SELECT 1 as id, 'A' as name UNION ALL SELECT 2, 'B' UNION ALL SELECT 3, 'C'")
                .toQuery("SELECT 1 as id, 'A' as name UNION ALL SELECT 2, 'X' UNION ALL SELECT 4, 'D'")
                .execute();
        log.info("Literal values query diff result: {}", result);
    }
    
    @Test
    public void testDoltQueryDiffWithComplexLiteralQueries() {
        DoltQueryDiff doltQueryDiff = versionControl.doltQueryDiff();
        SqlExecuteResult result = doltQueryDiff.prepare()
                .fromQuery("SELECT 'HR' as department, 5000 as value UNION ALL SELECT 'IT', 5750 UNION ALL SELECT 'Finance', 7000")
                .toQuery("SELECT 'HR' as department, 5000 as value UNION ALL SELECT 'IT', 6000 UNION ALL SELECT 'Finance', 7000")
                .execute();
        log.info("Complex literal query diff result: {}", result);
    }
}