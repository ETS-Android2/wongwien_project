package com.example.wongwien.fragment.question;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wongwien.R;
import com.example.wongwien.adapter.AdapterFriend;
import com.example.wongwien.adapter.AdapterQuestion;
import com.example.wongwien.model.ModelQuestionAns;
import com.example.wongwien.model.ModelUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class LoadListShowFragment extends Fragment {
    private static final String TAG = "LoadListShowFragment";

    DatabaseReference ref;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    AdapterQuestion adapter;

    ArrayList<ModelQuestionAns> qlist;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_load_list_show, container, false);


        initView(view);

        qlist = getArguments().getParcelableArrayList("list");

        if (qlist != null) {
            loadRcPost();
        }

        return view;
    }

    private void showArraylist() {
        for (ModelQuestionAns d : qlist) {
            Log.d(TAG, "showArraylist: model" + d.toString());
        }
    }

    private void loadRcPost() {
        adapter = new AdapterQuestion(getContext(), qlist);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.rcView);
        qlist = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }


}