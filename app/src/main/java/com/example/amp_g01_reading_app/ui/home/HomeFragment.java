package com.example.amp_g01_reading_app.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.amp_g01_reading_app.R;
import com.example.amp_g01_reading_app.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class HomeFragment extends Fragment {

    private TextView welcomeTextView;
    private TextView timeLimitTextView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private FragmentHomeBinding binding;
    private BookAdapter popularBooksAdapter;
    private BookAdapter newBooksAdapter;
   // private HomeViewModel homeViewModel;
//    private List<Book> popularBooks = new ArrayList<>();
    private ActivityResultLauncher<Intent> speechRecognizerLauncher;

    private LinearLayout newlyBooks;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        speechRecognizerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null){
                        ArrayList<String> speedResult = result.getData().getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        if (speedResult !=null && !speedResult.isEmpty()){
                            String spokenText = speedResult.get(0);
                            binding.searchEdit.setText(spokenText);
                        }
                    }
                }
        );

//        binding.searchEdit.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {}
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//                String searchQuery = charSequence.toString().trim();
//
//
//                if (!searchQuery.isEmpty()) {
//                    searchBooks(searchQuery);
//                } else {
//                    // Nếu không có chữ hoặc chỉ có dấu cách
//                   // showAllBooks();
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {}
//        });
    }

    private void performSearch(String query) {


        // Ẩn các phần tử không liên quan
        newlyBooks.setVisibility(View.GONE);
    }

    private ArrayList<Book> filterBooks(ArrayList<Book> books, String query) {
        ArrayList<Book> filteredBooks = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredBooks.add(book);
            }
        }
        return filteredBooks;
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        requireActivity().getWindow().setStatusBarColor(
                ContextCompat.getColor(requireContext(), R.color.orange_statusbar)
        );


        setupRecyclerViews();
        observeViewModel(homeViewModel);
        setupClickListeners();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        welcomeTextView = view.findViewById(R.id.welcomeTextView);
        timeLimitTextView = view.findViewById(R.id.timeLimitTextView);
        newlyBooks = view.findViewById(R.id.newlyBooks);

        loadUserData();
    }

    private void setupRecyclerViews() {
        // Setup Popular Books RecyclerView
        binding.popularBooksRecycler.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        popularBooksAdapter = new BookAdapter();
        binding.popularBooksRecycler.setAdapter(popularBooksAdapter);

        // Setup New Books RecyclerView
        binding.newBooksRecycler.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        newBooksAdapter = new BookAdapter();
        binding.newBooksRecycler.setAdapter(newBooksAdapter);


    }

    private void startSpeechRecognization(){
        Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speek to seach...");

        try{
            speechRecognizerLauncher.launch(speechIntent);
        }catch (Exception e){
            Toast.makeText(getContext(), "Thiết bị không hỗ trợ tìm kiếm giọng nói", Toast.LENGTH_LONG).show();
        }
    }

    private void observeViewModel(HomeViewModel viewModel) {
        viewModel.getPopularBooks().observe(getViewLifecycleOwner(), books -> {
            //popularBooks = books;
            popularBooksAdapter.submitList(books);
        });

        viewModel.getNewBooks().observe(getViewLifecycleOwner(), books -> newBooksAdapter.submitList(books));
    }

//    public void searchBooks(String textSearch) {
//        List<Book> filteredBooks = new ArrayList<>();
//
//        // Lọc sách theo tiêu đề
//        for (Book book : popularBooks) {
//            if (book.getTitle().toLowerCase().contains(textSearch.toLowerCase())) {
//                filteredBooks.add(book);
//            }
//        }
//
//        // Cập nhật danh sách sách sau khi tìm kiếm
//        popularBooksAdapter.submitList(filteredBooks);
//    }

    private void setupClickListeners() {
//        binding.viewAllPopular.setOnClickListener(v -> {
//            // Handle view all popular books click
//            newlyBooks.setVisibility(View.INVISIBLE);
//        });
//
//        binding.viewAllNew.setOnClickListener(v -> {
//            // Handle view all new books click
//        });

        binding.voiceSearchButton.setOnClickListener(v -> startSpeechRecognization());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("SetTextI18n")
    private void loadUserData() {
        String userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        db.collection("parents").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String email = document.getString("email");
                            welcomeTextView.setText("Welcome, " + email);
                            timeLimitTextView.setVisibility(View.GONE);
                        }
                    }
                });

        db.collection("children").whereEqualTo("parentId", userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot childDoc = task.getResult().getDocuments().get(0);
                        String name = childDoc.getString("name");
                        Long timeLimit = childDoc.getLong("timeLimit");
                        Integer age = Math.toIntExact(childDoc.getLong("age"));
                        welcomeTextView.setText("Welcome, " + name);
                        timeLimitTextView.setText("Time limit: " + timeLimit + " minutes");
                        timeLimitTextView.setVisibility(View.VISIBLE);


                        if (age != null) {
                            HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
                            homeViewModel.setUserAge(age);
                        }
                    }
                });
    }
}