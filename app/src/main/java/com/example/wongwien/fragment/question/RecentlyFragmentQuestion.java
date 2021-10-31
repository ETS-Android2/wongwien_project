package com.example.wongwien.fragment.question;

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

public class RecentlyFragmentQuestion extends Fragment {
    private static final String TAG = "RecentlyFragmentQuestio";

    DatabaseReference ref;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;

    List<ModelQuestionAns> qlist;
    List<ModelUser> userList;
    AdapterFriend adapterFriend;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.question_fragment_recently, container, false);

        Log.d(TAG, "onCreateView: commplete add");

        initView(view);
        loadRecentlyPost();
        return view;
    }

    private void loadRecentlyPost() {
        ref=FirebaseDatabase.getInstance().getReference("QuestionAns");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                qlist.clear();
                for(DataSnapshot d:snapshot.getChildren()){
                    ModelQuestionAns q=d.getValue(ModelQuestionAns.class);
                    Log.d(TAG, "onDataChange: ::"+q.getQuestion());
                    qlist.add(q);

                }
                    Log.d(TAG, "onDataChange: list::" + qlist.get(0).getQuestion());

                AdapterQuestion question=new AdapterQuestion(getContext(),qlist);
                recyclerView.setAdapter(question);

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }
    private void loadFriend() {
        userList=new ArrayList<>();
        Log.d(TAG, "loadFriend: ");
        ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                userList.clear();

                for(DataSnapshot d:snapshot.getChildren()){
                    ModelUser user1=d.getValue(ModelUser.class);
                        userList.add(user1);

                    adapterFriend=new AdapterFriend(userList, getActivity());
                    recyclerView.setAdapter(adapterFriend);
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }
    private void initView(View view) {
        recyclerView=view.findViewById(R.id.rcView);
        qlist=new ArrayList<>();

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }


}