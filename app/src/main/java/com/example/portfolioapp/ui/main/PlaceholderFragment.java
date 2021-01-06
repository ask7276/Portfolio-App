package com.example.portfolioapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portfolioapp.Models.Project;
import com.example.portfolioapp.ProjectAdapter;
import com.example.portfolioapp.Activities.ProjectInfo;
import com.example.portfolioapp.R;
import com.example.portfolioapp.RecyclerItemClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    FirebaseFirestore db;
    RecyclerView mRecyclerView;
    ArrayList<Project> userArrayList;
    FirebaseAuth mAuth;
    ProjectAdapter adapter;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        pageViewModel = new ViewModelProvider(this).get(PageViewModel.class);
//        int index = 1;
//        if (getArguments() != null) {
//            index = getArguments().getInt(ARG_SECTION_NUMBER);
//        }
//        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main2, container, false);

        userArrayList = new ArrayList<>();
        mRecyclerView = root.findViewById(R.id.project_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if (userArrayList.size() > 0)
            userArrayList.clear();

        int x = 1;
        if (getArguments() != null) {
            x = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        if (x == 1) {
            db.collection("projects")
                    .get()
                    .addOnCompleteListener(task -> {
                        for (DocumentSnapshot querySnapshot : Objects.requireNonNull(task.getResult())) {
                            Project project = new Project(querySnapshot.getString("Title"), querySnapshot.getString("Email"));
                            userArrayList.add(project);
                        }
                        adapter = new ProjectAdapter(root.getContext(), userArrayList);
                        mRecyclerView.setAdapter(adapter);
                    })
                    .addOnFailureListener(e -> Toast.makeText(root.getContext(), "fbrefg", Toast.LENGTH_SHORT).show());


        } else {
            db.collection("projects")
                    .whereEqualTo("Email", Objects.requireNonNull(mAuth.getCurrentUser()).getEmail())
                    .get()
                    .addOnCompleteListener(task -> {
                        for (DocumentSnapshot querySnapshot : Objects.requireNonNull(task.getResult())) {
                            Project project = new Project(querySnapshot.getString("Title"), querySnapshot.getString("Email"));
                            userArrayList.add(project);
                        }
                        adapter = new ProjectAdapter(root.getContext(), userArrayList);
                        mRecyclerView.setAdapter(adapter);
                    })
                    .addOnFailureListener(e -> Toast.makeText(root.getContext(), "fbrefg", Toast.LENGTH_SHORT).show());
        }

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(root.getContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle b = new Bundle();
                b.putString("Email", userArrayList.get(position).getEmail());
                b.putString("Title", userArrayList.get(position).getProject_Title());
                Intent intent = new Intent(root.getContext(), ProjectInfo.class);
                intent.putExtras(b);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
//        final TextView textView = root.findViewById(R.id.section_label2);
//        pageViewModel.getText().observe(this, textView::setText);
        return root;
    }


}
