package com.example.amp_g01_reading_app.ui.comments;

public class Comment {
    private String story_id;   // ID của câu chuyện
    private String user_id;    // ID của người dùng
    private String email;
    private String comment;    // Nội dung bình luận

    // Constructor
    public Comment(String story_id, String user_id,String email,  String comment) {
        this.story_id = story_id;
        this.user_id = user_id;
        this.email = email;
        this.comment = comment;
    }


    public String getStoryId() {
        return story_id;
    }

    public void setStoryId(String story_id) {
        this.story_id = story_id;
    }


    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }
//
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}