package com.example.amp_g01_reading_app.ui.bookmark;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.ui.bookscreen.BookScreenActivity;

public class BookmarkFragment extends Fragment {

    private BookmarkViewModel bookmarkViewModel;
    private BookmarkAdapter bookmarkAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmark, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Lấy userId từ SharedPreferences (hoặc truyền qua Bundle nếu cần)
        String userId = getArguments() != null ? getArguments().getString("USER_ID", "1") : "xq8NXz7ZK1culDFqvkWgCDuhD5u1";

        // Khởi tạo RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_bookmarks);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Khởi tạo adapter và set callback
        bookmarkAdapter = new BookmarkAdapter(requireContext(), bookmark -> {
            if (bookmark.getStoryId() != null) {
                Intent intent = new Intent(requireContext(), BookScreenActivity.class);
                intent.putExtra("STORY_ID", bookmark.getStoryId());
                startActivity(intent);
            } else {
                Toast.makeText(requireContext(), "ID truyện không hợp lệ!", Toast.LENGTH_SHORT).show();
            }
        });

        // Gắn adapter cho RecyclerView
        recyclerView.setAdapter(bookmarkAdapter);

        // Khởi tạo ViewModel
        bookmarkViewModel = new ViewModelProvider(this).get(BookmarkViewModel.class);

        // Quan sát LiveData để cập nhật danh sách bookmark
        bookmarkViewModel.getBookmarks().observe(getViewLifecycleOwner(), bookmarks -> {
            if (bookmarks != null && !bookmarks.isEmpty()) {
                bookmarkAdapter.updateData(bookmarks);
            } else {
                Toast.makeText(requireContext(), "Danh sách bookmark trống!", Toast.LENGTH_SHORT).show();
            }
        });

        // Lấy danh sách bookmark
        bookmarkViewModel.fetchBookmarks(userId);
    }
}
