package com.example.amp_g01_reading_app.ui.category;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.amp_g01_reading_app.databinding.FragmentCategoryBinding;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding binding;
    private GenreAdapter genreAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Cài đặt RecyclerView
        genreAdapter = new GenreAdapter(new ArrayList<>());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(genreAdapter);

        // Quan sát dữ liệu từ ViewModel
        CategoryViewModel viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        viewModel.getGenres().observe(getViewLifecycleOwner(), genres -> {
            if (genres != null) {
                genreAdapter.updateGenres(genres);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
