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

import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.databinding.FragmentCategoryBinding;

import java.util.ArrayList;

public class CategoryFragment extends Fragment {
    private FragmentCategoryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        GenreAdapter genreAdapter = new GenreAdapter(new ArrayList<>(), genre -> {
            Bundle bundle = new Bundle();
            bundle.putString("genre_id", genre.getId());

            StoryListFragment storyListFragment = new StoryListFragment();
            storyListFragment.setArguments(bundle);

            binding.recyclerView.setVisibility(View.GONE);
//            binding.categoryFragment.setVisibility(View.GONE);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.category_fragment, storyListFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(genreAdapter);

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

