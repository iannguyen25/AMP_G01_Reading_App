package com.example.amp_g01_reading_app.ui.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;



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
    private MutableLiveData<Integer> userAge = new MutableLiveData<>();
    private final ApiService api;
    private List<Book> allBooks ;

    public HomeViewModel() {
        popularBooks = new MutableLiveData<>();
        newBooks = new MutableLiveData<>();
        api =  ApiClient.getRetrofitInstance().create(ApiService.class);
        allBooks = new ArrayList<>();
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
        //popularBooks.setValue(popular);
        api.getStories().enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(Call<List<Book>> call, Response<List<Book>> response) {
                Log.d("API_RESPONSE", "Response code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    allBooks = response.body();
                    Integer age = userAge.getValue();
                    Log.d("AGE_DATA", "tuổi: " + (age != null ? age : 0));
                    Log.d("BOOKS_DATA", "Số lượng sách: " + (allBooks != null ? allBooks.size() : 0));
                    for (Book a: allBooks) {
                        Log.d("BOOKS_DATA_IMAGE", "URL: " + (a != null ? a.getCover_image() : "empty"));
                        Log.d("BOOKS_DATA_AGE_RANGE", "RANGE: " + (a != null ? a.getAge_range() : "empty"));
                        long seconds = a.getPublished_date().get_seconds();
                        Instant instant = Instant.ofEpochSecond(seconds);
                        LocalDateTime publishedDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                        Log.d("BOOKS_DATA_PUBLISHED", "DATE: " + publishedDateTime);
                    }
                    if (age != null) {
                        filterBooksByAge(age);
                        filterBooksByCurrentMonth(popularBooks.getValue());
                    }

                }
            }



            @Override
            public void onFailure(Call<List<Book>> call, Throwable t) {
                Log.d("API_RESPONSE", "API call failed. Response code: " );
                // Handle API call failure
            }
        });

        //List<Book> newBooksList = new ArrayList<>();
//        newBooksList.add(new Book("World Book", "3 Pages", R.drawable.jungle_book));
//        newBooksList.add(new Book("Help Book", "5 Pages", R.drawable.jungle_book));

        //newBooks.setValue(newBooksList);
    }






    private void filterBooksByAge(int age){
        if (allBooks ==null || allBooks.isEmpty()){
            popularBooks.setValue(new ArrayList<>());
            return;
        }

        List<Book> filteredBooks = new ArrayList<>();
        for (Book book : allBooks){
            if (isAgeInGroup(age, book.getAge_range())){
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
                LocalDate publishedDate = Instant.ofEpochSecond(book.getPublished_date().get_seconds())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

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

    //Check age
    private boolean isAgeInGroup(int userAge, String ageGroup){
       if (ageGroup.contains("-")){
           String[] ageRange = ageGroup.split("-");
           if (ageRange.length==2){
               int minAge = Integer.parseInt(ageRange[0].trim());
               int maxAge = Integer.parseInt(ageRange[1].trim());
               return userAge >= minAge && userAge <=maxAge;
           }
       }else{
           int singleAge = Integer.parseInt(ageGroup.trim());
           return userAge == singleAge;
       }
        return false;
    }

        public LiveData<List<Book>> getPopularBooks () {
            return popularBooks;
        }

        public LiveData<List<Book>> getNewBooks () {
            return newBooks;
        }

    public void setUserAge(int age) {
        userAge.setValue(age);
    }
}