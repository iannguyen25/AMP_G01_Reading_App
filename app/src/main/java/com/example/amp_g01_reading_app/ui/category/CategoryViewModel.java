package com.example.amp_g01_reading_app.ui.category;

import android.annotation.SuppressLint;
import android.util.Log;

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

public class CategoryViewModel extends ViewModel {
    private final MutableLiveData<List<Genre>> genresLiveData = new MutableLiveData<>();
    private final ApiService api;

    public CategoryViewModel() {
        api = ApiClient.getRetrofitInstance().create(ApiService.class);
        loadInitialData(); // Gọi hàm tải dữ liệu từ API
    }

    private void loadInitialData() {
        api.getGenres().enqueue(new Callback<List<Genre>>() {
            @Override
            public void onResponse(Call<List<Genre>> call, Response<List<Genre>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    genresLiveData.setValue(response.body()); // Cập nhật LiveData với danh sách genres
                }
            }

            @Override
            public void onFailure(Call<List<Genre>> call, Throwable t) {
                Log.e("API_ERROR", "Failed to load genres: " + t.getMessage());
            }
        });
    }

    public LiveData<List<Genre>> getGenres() {
        return genresLiveData; // Trả về LiveData chứa danh sách thể loại
    }
}
