package com.example.amp_g01_reading_app.ui.bookscreen;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.amp_g01_reading_app.R;

public class BookDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        // Tham chiếu các View
        TextView titleTextView = findViewById(R.id.book_title);
        TextView authorTextView = findViewById(R.id.book_author);
        TextView ageGroupTextView = findViewById(R.id.book_age_group);
        TextView publishedDateTextView = findViewById(R.id.book_published_date);

        // Lấy dữ liệu từ Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String title = extras.getString("title");
            String author = extras.getString("author");
            String ageGroup = extras.getString("ageGroup");
            String publishedDate = extras.getString("publishedDate");

            // Hiển thị dữ liệu lên giao diện
            titleTextView.setText("Tên truyện: " + title);
            authorTextView.setText("Tác giả: " + author);
            ageGroupTextView.setText("Độ tuổi: " + ageGroup);
            publishedDateTextView.setText("Ngày đăng: " + publishedDate);
        }
    }
}
