
package com.example.amp_g01_reading_app.ui.bookscreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.amp_g01_reading_app.ui.home.Book;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.ui.comments.CommentsActivity;
import com.example.amp_g01_reading_app.ui.home.PublishedDate;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookScreenActivity extends AppCompatActivity {
    //biến
    private TextView contentTextView,titleTextView,pageNumberLabel;
    private int totalPages;
    private List<String> pages;
    private ImageView coverImageView;
    private  Button fontSettingsButton;
    private SeekBar pageSlider;
    private ImageButton menuButton;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_screen);

        //ánh xạ
        coverImageView = findViewById(R.id.image_book);
        titleTextView = findViewById(R.id.title_book);
        contentTextView = findViewById(R.id.content_text);
        fontSettingsButton = findViewById(R.id.font_settings);
        pageSlider = findViewById(R.id.page_slider);
        pageNumberLabel = findViewById(R.id.page_number_label);
        menuButton = findViewById(R.id.menu_button);
        // LiveData cập nhật giao diện khi dữ liệu thay đổi
        BookScreenViewModel viewModel = new ViewModelProvider(this).get(BookScreenViewModel.class);
        viewModel.getBook().observe(this, book -> {
            if (book != null) {
                titleTextView.setText(book.getTitle());
                Glide.with(this)
                        .load(book.getCover_image())
                        .placeholder(R.drawable.placeholder_image) // Hình ảnh tạm thời khi tải
                        .error(R.drawable.error_image) // Hình ảnh hiển thị khi lỗi tải
                        .into(coverImageView);
                // Chia nội dung thành các trang
                int charactersPerPage = 299; // Giả định số ký tự mỗi trang
                pages = splitContentIntoPages(book.getContent(), charactersPerPage);
                totalPages = pages.size();

                // Cập nhật max của SeekBar và hiển thị trang đầu tiên
                pageSlider.setMax(totalPages - 1);
                updateContent(0);
            } else {
                titleTextView.setText("Đã có lỗi xảy ra");
                contentTextView.setText("Failed to load story data.");
            }
        });
        // Xử lí  sự kiện thay đổi của SeekBar
        pageSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Cập nhật nội dung và số trang hiện tại
                updateContent(progress);
                pageNumberLabel.setText("Page: " + (progress + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        // Xử lí sự kiện cho font button
        fontSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsPanelFragment settingsFragment = new SettingsPanelFragment();
                settingsFragment.show(getSupportFragmentManager(), "SettingsPanelFragment");
            }
        });
        // Xử lí sự kiện cho menu button
        menuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, menuButton);
            popupMenu.getMenuInflater().inflate(R.menu.book_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.menu_details) {
                    Intent detailsIntent = new Intent(this, BookDetailsActivity.class);
                    Book book = viewModel.getBook().getValue();
                    if (book != null) {
                        detailsIntent.putExtra("title", book.getTitle());
                        detailsIntent.putExtra("author", book.getAuthor_id());
                        detailsIntent.putExtra("ageGroup", book.getAge_range());

                        Book.PublishedDate publishedDate = book.getPublished_date();
                        if (publishedDate != null) {
                            // Chuyển _seconds thành mili giây
                            long milliseconds = publishedDate.getSeconds() * 1000;

                            Date date = new Date(milliseconds);

                            // Định dạng ngày thành "dd-MM-yyyy"
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            String formattedDate = dateFormat.format(date);

                            detailsIntent.putExtra("publishedDate", formattedDate);
                        }
                    }

                    startActivity(detailsIntent);
                    return true;
                } else if (id == R.id.menu_comments) {
                    showComments();
                    return true;
                } else if (id == R.id.menu_favorites) {
                    showFavorites();
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });


        // Lấy dữ liệu chi tiết truyện theo ID
        viewModel.fetchBookDataById("I0l5CXoUuPf4EiwO7Non");

    }

    // Phương thức để chia nội dung thành các trang
    private List<String> splitContentIntoPages(String content, int charactersPerPage) {
        List<String> pages = new ArrayList<>();
        int start = 0;
        while (start < content.length()) {
            int end = Math.min(start + charactersPerPage, content.length());
            pages.add(content.substring(start, end));
            start = end;
        }
        return pages;
    }
    // Phương thức để cập nhật nội dung TextView
    private void updateContent(int pageIndex) {
        if (pageIndex >= 0 && pageIndex < pages.size()) {
            contentTextView.setText(pages.get(pageIndex));
        }
    }


    private void showComments() {
        String storyId = "I0l5CXoUuPf4EiwO7Non"; // ID của truyện
        Intent intent = new Intent(BookScreenActivity.this, CommentsActivity.class);
        intent.putExtra("STORY_ID", storyId); // Truyền ID của truyện cho CommentsActivity
        startActivity(intent);
    }


    private void showFavorites() {
        // Xử lý danh sách yêu thích
        Toast.makeText(this, "Hiển thị danh sách yêu thích", Toast.LENGTH_SHORT).show();
        // Ví dụ: Chuyển sang màn hình danh sách yêu thích
//        Intent intent = new Intent(this, FavoritesActivity.class);
    }
}

