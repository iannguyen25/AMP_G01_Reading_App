package com.example.amp_g01_reading_app.ui.comments;

public class Comment {
    private String story_id;   // ID của câu chuyện
    private String user_id;    // ID của người dùng
    private String comment;    // Nội dung bình luận

    // Constructor
    public Comment(String story_id, String user_id, String comment) {
        this.story_id = story_id;
        this.user_id = user_id;
        this.comment = comment;
    }

    // Getter and Setter for story_id
    public String getStoryId() {
        return story_id;
    }

    public void setStoryId(String story_id) {
        this.story_id = story_id;
    }

    // Getter and Setter for user_id
    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    // Getter and Setter for comment
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}