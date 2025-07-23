package com.hxuanyu.jdolt.client.function;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.function.table.DoltSchemaDiff;
import com.hxuanyu.jdolt.model.SqlExecuteResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * DoltSchemaDiff表函数测试类
 *
 * @author hanxuanyu
 * @version 1.0
 */
@Slf4j
public class DoltSchemaDiffTest extends DoltClientTest {

    /**
     * 测试使用from_revision和to_revision参数的DoltSchemaDiff调用
     * 计算两个提交之间的模式差异
     */
    @Test
    public void testDoltSchemaDiffWithFromAndToRevision() {
        DoltSchemaDiff doltSchemaDiff = versionControl.doltSchemaDiff();
        SqlExecuteResult result = doltSchemaDiff.prepare()
                .fromRevision("main~")
                .toRevision("main")
                .execute();
        log.info("DoltSchemaDiff with from_revision and to_revision parameters:");
        result.print();
    }

    /**
     * 测试使用两个点的差异表达式的DoltSchemaDiff调用
     * 格式为from_revision..to_revision
     */
    @Test
    public void testDoltSchemaDiffWithTwoPointDiff() {
        DoltSchemaDiff doltSchemaDiff = versionControl.doltSchemaDiff();
        SqlExecuteResult result = doltSchemaDiff.prepare()
                .twoPointDiff("main~..main")
                .execute();
        log.info("DoltSchemaDiff with two-point diff expression (from_revision..to_revision):");
        result.print();
    }

    /**
     * 测试使用三个点的差异表达式的DoltSchemaDiff调用
     * 格式为from_revision...to_revision
     */
    @Test
    public void testDoltSchemaDiffWithThreePointDiff() {
        DoltSchemaDiff doltSchemaDiff = versionControl.doltSchemaDiff();
        SqlExecuteResult result = doltSchemaDiff.prepare()
                .threePointDiff("main~...main")
                .execute();
        log.info("DoltSchemaDiff with three-point diff expression (from_revision...to_revision):");
        result.print();
    }

    /**
     * 测试使用from_revision、to_revision和tableName参数的DoltSchemaDiff调用
     * 计算指定表在两个提交之间的模式差异
     */
    @Test
    public void testDoltSchemaDiffWithTableName() {
        // 假设有一个名为"test_table"的表
        String tableName = "test_table";
        DoltSchemaDiff doltSchemaDiff = versionControl.doltSchemaDiff();
        SqlExecuteResult result = doltSchemaDiff.prepare()
                .fromRevision("main~")
                .toRevision("main")
                .tableName(tableName)
                .execute();
        log.info("DoltSchemaDiff with from_revision, to_revision, and table_name parameters:");
        result.print();
    }

    /**
     * 测试使用两个点的差异表达式和tableName参数的DoltSchemaDiff调用
     */
    @Test
    public void testDoltSchemaDiffWithTwoPointDiffAndTableName() {
        // 假设有一个名为"test_table"的表
        String tableName = "test_table";
        DoltSchemaDiff doltSchemaDiff = versionControl.doltSchemaDiff();
        SqlExecuteResult result = doltSchemaDiff.prepare()
                .twoPointDiff("main~..main")
                .tableName(tableName)
                .execute();
        log.info("DoltSchemaDiff with two-point diff expression and table_name parameter:");
        result.print();
    }
}