package com.hxuanyu.jdolt.model.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Git分支信息实体类
 *
 * @author hanxuanyu
 */
public class BranchInfo {

    /**
     * 最新提交消息
     */
    private String latestCommitMessage;

    /**
     * 分支名称
     */
    private String name;

    /**
     * 最新提交者邮箱
     */
    private String latestCommitterEmail;

    /**
     * 最新提交日期
     */
    private LocalDateTime latestCommitDate;

    /**
     * 远程仓库
     */
    private String remote;

    /**
     * 分支
     */
    private String branch;

    /**
     * 提交哈希
     */
    private String hash;

    /**
     * 最新提交者
     */
    private String latestCommitter;

    // 无参构造器
    public BranchInfo() {
    }

    // 全参构造器
    public BranchInfo(String latestCommitMessage, String name, String latestCommitterEmail,
                      LocalDateTime latestCommitDate, String remote, String branch,
                      String hash, String latestCommitter) {
        this.latestCommitMessage = latestCommitMessage;
        this.name = name;
        this.latestCommitterEmail = latestCommitterEmail;
        this.latestCommitDate = latestCommitDate;
        this.remote = remote;
        this.branch = branch;
        this.hash = hash;
        this.latestCommitter = latestCommitter;
    }

    // Getter 和 Setter 方法
    public String getLatestCommitMessage() {
        return latestCommitMessage;
    }

    public void setLatestCommitMessage(String latestCommitMessage) {
        this.latestCommitMessage = latestCommitMessage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLatestCommitterEmail() {
        return latestCommitterEmail;
    }

    public void setLatestCommitterEmail(String latestCommitterEmail) {
        this.latestCommitterEmail = latestCommitterEmail;
    }

    public LocalDateTime getLatestCommitDate() {
        return latestCommitDate;
    }

    public void setLatestCommitDate(LocalDateTime latestCommitDate) {
        this.latestCommitDate = latestCommitDate;
    }

    public String getRemote() {
        return remote;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getLatestCommitter() {
        return latestCommitter;
    }

    public void setLatestCommitter(String latestCommitter) {
        this.latestCommitter = latestCommitter;
    }

    @Override
    public String toString() {
        return "BranchInfo{" +
                "latestCommitMessage='" + latestCommitMessage + '\'' +
                ", name='" + name + '\'' +
                ", latestCommitterEmail='" + latestCommitterEmail + '\'' +
                ", latestCommitDate=" + latestCommitDate +
                ", remote='" + remote + '\'' +
                ", branch='" + branch + '\'' +
                ", hash='" + hash + '\'' +
                ", latestCommitter='" + latestCommitter + '\'' +
                '}';
    }
}