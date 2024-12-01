package com.example.amp_g01_reading_app.ui.category;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.amp_g01_reading_app.connect.ApiClient;
import com.example.amp_g01_reading_app.connect.ApiService;
import com.example.amp_g01_reading_app.ui.home.Book;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoryListViewModel extends ViewModel {
    private final MutableLiveData<List<Book>> storiesLiveData = new MutableLiveData<>();
    private final ApiService api;

    public StoryListViewModel() {
        api = ApiClient.getRetrofitInstance().create(ApiService.class);
    }

    public void loadStoriesByGenre(String genreId) {
        api.getStoriesByGenre(genreId).enqueue(new Callback<List<Book>>() {
            @Override
            public void onResponse(@NonNull Call<List<Book>> call, @NonNull Response<List<Book>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    storiesLiveData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Book>> call, @NonNull Throwable t) {
                Log.e("API_ERROR", "Failed to load stories: " + t.getMessage());
            }
        });
    }

    public LiveData<List<Book>> getStories() {
        return storiesLiveData;
    }
}
