package com.example.amp_g01_reading_app.ui.comments;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amp_g01_reading_app.R;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentsAdapter commentAdapter;
    private EditText editTextComment;
    private Button buttonPostComment;
    private CommentsViewModel commentsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        // Lấy ID truyện từ Intent (có thể truyền từ BookScreenActivity)
        String storyId = getIntent().getStringExtra("STORY_ID");

        recyclerView = findViewById(R.id.recycler_view_comments);
        editTextComment = findViewById(R.id.edit_text_comment);
        buttonPostComment = findViewById(R.id.button_post_comment);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentsAdapter();
        recyclerView.setAdapter(commentAdapter);

        // Khởi tạo ViewModel
        commentsViewModel = new ViewModelProvider(this).get(CommentsViewModel.class);

        // Quan sát LiveData để cập nhật danh sách bình luận
        commentsViewModel.getComments().observe(this, comments -> {
            if (comments != null) {
                commentAdapter.submitList(comments);
            }
        });

        // Lấy danh sách bình luận khi Activity được tạo
        if (storyId != null) {
            commentsViewModel.fetchComments(storyId);
        }

        // Xử lý sự kiện khi nhấn nút gửi bình luận
        buttonPostComment.setOnClickListener(v -> {
            String commentText = editTextComment.getText().toString();
            if (TextUtils.isEmpty(commentText)) {
                Toast.makeText(CommentsActivity.this, "Vui lòng nhập bình luận", Toast.LENGTH_SHORT).show();
            } else {
                // Tạo đối tượng Comment và gửi tới API
                Comment comment = new Comment(storyId, "user456", commentText); // user456 là ID giả
                commentsViewModel.postComment(comment);

                // Xử lý kết quả gửi bình luận
                commentsViewModel.getPostCommentResult().observe(this, success -> {
                    if (success) {
                        Toast.makeText(CommentsActivity.this, "Bình luận đã được gửi", Toast.LENGTH_SHORT).show();
                        editTextComment.setText(""); // Xóa nội dung trong EditText
                        commentsViewModel.fetchComments(storyId); // Cập nhật danh sách bình luận
                    } else {
                        Toast.makeText(CommentsActivity.this, "Có lỗi xảy ra, không thể gửi bình luận", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
