package com.example.amp_g01_reading_app.ui.bookmark;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.ui.bookmark.Bookmark;

import java.util.ArrayList;
import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {

    public interface OnBookmarkClickListener {
        void onBookmarkClick(Bookmark bookmark);
    }

    private final List<Bookmark> bookmarkList = new ArrayList<>();
    private final Context context;
    private final OnBookmarkClickListener listener;

    public BookmarkAdapter(Context context, OnBookmarkClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void updateData(List<Bookmark> newBookmarks) {
        bookmarkList.clear();
        bookmarkList.addAll(newBookmarks);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_story, parent, false);
        return new BookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookmarkViewHolder holder, int position) {
        Bookmark bookmark = bookmarkList.get(position);
        holder.bind(bookmark, listener);
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    static class BookmarkViewHolder extends RecyclerView.ViewHolder {
        private final ImageView coverImageView;
        private final TextView titleTextView, authorTextView, ageRangeTextView;

        public BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImageView = itemView.findViewById(R.id.imageViewCover);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            authorTextView = itemView.findViewById(R.id.textViewAuthor);
            ageRangeTextView = itemView.findViewById(R.id.textViewAgeRange);
        }

        public void bind(Bookmark bookmark, OnBookmarkClickListener listener) {
            titleTextView.setText(bookmark.getTitle());
            authorTextView.setText(bookmark.getAuthor());
            ageRangeTextView.setText(bookmark.getAgeRange());

            Glide.with(itemView.getContext())
                    .load(bookmark.getCoverImage())
                    .into(coverImageView);

            itemView.setOnClickListener(v -> listener.onBookmarkClick(bookmark));
        }
    }
}
