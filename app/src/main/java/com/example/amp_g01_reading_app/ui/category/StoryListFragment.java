package com.example.amp_g01_reading_app.ui.category;

import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.databinding.FragmentStoryListBinding;
import com.example.amp_g01_reading_app.ui.bookscreen.BookScreenActivity;
import com.example.amp_g01_reading_app.ui.home.HomeViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class StoryListFragment extends Fragment {
    private FragmentStoryListBinding binding;
    private StoryAdapter storyAdapter;
    private StoryListViewModel viewModel;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStoryListBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadUserData();

        // Thiết lập adapter cho RecyclerView
        storyAdapter = new StoryAdapter(new ArrayList<>());
        storyAdapter.setOnItemClickListener(this::navigateToBookScreen);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.recyclerView.setAdapter(storyAdapter);

        binding.backButton.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.global_action_to_list_category);
        });

        // Lấy ViewModel
        viewModel = new ViewModelProvider(this).get(StoryListViewModel.class);

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.recyclerView.setVisibility(View.GONE);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
            }
        });

        // Quan sát dữ liệu stories
        viewModel.getStories().observe(getViewLifecycleOwner(), stories -> {
            if (stories != null && !stories.isEmpty()) {
                storyAdapter.updateStories(stories);
            } else {
                Toast.makeText(getContext(), "No stories found for your age group", Toast.LENGTH_SHORT).show();
            }
        });

        // Lấy dữ liệu genreId từ arguments
        if (getArguments() != null) {
            String genreId = getArguments().getString("genre_id");
            if (genreId != null) {
                viewModel.loadStoriesByGenre(genreId); // Gọi API tải dữ liệu stories
            } else {
                Toast.makeText(getContext(), "No genre selected", Toast.LENGTH_SHORT).show();
            }
        }

        return view;
    }


    private void navigateToBookScreen(String bookId) {
        Intent intent = new Intent(getContext(), BookScreenActivity.class);
        intent.putExtra("storyId", bookId);
        startActivity(intent);
    }

    private void loadUserData() {
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        // Lấy thông tin của trẻ em
        db.collection("children").whereEqualTo("parentId", userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                DocumentSnapshot childDoc = task.getResult().getDocuments().get(0);
                Integer age = Math.toIntExact(childDoc.getLong("age"));

                // Cập nhật age vào ViewModel
                if (age != null) {
                    viewModel.setUserAge(age);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}