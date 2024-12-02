package com.example.amp_g01_reading_app.ui.home;

public class BookMark {
    private String id;
    private String story_id;
    private String user_id;
    private String status;


    public BookMark(String status, String user_id, String story_id) {
        this.status = status;
        this.user_id = user_id;
        this.story_id = story_id;
    }

    public BookMark() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStory_id() {
        return story_id;
    }

    public void setStory_id(String story_id) {
        this.story_id = story_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
