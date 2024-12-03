package com.example.amp_g01_reading_app.ui.comments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amp_g01_reading_app.R;

public class CommentsAdapter extends ListAdapter<Comment, CommentsAdapter.CommentViewHolder> {

    public CommentsAdapter() {
        super(new DiffUtil.ItemCallback<Comment>() {
            @Override
            public boolean areItemsTheSame(Comment oldItem, Comment newItem) {
                return oldItem.getUserId().equals(newItem.getUserId()); // Sử dụng user_id làm duy nhất
            }

            @Override
            public boolean areContentsTheSame(Comment oldItem, Comment newItem) {
                return oldItem.getComment().equals(newItem.getComment());
            }
        });
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        Comment comment = getItem(position);

        holder.commentTextView.setText(comment.getComment());
        holder.commentUser.setText(comment.getEmail());
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView  commentUser,commentTextView;

        public CommentViewHolder(View itemView) {
            super(itemView);
            commentTextView = itemView.findViewById(R.id.comment_content);
            commentUser = itemView.findViewById(R.id.comment_user_name);
        }
    }
}
