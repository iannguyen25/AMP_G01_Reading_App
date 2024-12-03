package com.example.amp_g01_reading_app.ui.bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.ui.bookscreen.BookScreenActivity;



public class BookmarkActivity extends AppCompatActivity {

    private BookmarkViewModel bookmarkViewModel;
    private BookmarkAdapter bookmarkAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        // Lấy userId từ Intent hoặc SharedPreferences
        String userId = getIntent().getStringExtra("USER_ID");
        if (userId == null) {
            userId = "1";
        }

        // Khởi tạo RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view_bookmarks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

// Khởi tạo adapter và set callback
        bookmarkAdapter = new BookmarkAdapter(this, bookmark -> {
            if (bookmark.getStoryId() != null) {
                Intent intent = new Intent(BookmarkActivity.this, BookScreenActivity.class);
                intent.putExtra("STORY_ID", bookmark.getStoryId());
                startActivity(intent);
            } else {
                Toast.makeText(this, "ID truyện không hợp lệ!", Toast.LENGTH_SHORT).show();
            }
        });

// Gắn adapter cho RecyclerView
        recyclerView.setAdapter(bookmarkAdapter);

        // Khởi tạo ViewModel
        bookmarkViewModel = new ViewModelProvider(this).get(BookmarkViewModel.class);

        // Quan sát LiveData để cập nhật danh sách bookmark
        bookmarkViewModel.getBookmarks().observe(this, bookmarks -> {
            if (bookmarks != null && !bookmarks.isEmpty()) {
                bookmarkAdapter.updateData(bookmarks);
            } else {
                Toast.makeText(this, "Danh sách bookmark trống!", Toast.LENGTH_SHORT).show();
            }
        });

        // Lấy danh sách bookmark
        bookmarkViewModel.fetchBookmarks(userId);
    }
}
