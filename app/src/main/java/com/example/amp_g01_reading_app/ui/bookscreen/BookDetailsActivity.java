package com.example.amp_g01_reading_app.ui.bookscreen;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.amp_g01_reading_app.R;

public class BookDetailsActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        // Tham chiếu các View
        TextView titleTextView = findViewById(R.id.book_title);
        TextView authorTextView = findViewById(R.id.book_author);
        TextView ageGroupTextView = findViewById(R.id.book_age_group);

        // Lấy dữ liệu từ Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String title = extras.getString("title");
            String author = extras.getString("author");
            String ageGroup = extras.getString("ageGroup");


            // Hiển thị dữ liệu lên giao diện
            titleTextView.setText("Tên truyện: " + title);
            authorTextView.setText("Tác giả: " + author);
            ageGroupTextView.setText("Độ tuổi: " + ageGroup);

        }
    }
}
