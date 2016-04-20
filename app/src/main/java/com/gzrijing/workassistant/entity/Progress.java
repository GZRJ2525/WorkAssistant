package com.gzrijing.workassistant.entity;

public class Progress {
    private String time;
    private String content;

    public Progress() {
    }

    public Progress(String time, String content) {
        this.time = time;
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
