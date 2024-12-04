package com.example.amp_g01_reading_app.ui.category;

import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.amp_g01_reading_app.databinding.FragmentStoryListBinding;
import com.example.amp_g01_reading_app.ui.bookscreen.BookScreenActivity;

import java.util.ArrayList;

public class StoryListFragment extends Fragment {
    private FragmentStoryListBinding binding;
    private StoryAdapter storyAdapter;
    private StoryListViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStoryListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        storyAdapter = new StoryAdapter(new ArrayList<>());
        storyAdapter.setOnItemClickListener(this::navigateToBookScreen);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(storyAdapter);

        viewModel = new ViewModelProvider(this).get(StoryListViewModel.class);

        if (getArguments() != null) {
            String genreId = getArguments().getString("genre_id");
            if (genreId != null) {
                viewModel.loadStoriesByGenre(genreId);
            } else {
                Toast.makeText(getContext(), "No genre selected", Toast.LENGTH_SHORT).show();
            }
        }

        viewModel.getStories().observe(getViewLifecycleOwner(), stories -> {
            if (stories != null) {
                storyAdapter.updateStories(stories);
            } else {
                Toast.makeText(getContext(), "No stories found", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void navigateToBookScreen(String bookId) {
        Intent intent = new Intent(getContext(), BookScreenActivity.class);
        intent.putExtra("storyId", bookId);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}