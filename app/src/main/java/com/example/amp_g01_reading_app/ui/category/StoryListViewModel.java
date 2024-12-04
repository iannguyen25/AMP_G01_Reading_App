package com.example.amp_g01_reading_app.ui.category;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.amp_g01_reading_app.connect.ApiClient;
import com.example.amp_g01_reading_app.connect.ApiService;
import com.example.amp_g01_reading_app.ui.home.Book;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoryListViewModel extends ViewModel {
    private final MutableLiveData<List<Book>> storiesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> userAge = new MutableLiveData<>();
    private final ApiService api;
    private List<Book> allStories = new ArrayList<>(); // Dữ liệu gốc từ API
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);

    public StoryListViewModel() {
        api = ApiClient.getRetrofitInstance().create(ApiService.class);
    }

    public LiveData<Integer> getUserAge() {
        return userAge;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadStoriesByGenre(String genreId) {
        api.getStoriesByGenre(genreId).enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(@NonNull Call<List<Book>> call, @NonNull Response<List<Book>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allStories = response.body(); // Lưu dữ liệu gốc
                    filterStoriesIfAgeSet();
//                    storiesLiveData.setValue(allStories); // Hiển thị toàn bộ ban đầu
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Book>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Failed to load stories: " + t.getMessage());
            }
        });
    }

//    public void filterStoriesByAge(int age) {
//        List<Book> filteredStories = new ArrayList<>();
//        List<Book> allStories = storiesLiveData.getValue();
//        if (allStories != null) {
//            for (Book book : allStories) {
//                if (isAgeInGroup(age, book.getAge_group())) {
//                    filteredStories.add(book);
//                }
//            }
//        }
//        storiesLiveData.setValue(filteredStories); // Cập nhật danh sách truyện đã lọc
//    }

    private void filterStoriesIfAgeSet() {
        Integer age = userAge.getValue();
        if (age != null && !allStories.isEmpty()) {
            List<Book> filteredStories = new ArrayList<>();
            for (Book book : allStories) {
                if (isAgeInGroup(age, book.getAge_group())) {
                    filteredStories.add(book);
                }
            }
            storiesLiveData.setValue(filteredStories);
            isLoading.setValue(false);
        }
    }

    public LiveData<List<Book>> getStories() {
        return storiesLiveData;
    }

    public void setUserAge(Integer age) {
        userAge.setValue(age);
        filterStoriesIfAgeSet();
    }

    private boolean isAgeInGroup(int userAge, String ageGroup) {
        if (ageGroup == null || ageGroup.isEmpty()) return false;

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
}
