package com.example.portfolioapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.portfolioapp.FaqPage;
import com.example.portfolioapp.MyRecyclerViewAdapter;
import com.example.portfolioapp.ProjectInfo;
import com.example.portfolioapp.R;
import com.example.portfolioapp.RecyclerItemClickListener;
import com.example.portfolioapp.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    FirebaseFirestore db;
    RecyclerView mRecyclerView;
    ArrayList<User> userArrayList;
    private FirebaseAuth mAuth;
    MyRecyclerViewAdapter adapter;


    private PageViewModel pageViewModel;

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
        mRecyclerView = root.findViewById(R.id.mRecyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        if (userArrayList.size()>0)
            userArrayList.clear();

        int x=1;
        if(getArguments() != null){
            x = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        if(x==1){
            db.collection("projects")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for(DocumentSnapshot querySnapshot : task.getResult()){
                                User user = new User(querySnapshot.getString("Title"), querySnapshot.getString("Email"));
                                userArrayList.add(user);
                            }
                            adapter = new MyRecyclerViewAdapter(root.getContext(), userArrayList);
                            mRecyclerView.setAdapter(adapter);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(root.getContext(), "fbrefg", Toast.LENGTH_SHORT).show();
                        }

                    });



        }
        else{
            db.collection("projects")
                    .whereEqualTo("Email", mAuth.getCurrentUser().getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for(DocumentSnapshot querySnapshot : task.getResult()){
                                User user = new User(querySnapshot.getString("Title"), querySnapshot.getString("Email"));
                                userArrayList.add(user);
                            }
                            adapter = new MyRecyclerViewAdapter(root.getContext(), userArrayList);
                            mRecyclerView.setAdapter(adapter);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(root.getContext(), "fbrefg", Toast.LENGTH_SHORT).show();
                        }

                    });
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

    private void loadfromFirebase() {

    }


}
