package com.example.amp_g01_reading_app.ui.category;

import androidx.lifecycle.ViewModelProvider;
import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.databinding.FragmentStoryListBinding;

import java.util.ArrayList;

public class StoryListFragment extends Fragment {
    private FragmentStoryListBinding binding;
    private StoryAdapter storyAdapter;
    private StoryListViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Use view binding
        binding = FragmentStoryListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize adapter
        storyAdapter = new StoryAdapter(new ArrayList<>());

        // Set up RecyclerView
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(storyAdapter);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(StoryListViewModel.class);

        // Load stories by genre
        if (getArguments() != null) {
            String genreId = getArguments().getString("genre_id");
            if (genreId != null) {
                viewModel.loadStoriesByGenre(genreId);
            } else {
                Toast.makeText(getContext(), "No genre selected", Toast.LENGTH_SHORT).show();
            }
        }

        // Observe stories
        viewModel.getStories().observe(getViewLifecycleOwner(), stories -> {
            if (stories != null) {
                storyAdapter.updateStories(stories);
            } else {
                Toast.makeText(getContext(), "No stories found", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear the binding to avoid memory leaks
        binding = null;
    }
}