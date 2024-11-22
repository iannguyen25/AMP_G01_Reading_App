package com.example.amp_g01_reading_app.ui.home;

public class Book {
    private String title;
    private String pages;
    private int coverResourceId;

    public Book(String title, String pages, int coverResourceId) {
        this.title = title;
        this.pages = pages;
        this.coverResourceId = coverResourceId;
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