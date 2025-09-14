package com.hxuanyu.jdolt.core.systemtable;


import com.hxuanyu.jdolt.manager.DoltConnectionManager;

import java.util.concurrent.ConcurrentHashMap;


/**
 * ## `dolt_rebase`
 *
 * `dolt_rebase` 仅在交互式变基进行中时存在，且仅存在于执行变基的分支上。比如，当对 `feature1` 分支进行变基时，变基会在 `dolt_rebase_feature1` 分支上执行，而 `dolt_rebase` 系统表会在变基进行期间存在于该分支上。`dolt_rebase` 系统表最初包含默认的变基计划，即对所有被选中参与变基的提交执行 `pick`。用户可以通过更新 `dolt_rebase` 表来调整变基计划，例如更改变基动作、修改提交消息，甚至添加新行以在变基中额外应用一些提交。有关变基的更多细节，请参阅 {@code dolt_rebase()} 存储过程。
 *
 * ### 模式
 *
 * <pre>
 * +----------------+---------------------------------------------------+
 * | Field          | Type                                              |
 * +----------------+---------------------------------------------------+
 * | rebase_order   | DECIMAL(6,2)                                      |
 * | action         | ENUM('pick', 'drop', 'reword', 'squash', 'fixup') |
 * | commit_hash    | TEXT                                              |
 * | commit_message | TEXT                                              |
 * +----------------+---------------------------------------------------+
 * </pre>
 *
 * {@code action} 字段可取以下变基动作之一：
 * <ul>
 *   <li><b>{@code pick}</b> —— 原样应用提交并保留其提交消息。</li>
 *   <li><b>{@code drop}</b> —— 不应用该提交。也可以通过删除 {@code dolt_rebase} 表中的该行来从变基计划中移除该提交。</li>
 *   <li><b>{@code reword}</b> —— 应用该提交，并使用 {@code dolt_rebase} 表中 {@code commit_message} 字段更新后的提交消息。注意，如果你编辑了 {@code commit_message} 但未将动作设置为 {@code reword}，仍会使用原始的提交消息。</li>
 *   <li><b>{@code squash}</b> —— 应用该提交，但将其更改合并到前一个提交中，而不是创建新提交。此前一个提交的提交消息会被修改，以包含前一个提交消息以及被压缩提交的提交消息。注意，变基计划在 {@code squash} 动作之前必须包含一个 {@code pick} 或 {@code reword} 动作。</li>
 *   <li><b>{@code fixup}</b> —— 应用该提交，但将其更改合并到前一个提交中，而不是创建新提交。此前一个提交的提交消息不会被更改，被修复提交（fixup）的提交消息将被丢弃。注意，变基计划在 {@code fixup} 动作之前必须包含一个 {@code pick} 或 {@code reword} 动作。</li>
 * </ul>
 *
 * ### 示例查询
 *
 * 要将所有提交压缩为单个提交并包含所有提交消息，可使用以下查询：
 * <pre>{@code
 * update dolt_rebase set action = 'squash' where rebase_order > 1;
 * }</pre>
 *
 * 要重写提交哈希为 '123aef456f' 的提交的消息，请确保将动作设置为 {@code reword} 并更新 {@code commit_message} 字段：
 * <pre>{@code
 * update dolt_rebase set action = 'reword', commit_message = 'here is my new message' where commit_hash = '123aef456f';
 * }</pre>
 *
 * 要在变基计划中删除第二个提交，可以使用 {@code drop} 动作：
 * <pre>{@code
 * update dolt_rebase set action = 'drop' where rebase_order = 2;
 * }</pre>
 *
 * 或者也可以直接从 {@code dolt_rebase} 表中删除该行：
 * <pre>{@code
 * delete from dolt_rebase where rebase_order = 2;
 * }</pre>
 */
public class DoltRebase extends DoltSystemTable {
    // 单例管理
    private static final ConcurrentHashMap<DoltConnectionManager, DoltRebase> INSTANCES = new ConcurrentHashMap<>();

    protected DoltRebase(DoltConnectionManager connectionManager) {
        super(connectionManager);
    }

    public static DoltRebase getInstance(DoltConnectionManager connectionManager) {
        return INSTANCES.computeIfAbsent(connectionManager, k -> new DoltRebase(connectionManager));
    }

    @Override
    public Params prepare() {
        return new Params(this).from("dolt_rebase");
    }
}