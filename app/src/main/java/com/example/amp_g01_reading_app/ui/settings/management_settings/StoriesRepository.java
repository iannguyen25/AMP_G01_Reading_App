package com.example.amp_g01_reading_app.ui.settings.management_settings;

import android.util.Log;

import com.example.amp_g01_reading_app.ui.home.Book;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class StoriesRepository {
    private final FirebaseFirestore db;

    public StoriesRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void getStoriesByAgeRange(int minAge, int maxAge, OnStoriesLoadedListener listener) {
        db.collection("book")
                .whereGreaterThanOrEqualTo("minAge", minAge)
                .whereLessThanOrEqualTo("maxAge", maxAge)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Book> stories = queryDocumentSnapshots.toObjects(Book.class);
                    listener.onStoriesLoaded(stories);
                })
                .addOnFailureListener(e -> {
                    logError("Failed to load stories", e);
                    listener.onError(e);
                });
    }

    private void logError(String message, Exception e) {
        Log.e("StoriesRepository", message, e);
    }

    public interface OnStoriesLoadedListener {
        void onStoriesLoaded(List<Book> stories);
        void onError(Exception e); // Trả về Exception thay vì chuỗi lỗi
    }
}
