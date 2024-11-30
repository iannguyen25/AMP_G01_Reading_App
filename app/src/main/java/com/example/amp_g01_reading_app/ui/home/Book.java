package com.example.amp_g01_reading_app.ui.home;

import com.google.firebase.Timestamp;
import com.google.type.DateTime;

public class Book {
    private String title;
    private String pages;
    private int coverResourceId;
    private String description;
    private String content;
    private String author;
    private String genre_id;
    private String age_group;
    private String cover_image;
    private PublishedDate published_date;
    private boolean is_published;

    public Book(String title, String pages, int coverResourceId, String author, String age_group) {
        this.title = title;
        this.pages = pages;
        this.coverResourceId = coverResourceId;
        this.author = author;
        this.age_group = age_group;
    }

    public Book() {
    }

    public void setCoverResourceId(int coverResourceId) {
        this.coverResourceId = coverResourceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor_id() {
        return author;
    }

    public void setAuthor_id(String author) {
        this.author = author;
    }

    public String getGenre_id() {
        return genre_id;
    }

    public void setGenre_id(String genre_id) {
        this.genre_id = genre_id;
    }

    public String getAge_range() {
        return age_group;
    }

    public void setAge_range(String age_group) {
        this.age_group = age_group;
    }

    public PublishedDate getPublished_date() {
        return published_date;
    }

    public void setPublished_date(PublishedDate  published_date) {
        this.published_date = published_date;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public boolean isIs_published() {
        return is_published;
    }

    public void setIs_published(boolean is_published) {
        this.is_published = is_published;
    }

    public String getTitle() {
        return title;
    }

    public String getPages() {
        return pages;
    }

    public int getCoverResourceId() {
        return coverResourceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return coverResourceId == book.coverResourceId &&
                title.equals(book.title) &&
                pages.equals(book.pages);
    }
}


