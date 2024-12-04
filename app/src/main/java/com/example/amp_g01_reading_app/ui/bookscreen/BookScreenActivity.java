package com.example.amp_g01_reading_app.ui.bookscreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.amp_g01_reading_app.MainActivity;
import com.example.amp_g01_reading_app.ui.bookmark.BookmarkActivity;
import com.example.amp_g01_reading_app.ui.bookmark.BookmarkViewModel;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


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
    private Button fontSettingsButton;
    private SeekBar pageSlider;
    private ImageButton menuButton,bookmarkButton,buttonBack;
    private BookmarkViewModel bookmarkViewModel;
    private FirebaseAuth mAuth;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_screen);

        //getId
        String storyId = getIntent().getStringExtra("storyId");

        //ánh xạ
        coverImageView = findViewById(R.id.image_book);
        titleTextView = findViewById(R.id.title_book);
        contentTextView = findViewById(R.id.content_text);
        fontSettingsButton = findViewById(R.id.font_settings);
        pageSlider = findViewById(R.id.page_slider);
        pageNumberLabel = findViewById(R.id.page_number_label);
        menuButton = findViewById(R.id.menu_button);
        buttonBack = findViewById(R.id.back_button);
        bookmarkButton = findViewById(R.id.bookmark_button);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String user_id = user.getUid().toString();
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
        //BackButton handled
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Xử lí sự kiện cho menu button
        menuButton.setOnClickListener(v -> {
            // Tạo PopupMenu
            PopupMenu popupMenu = new PopupMenu(this, menuButton);
            popupMenu.getMenuInflater().inflate(R.menu.book_menu, popupMenu.getMenu());

            // Lắng nghe sự kiện chọn mục trong menu
            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.menu_details) {
                    Intent detailsIntent = new Intent(this, BookDetailsActivity.class);
                    Book book = viewModel.getBook().getValue(); // Lấy dữ liệu từ ViewModel
                    //  Book.PublishedDate publishedDate = book.getPublishedDate();
                    if (book != null) {
                        detailsIntent.putExtra("title", book.getTitle());
                        detailsIntent.putExtra("author", book.getAuthor());
                        detailsIntent.putExtra("ageGroup", book.getAge_group());
                    }

                    Toast.makeText(this, "Hiển thị chi tiết truyện", Toast.LENGTH_SHORT).show();
                    //  Chuyển sang màn hình chi tiết sách
                    startActivity(detailsIntent);
                    return true;
                } else if (id == R.id.menu_comments) {
                    Intent intent = new Intent(BookScreenActivity.this, CommentsActivity.class);
                    intent.putExtra("STORY_ID", storyId);
                    intent.putExtra("USER_ID",user_id);
                    startActivity(intent);
                    return true;
                } if (id == R.id.menu_favorites) {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("SHOW_FRAGMENT", "BookmarkFragment");
                    intent.putExtra("USER_ID", user_id);
                    startActivity(intent);
                    return true;
                }

                return false;
            });

            // Hiển thị PopupMenu
            popupMenu.show();
        });
        //Xử lí sự kiện cho bookmarkButton
        bookmarkViewModel = new ViewModelProvider(this).get(BookmarkViewModel.class);
        bookmarkButton.setOnClickListener(v -> {
            // Gọi phương thức thêm bookmark trong ViewModel
            bookmarkViewModel.addBookmark(viewModel.getBook().getValue().getId(),user_id,viewModel.getBook().getValue().getTitle(),
                    viewModel.getBook().getValue().getAuthor(),viewModel.getBook().getValue().getAge_group(),
                    viewModel.getBook().getValue().getCover_image());

            Intent intent = new Intent(BookScreenActivity.this, MainActivity.class);
            intent.putExtra("SHOW_FRAGMENT", "BookmarkFragment");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

            // Lắng nghe kết quả từ ViewModel
            bookmarkViewModel.getErrorMessage().observe(this, message -> {
                if (message != null) {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Lấy dữ liệu chi tiết truyện theo ID
        viewModel.fetchBookDataById(storyId);

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



}