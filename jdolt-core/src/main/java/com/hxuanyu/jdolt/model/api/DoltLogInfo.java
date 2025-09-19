package com.hxuanyu.jdolt.model.api;

import java.time.LocalDateTime;

/**
 * Dolt日志信息实体类
 *
 * @author hanxuanyu
 * @version 1.0
 */
public class DoltLogInfo {

    private String commitHash;
    private String committer;
    private String email;
    private LocalDateTime date;
    private String message;
    private String parents;
    private String refs;


    @Override
    public String toString() {
        return "DoltLog{" +
                "commitHash='" + commitHash + '\'' +
                ", committer='" + committer + '\'' +
                ", email='" + email + '\'' +
                ", date=" + date +
                ", message='" + message + '\'' +
                ", parents='" + parents + '\'' +
                ", refs='" + refs + '\'' +
                '}';
    }

    public String getParents() {
        return parents;
    }

    public void setParents(String parents) {
        this.parents = parents;
    }

    public String getRefs() {
        return refs;
    }

    public void setRefs(String refs) {
        this.refs = refs;
    }

    public DoltLogInfo(String commitHash, String committer, String email, LocalDateTime date, String message, String parents, String refs) {
        this.commitHash = commitHash;
        this.committer = committer;
        this.email = email;
        this.date = date;
        this.message = message;
        this.parents = parents;
        this.refs = refs;
    }

    public DoltLogInfo() {
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
