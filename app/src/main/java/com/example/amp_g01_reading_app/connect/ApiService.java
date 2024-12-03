package com.example.amp_g01_reading_app.connect;

import com.example.amp_g01_reading_app.ui.bookmark.Bookmark;
import com.example.amp_g01_reading_app.ui.category.Genre;
import com.example.amp_g01_reading_app.ui.home.Book;
import com.example.amp_g01_reading_app.ui.comments.Comment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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

    // Lấy danh sách bình luận cho một truyện
    @GET("Comments/{storyId}")
    Call<List<Comment>> getComments(@Path("storyId") String storyId);

    @POST("Comments")
    Call<Void> addComment(@Body Comment comment);

    // Gửi bình luận mới
    @POST("Bookmarks")
    Call<Void> addBookmark(@Body Bookmark bookmark);

    @GET("bookmarks/{userId}")
    Call<List<Bookmark>> getBookmarksByUserId(@Path("userId") String userId);

    @DELETE("bookmarks/{id}")
    Call<Void> deleteBookmark(@Path("id") String bookmarkId);

    @GET("genres")
    Call<List<Genre>> getGenres();

    @GET("Stories/genre/{genre_id}")
    Call<List<Book>> getStoriesByGenre(@Path("genre_id") String genre_id);
}
