package com.hxuanyu.jdolt.model.api;

import java.time.LocalDateTime;

/**
 * 提交信息实体类
 *
 * @author hanxuanyu
 * @version 1.0
 */
public class CommitInfo {

    private String commitHash;
    private String committer;
    private String email;
    private LocalDateTime date;
    private String message;

    public CommitInfo(String commitHash, String committer, String email, LocalDateTime date, String message) {
        this.commitHash = commitHash;
        this.committer = committer;
        this.email = email;
        this.date = date;
        this.message = message;
    }

    @Override
    public String toString() {
        return "CommitInfo{" +
                "commitHash='" + commitHash + '\'' +
                ", committer='" + committer + '\'' +
                ", email='" + email + '\'' +
                ", date=" + date +
                ", message='" + message + '\'' +
                '}';
    }

    public CommitInfo() {
    }

    public String getCommitHash() {
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }

    public String getCommitter() {
        return committer;
    }

    public void setCommitter(String committer) {
        this.committer = committer;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
