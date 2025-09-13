package com.hxuanyu.jdolt.client.systemtable;

import com.hxuanyu.jdolt.client.DoltClientTest;
import com.hxuanyu.jdolt.core.systemtable.DoltSystemTable;
import com.hxuanyu.jdolt.model.WhereCondition;
import com.hxuanyu.jdolt.util.builder.SqlBuilder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 *
 * @author hanxuanyu
 * @version 1.0
 */
/**
 * ## {@code dolt_commit_ancestors}
 *
 * {@code dolt_commit_ancestors} 表记录数据库中每个提交的祖先。每个提交有一个或两个祖先；合并提交（merge commit）则有两个。
 *
 * ### 架构（Schema）
 *
 * 每个提交哈希在该表中有一条或两条记录，取决于它是否有一个或两个父提交。数据库的根提交（root commit）其父提交为 {@code NULL}。
 * 对于合并提交，合并基（merge base）的 {@code parent_index} 为 0，被合并的提交的 {@code parent_index} 为 1。
 *
 * <pre><code class="language-text">
 * +--------------+------+------+-----+---------+-------+
 * | Field        | Type | Null | Key | Default | Extra |
 * +--------------+------+------+-----+---------+-------+
 * | commit_hash  | text | NO   | PRI |         |       |
 * | parent_hash  | text | NO   | PRI |         |       |
 * | parent_index | int  | NO   | PRI |         |       |
 * +--------------+------+------+-----+---------+-------+
 * </code></pre>
 */
public class DoltSystemTableTest extends DoltClientTest {


    @Test
    public void testDoltCommitAncestors() {
        versionControl.systemTable().commitAncestors().prepare().execute().print();
    }


    @Test
    public void testDoltBlame() {
        versionControl.systemTable().blame().prepare().withTableNameSuffix("orders").execute().print();
    }

    @Test
    public void testDoltStatistics() {
        versionControl.systemTable().statistics().prepare().execute().print();
    }

    @Test
    public void testDoltTags() {
        versionControl.systemTable().tags().prepare().execute().print();
    }

    @Test
    public void testDoltSchemas() {
        versionControl.systemTable().schemas().prepare().execute().print();
    }

    @Test
    public void testDoltBackups() {
        versionControl.systemTable().backups().prepare().execute().print();
    }

    @Test
    public void testDoltRemotes() {
        versionControl.systemTable().remotes().prepare().execute().print();
    }

    @Test
    public void testDoltQueryCatalog() {
        versionControl.systemTable().queryCatalog().prepare().execute().printJson();

    }

    @Test
    public void testDoltBranches() {
        versionControl.systemTable().branches().prepare()
                .orderBy("name")
                .limit(10)
                .offset(0)
                .execute().printJson();
    }

    @Test
    public void testDoltCommits() {

        versionControl.systemTable().commits().prepare().execute().printJson();

    }

    @Test
    public void testWhereConditionCreation() {
        // 测试WhereCondition的各种创建方法
        WhereCondition equals = WhereCondition.equals("name", "test");
        assertEquals("name", equals.getColumn());
        assertEquals(WhereCondition.Operator.EQUALS, equals.getOperator());
        assertEquals("test", equals.getValue());

        WhereCondition notEquals = WhereCondition.notEquals("status", "inactive");
        assertEquals(WhereCondition.Operator.NOT_EQUALS, notEquals.getOperator());

        WhereCondition like = WhereCondition.like("description", "%keyword%");
        assertEquals(WhereCondition.Operator.LIKE, like.getOperator());

        WhereCondition isNull = WhereCondition.isNull("deleted_at");
        assertEquals(WhereCondition.Operator.IS_NULL, isNull.getOperator());
        assertFalse(isNull.needsValue());
    }

    @Test
    public void testSqlBuilderWithWhereConditions() {
        // 测试SqlBuilder支持WhereCondition
        SqlBuilder builder = SqlBuilder.select()
                .from("users")
                .where(WhereCondition.equals("active", true))
                .where(WhereCondition.like("name", "%john%"))
                .where(WhereCondition.greaterThan("age", 18))
                .orderBy("name")
                .limit(10)
                .offset(0);

        SqlBuilder.SqlTemplate template = builder.build();
        String sql = template.sql();
        List<Object> params = template.parameters();

        System.out.println("[DEBUG_LOG] Generated SQL: " + sql);
        System.out.println("[DEBUG_LOG] Parameters: " + params);

        assertTrue(sql.contains("WHERE"));
        assertTrue(sql.contains("active = ?"));
        assertTrue(sql.contains("name LIKE ?"));
        assertTrue(sql.contains("age > ?"));
        assertTrue(sql.contains("ORDER BY"));
        assertTrue(sql.contains("LIMIT"));
        assertTrue(sql.contains("OFFSET"));

        assertEquals(5, params.size()); // 3 WHERE params + LIMIT + OFFSET
        assertEquals(true, params.get(0));
        assertEquals("%john%", params.get(1));
        assertEquals(18, params.get(2));
        assertEquals(10, params.get(3));
        assertEquals(0, params.get(4));
    }

    @Test
    public void testDoltSystemTableParamsCreation() {
        // 测试WhereCondition参数构建（不依赖具体数据库连接）
        WhereCondition equalsCondition = WhereCondition.equals("name", "main");
        WhereCondition notEqualsCondition = WhereCondition.notEquals("hash", "");
        WhereCondition likeCondition = WhereCondition.like("message", "%fix%");
        
        assertNotNull(equalsCondition);
        assertNotNull(notEqualsCondition);
        assertNotNull(likeCondition);
        
        assertEquals("name", equalsCondition.getColumn());
        assertEquals("main", equalsCondition.getValue());
        assertEquals(WhereCondition.Operator.EQUALS, equalsCondition.getOperator());
        
        System.out.println("[DEBUG_LOG] WhereCondition objects created successfully");
        System.out.println("[DEBUG_LOG] Equals condition: " + equalsCondition);
        System.out.println("[DEBUG_LOG] NotEquals condition: " + notEqualsCondition);
        System.out.println("[DEBUG_LOG] Like condition: " + likeCondition);
    }

    @Test
    public void testDifferentOperators() {
        // 测试不同的比较操作符
        SqlBuilder builder = SqlBuilder.select("*")
                .from("test_table")
                .where(WhereCondition.equals("status", "active"))
                .where(WhereCondition.notEquals("deleted", true))
                .where(WhereCondition.like("name", "test%"))
                .where(WhereCondition.greaterThan("score", 85))
                .where(WhereCondition.lessThanOrEqual("age", 65))
                .where(WhereCondition.isNull("archived_at"))
                .where(WhereCondition.isNotNull("created_at"));

        SqlBuilder.SqlTemplate template = builder.build();
        String sql = template.sql();

        System.out.println("[DEBUG_LOG] Different operators SQL: " + sql);

        assertTrue(sql.contains("status = ?"));
        assertTrue(sql.contains("deleted != ?"));
        assertTrue(sql.contains("name LIKE ?"));
        assertTrue(sql.contains("score > ?"));
        assertTrue(sql.contains("age <= ?"));
        assertTrue(sql.contains("archived_at IS NULL"));
        assertTrue(sql.contains("created_at IS NOT NULL"));

        // 验证参数数量（IS NULL和IS NOT NULL不需要参数）
        assertEquals(5, template.parameters().size());
    }
}
