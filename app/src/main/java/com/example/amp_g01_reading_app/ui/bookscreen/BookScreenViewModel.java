package com.example.amp_g01_reading_app.ui.bookscreen;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.amp_g01_reading_app.connect.ApiClient;
import com.example.amp_g01_reading_app.connect.ApiService;
import com.example.amp_g01_reading_app.ui.bookscreen.Book;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookScreenViewModel extends ViewModel {

    private MutableLiveData<Book> bookLiveData = new MutableLiveData<>();

    public LiveData<Book> getBook() {
        return bookLiveData;
    }

    // Phương thức để lấy dữ liệu chi tiết của một truyện theo id
    public void fetchBookDataById(String storyId) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        apiService.getStoryById(storyId).enqueue(new Callback<Book>() {
            @Override
            public void onResponse(Call<Book> call, Response<Book> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bookLiveData.postValue(response.body());
                } else {
                    // Xử lý khi không có dữ liệu trả về
                    bookLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<Book> call, Throwable t) {
                // Xử lý lỗi kết nối hoặc phản hồi không thành công
                t.printStackTrace();
                bookLiveData.postValue(null);
            }
        });
    }
}

