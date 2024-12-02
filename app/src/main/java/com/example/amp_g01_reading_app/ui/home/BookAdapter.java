package com.example.amp_g01_reading_app.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.databinding.ItemBookBinding;

public class BookAdapter extends ListAdapter<Book, BookAdapter.BookViewHolder> {

    private final OnBookClickListener listener; // Thêm callback

    public BookAdapter(OnBookClickListener listener) {
        super(new DiffUtil.ItemCallback<Book>() {
            @Override
            public boolean areItemsTheSame(@NonNull Book oldItem, @NonNull Book newItem) {
                return oldItem.getTitle().equals(newItem.getTitle());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Book oldItem, @NonNull Book newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.listener = listener; // Gán callback
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookBinding binding = ItemBookBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BookViewHolder(binding, listener); // Truyền listener vào ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    // Interface callback để xử lý sự kiện click
    public interface OnBookClickListener {
        void onBookClick(int position);

        void onAddToLibraryClick(int position);
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        private final ItemBookBinding binding;

        public BookViewHolder(ItemBookBinding binding, OnBookClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;

            // Lắng nghe sự kiện click vào item
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onBookClick(getBindingAdapterPosition());
                }
            });
            // Thêm sự kiện click cho ImageButton
            binding.addToLibraryButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    toggleAddToLibraryButtonState(binding.addToLibraryButton);
                    listener.onAddToLibraryClick(getBindingAdapterPosition());
                    //updateAddToLibraryButtonState(book, false);
//                    if (book != null) {
//                        if (libraryBooks.contains(book.getId())) {
//                            libraryBooks.remove(book.getId());
//                            updateAddToLibraryButtonState(book, false);
//                        } else {
//                            libraryBooks.add(book.getId());
//                            updateAddToLibraryButtonState(book, true);
//                        }
//                    }
                }
            });
        }

        private void toggleAddToLibraryButtonState(ImageButton button) {
            Context context = button.getContext();
            if (button.getBackground().getConstantState() == context.getDrawable(R.drawable.button_added_to_libraries_home).getConstantState()) {
                button.setBackgroundResource(R.drawable.button_add_libraires_home);
            } else {
                button.setBackgroundResource(R.drawable.button_added_to_libraries_home);
            }
        }


        public void bind(Book book) {
            binding.bookTitle.setText(book.getTitle());
            binding.bookPages.setText(book.getPages());
            Glide.with(binding.bookCover.getContext()).load(book.getCover_image()).placeholder(R.drawable.placeholder_image).error(R.drawable.error_image).into(binding.bookCover);

            Log.d("FRAGMENT_BOOKS_IS_BOOKMARK", "-------->:" + book.isBookMark());
            if (book.isBookMark()) {
                binding.addToLibraryButton.setBackgroundResource(R.drawable.button_added_to_libraries_home);
            } else {
                binding.addToLibraryButton.setBackgroundResource(R.drawable.button_add_libraires_home);
            }

        }
    }
}

