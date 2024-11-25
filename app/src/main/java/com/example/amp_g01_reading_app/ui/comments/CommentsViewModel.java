package com.example.amp_g01_reading_app.ui.comments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.amp_g01_reading_app.connect.ApiClient;
import com.example.amp_g01_reading_app.connect.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentsViewModel extends ViewModel {

    private MutableLiveData<List<Comment>> commentsLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> postCommentResult = new MutableLiveData<>();

    // Lấy danh sách bình luận
    public LiveData<List<Comment>> getComments() {
        return commentsLiveData;
    }

    // Kết quả khi thêm bình luận
    public LiveData<Boolean> getPostCommentResult() {
        return postCommentResult;
    }

    // Phương thức để lấy bình luận của truyện
    public void fetchComments(String storyId) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        apiService.getComments(storyId).enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    commentsLiveData.postValue(response.body());
                } else {
                    commentsLiveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                t.printStackTrace();
                commentsLiveData.postValue(null);
            }
        });
    }

    // Phương thức để gửi bình luận
    public void postComment(Comment comment) {
        ApiService apiService = ApiClient.getRetrofitInstance().create(ApiService.class);
        apiService.postComment(comment).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                postCommentResult.postValue(response.isSuccessful());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
                postCommentResult.postValue(false);
            }
        });
    }
}
