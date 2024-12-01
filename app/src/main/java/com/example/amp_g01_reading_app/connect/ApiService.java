package com.example.amp_g01_reading_app.connect;

import com.example.amp_g01_reading_app.ui.category.Genre;
import com.example.amp_g01_reading_app.ui.home.Book;
import com.example.amp_g01_reading_app.ui.comments.Comment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    // Endpoint để lấy danh sách truyện
    @GET("Stories")
    Call<List<Book>> getStories();

    // Endpoint để lấy thông tin chi tiết của một truyện
    @GET("Stories/{id}")
    Call<Book> getStoryById(@Path("id") String storyId);
    //
    @GET("comments/{storyId}")
    Call<List<Comment>> getComments(@Path("storyId") String storyId);

    // Gửi bình luận mới
    @POST("comments")
    Call<Void> postComment(@Body Comment comment);
    //  thêm các endpoint khác tương ứng với web service

    @GET("genres")
    Call<List<Genre>> getGenres();

    @GET("Stories/genre/{genre_id}")
    Call<List<Book>> getStoriesByGenre(@Path("genre_id") String genre_id);
}
