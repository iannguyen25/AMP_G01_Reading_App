package com.example.amp_g01_reading_app.ui.bookmark;

public class Bookmark {

    private String id; // ID của bookmark
    private String storyId; // ID của truyện
    private String UserId;
    private String title; // Tên truyện
    private String author; // Tác giả
    private String ageRange; // Độ tuổi phù hợp
    private String coverImage; // URL ảnh bìa

    // Constructor không tham số
    public Bookmark() {}

    // Constructor đầy đủ
    public Bookmark(String id, String storyId,String UserId, String title, String author, String ageRange, String coverImage) {
        this.id = id;
        this.storyId = storyId;
        this.UserId = UserId;
        this.title = title;
        this.author = author;
        this.ageRange = ageRange;
        this.coverImage = coverImage;
    }

    // Getters và Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        this.UserId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAgeRange() {
        return ageRange;
    }

    public void setAgeRange(String ageRange) {
        this.ageRange = ageRange;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }
}
