package com.example.amp_g01_reading_app.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.amp_g01_reading_app.connect.ApiClient;
import com.example.amp_g01_reading_app.connect.ApiService;
import com.example.amp_g01_reading_app.ui.bookmark.Bookmark;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;


import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.connect.ApiClient;
import com.example.amp_g01_reading_app.connect.ApiService;
import com.google.firebase.Timestamp;
import com.google.type.DateTime;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<Book>> popularBooks;
    private final MutableLiveData<List<Book>> newBooks;
    private final ApiService api;
    private final MutableLiveData<Integer> userAge = new MutableLiveData<>();
    private final MutableLiveData<String> userId = new MutableLiveData<>();
    private final MutableLiveData<List<Bookmark>> BookMarksLiveData = new MutableLiveData<>();
    private List<Book> allBooks;

    public HomeViewModel() {
        popularBooks = new MutableLiveData<>();
        newBooks = new MutableLiveData<>();
        api = ApiClient.getRetrofitInstance().create(ApiService.class);
        allBooks = new ArrayList<>();
        loadInitialData();
    }

    private void loadInitialData() {
        // Populate with sample data
        List<Book> popular = new ArrayList<>();
        api.getStories().enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                Log.d("API_RESPONSE", "Response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    allBooks = response.body();
                    String idUser = userId.getValue();
                    Log.d("BOOKS_DATA", "Số lượng sách: " + (allBooks != null ? allBooks.size() : 0));

                    fetchBookMarks(idUser);

                }
            }



            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Log.d("API_RESPONSE", "API call failed. Response code: ");
                // Handle API call failure
            }
        });


        //List<Book> newBooksList = new ArrayList<>();
//        newBooksList.add(new Book("World Book", "3 Pages", R.drawable.jungle_book));
//        newBooksList.add(new Book("Help Book", "5 Pages", R.drawable.jungle_book));

        //newBooks.setValue(newBooksList);
    }


    private void filterBooksByAge(int age) {
        if (allBooks == null || allBooks.isEmpty()) {
            popularBooks.setValue(new ArrayList<>());
            return;
        }

        List<Book> filteredBooks = new ArrayList<>();
        for (Book book : allBooks) {
            if (isAgeInGroup(age, book.getAge_group())) {
                filteredBooks.add(book);
            }
        }

        popularBooks.setValue(filteredBooks);

    }

    private void filterBooksByCurrentMonth(List<Book> books) {
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();

        Log.d("DATE_CURRENT", "DATE: " + currentMonth + "/" + currentYear);

        List<Book> newBooksList = new ArrayList<>();
        for (Book book : books) {
            if (book.getPublished_date() != null) {
                // Convert Firestore timestamp to LocalDate
                LocalDate publishedDate = Instant.ofEpochSecond(book.getPublished_date().getSeconds()).atZone(ZoneId.systemDefault()).toLocalDate();

                int publishedMonth = publishedDate.getMonthValue();
                int publishedYear = publishedDate.getYear();

                Log.d("DATE_PUBLISHED", "DATE: " + publishedMonth + "/" + publishedYear);

                if (publishedMonth == currentMonth && publishedYear == currentYear) {
                    newBooksList.add(book);
                }
            }
        }

        newBooks.setValue(newBooksList);
    }

    //Search Book
    public void searchBooks(String query) {

        if (!query.isEmpty()) {
            List<Book> filteredBooks = allBooks.stream().filter(book -> book.getTitle().toLowerCase().contains(query.toLowerCase())).collect(Collectors.toList());
            popularBooks.setValue(filteredBooks);
        } else {
            loadInitialData();
        }
    }

    //Check age
    private boolean isAgeInGroup(int userAge, String ageGroup) {
        if (ageGroup.contains("-")) {
            String[] ageRange = ageGroup.split("-");
            if (ageRange.length == 2) {
                int minAge = Integer.parseInt(ageRange[0].trim());
                int maxAge = Integer.parseInt(ageRange[1].trim());
                return userAge >= minAge && userAge <= maxAge;
            }
        } else {
            int singleAge = Integer.parseInt(ageGroup.trim());
            return userAge == singleAge;
        }
        return false;
    }


    public LiveData<List<Bookmark>> getBookMarks() {
        return BookMarksLiveData;
    }

    public void fetchBookMarks(String idUser) {


        api.getBookmarksByUserId(idUser).enqueue(new Callback<List<Bookmark>>() {
            @Override
            public void onResponse(Call<List<Bookmark>> call, Response<List<Bookmark>> response) {
                if (response.isSuccessful()) {
                    BookMarksLiveData.setValue(response.body());

                    for (Bookmark x : BookMarksLiveData.getValue()) {
                        Log.d("BOOKMARK_DATA", "ID: " + x.getStoryId());
                    }

                    // Cập nhật trạng thái isBookmark
                    for (Book book : allBooks) {
                        boolean isBookmarked = BookMarksLiveData.getValue().stream().anyMatch(bookmark -> bookmark.getStoryId().equals(book.getId()));
                        Log.d("BOOK_DATA", "ID: " + book.getId());
                        book.setBookMark(isBookmarked);
                    }

                    for (Book book : allBooks) {
                        Log.d("BOOKS_IS_BOOKMARK", "-------->:" + book.isBookMark());
                    }

                    Integer age = userAge.getValue();
                    Log.d("AGE_DATA", "tuổi: " + (age != null ? age : 0));
                    if (age != null) {
                        filterBooksByAge(age);
                        filterBooksByCurrentMonth(popularBooks.getValue());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Bookmark>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void addBookMark(Bookmark BookMark) {
        BookMark.setUserId(userId.getValue());
        api.addBookmark(BookMark).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                fetchBookMarks(userId.getValue()); // Refresh BookMarks
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void deleteBookMark(String BookMarkId) {
        api.deleteBookmark(BookMarkId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                fetchBookMarks(userId.getValue()); // Refresh BookMarks
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }


    public LiveData<List<Book>> getPopularBooks() {
        return popularBooks;
    }

    public LiveData<List<Book>> getNewBooks() {
        return newBooks;
    }

    public void setUserAge(int age) {
        userAge.setValue(age);
    }

    public void setUserId(String id) {
        userId.setValue(id);
    }
}