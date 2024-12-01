package com.example.amp_g01_reading_app.ui.category;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.amp_g01_reading_app.R;

import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {
    private final List<Genre> genres;
    private final OnGenreClickListener listener;

    public GenreAdapter(List<Genre> genres, OnGenreClickListener listener) {
        this.genres = genres;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_genre, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        Genre genre = genres.get(position);
        holder.name.setText(genre.getName());
        Glide.with(holder.icon.getContext())
                .load(genre.getImageUrl())
                .placeholder(R.drawable.background_settings)
                .into(holder.icon);

        holder.itemView.setOnClickListener(v -> listener.onGenreClick(genre));
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public static class GenreViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.genre_icon);
            name = itemView.findViewById(R.id.genre_name);
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    public void updateGenres(List<Genre> newGenres) {
        genres.clear();
        genres.addAll(newGenres);
        notifyDataSetChanged();
    }

}

