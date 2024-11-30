package com.example.amp_g01_reading_app.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;
import java.util.List;


public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<Book>> popularBooks;
    private final MutableLiveData<List<Book>> newBooks;

    public HomeViewModel() {
        popularBooks = new MutableLiveData<>();
        newBooks = new MutableLiveData<>();
        loadInitialData();
    }

    private void loadInitialData() {
        // Populate with sample data
        List<Book> popular = new ArrayList<>();
//        popular.add(new Book("Jungle Book", "4 Pages", R.drawable.jungle_book));
//        popular.add(new Book("Kindworld", "4 Pages", R.drawable.jungle_book));
//        popular.add(new Book("Kindworld", "4 Pages", R.drawable.jungle_book));
//        popular.add(new Book("Kindworld", "4 Pages", R.drawable.jungle_book));
//        popular.add(new Book("Kindworld", "4 Pages", R.drawable.jungle_book));
//        popular.add(new Book("Kindworld", "4 Pages", R.drawable.jungle_book));
        popularBooks.setValue(popular);

        List<Book> newBooksList = new ArrayList<>();
//        newBooksList.add(new Book("World Book", "3 Pages", R.drawable.jungle_book));
//        newBooksList.add(new Book("Help Book", "5 Pages", R.drawable.jungle_book));

        newBooks.setValue(newBooksList);
    }

        public LiveData<List<Book>> getPopularBooks () {
            return popularBooks;
        }

        public LiveData<List<Book>> getNewBooks () {
            return newBooks;
        }
}