package com.example.electronicsmarket;

public class exampleData {
    private String userId,content;

    public exampleData(String userId, String content) {
        this.userId = userId;
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
