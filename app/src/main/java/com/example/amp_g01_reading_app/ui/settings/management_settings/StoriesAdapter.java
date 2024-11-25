package com.example.amp_g01_reading_app.ui.settings.management_settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.ui.home.Book;

import java.util.List;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.StoryViewHolder> {
    private List<Book> stories;
    private OnStoryClickListener listener;

    public StoriesAdapter(List<Book> stories) {
        this.stories = stories;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_story, parent, false);
        return new StoryViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Book story = stories.get(position);
        holder.bind(story);
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public void setStories(List<Book> stories) {
        this.stories = stories;
        notifyDataSetChanged();
    }

    public void setOnStoryClickListener(OnStoryClickListener listener) {
        this.listener = listener;
    }

    class StoryViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView authorTextView;
        private ImageView coverImageView;

        StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            authorTextView = itemView.findViewById(R.id.textViewAuthor);
            coverImageView = itemView.findViewById(R.id.imageViewCover);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onStoryClick(stories.get(position));
                }
            });
        }

        void bind(Book story) {
            titleTextView.setText(story.getTitle());
            authorTextView.setText(story.getAuthor_id());
            // Sử dụng thư viện tải ảnh như Glide hoặc Picasso để tải ảnh bìa
            // Ví dụ với Glide:
            // Glide.with(itemView.getContext()).load(story.getCoverUrl()).into(coverImageView);
        }
    }

    public interface OnStoryClickListener {
        void onStoryClick(Book story);
    }
}