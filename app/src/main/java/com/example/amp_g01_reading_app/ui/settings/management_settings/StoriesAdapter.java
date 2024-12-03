package com.example.amp_g01_reading_app.ui.settings.management_settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.ui.home.Book;

import java.util.List;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.StoryViewHolder> {
    private List<Book> storyList;

    public StoriesAdapter(List<Book> storyList) {
        this.storyList = storyList;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Book book = storyList.get(position);

        // Set title
        holder.textViewTitle.setText(book.getTitle());

        // Set author (handle potential null)
        if (book.getAuthor() != null) {
            holder.textViewAuthor.setText(book.getAuthor());
        } else {
            holder.textViewAuthor.setText("Unknown Author");
        }

        if (book.getAge_range() != null) {
            holder.textViewAgeRange.setText(book.getAge_range());
        } else {
            holder.textViewAuthor.setText("Unknown Arange");
        }

        if (book.getCover_image() != null) {
            Glide.with(holder.imageViewCover.getContext())
                    .load(book.getCover_image())
                    .placeholder(R.drawable.button_background) // Ảnh placeholder trong khi tải
                    .error(R.drawable.ic_launcher_background) // Ảnh hiển thị nếu tải bị lỗi
                    .into(holder.imageViewCover);
        } else {
            // Nếu không có link, sử dụng ảnh local
            holder.imageViewCover.setImageResource(book.getCoverResourceId() != 0
                    ? book.getCoverResourceId()
                    : R.drawable.jungle_book);
        }
    }

    @Override
    public int getItemCount() {
        return storyList != null ? storyList.size() : 0;
    }

    static class StoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewCover;
        TextView textViewTitle;
        TextView textViewAuthor;
        TextView textViewAgeRange;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewCover = itemView.findViewById(R.id.imageViewCover);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewAuthor = itemView.findViewById(R.id.textViewAuthor);
            textViewAgeRange = itemView.findViewById(R.id.textViewAgeRange);
        }
    }

    // Method to update the list
    public void updateStories(List<Book> newStoryList) {
        this.storyList = newStoryList;
        notifyDataSetChanged();
    }
}