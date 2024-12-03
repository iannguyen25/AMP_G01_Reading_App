package com.example.amp_g01_reading_app.ui.bookmark;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.amp_g01_reading_app.connect.ApiClient;
import com.example.amp_g01_reading_app.connect.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookmarkViewModel extends ViewModel {

    private final MutableLiveData<List<Bookmark>> bookmarksLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<Bookmark>> getBookmarks() {
        return bookmarksLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void fetchBookmarks(String userId) {
        isLoading.setValue(true);
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        apiService.getBookmarksByUserId(userId).enqueue(new Callback<List<Bookmark>>() {
            @Override
            public void onResponse(Call<List<Bookmark>> call, Response<List<Bookmark>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    bookmarksLiveData.postValue(response.body());
                } else {
                    errorMessage.setValue("Không thể tải danh sách yêu thích!");
                }
            }

            @Override
            public void onFailure(Call<List<Bookmark>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void addBookmark(String storyId,String userId, String title, String author, String ageRange, String coverImage) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);

        // Tạo đối tượng Bookmark để gửi lên server
        Bookmark bookmark = new Bookmark();
        bookmark.setUserId(userId);
        bookmark.setStoryId(storyId);
        bookmark.setTitle(title);
        bookmark.setAuthor(author);
        bookmark.setAgeRange(ageRange);
        bookmark.setCoverImage(coverImage);

        apiService.addBookmark(bookmark).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Gọi lại fetchBookmarks để cập nhật danh sách
                    fetchBookmarks(userId);
                    errorMessage.setValue("Thêm vào danh sách yêu thích thành công!");
                } else {
                    errorMessage.setValue("Không thể thêm vào danh sách yêu thích!");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                errorMessage.setValue("Lỗi kết nối khi thêm bookmark: " + t.getMessage());
            }
        });
    }

}
