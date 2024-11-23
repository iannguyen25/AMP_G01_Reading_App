package com.example.amp_g01_reading_app.connect;

import com.example.amp_g01_reading_app.ui.bookscreen.Book;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
    // Endpoint để lấy danh sách truyện
    @GET("Stories")
    Call<List<Book>> getStories();

    // Endpoint để lấy thông tin chi tiết của một truyện
    @GET("Stories/{id}")
    Call<Book> getStoryById(@Path("id") String storyId);

    //  thêm các endpoint khác tương ứng với web service
}
