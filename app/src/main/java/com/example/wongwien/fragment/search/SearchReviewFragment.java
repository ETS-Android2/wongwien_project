package com.example.wongwien.fragment.search;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wongwien.R;
import com.example.wongwien.adapter.AdapterQuestion;
import com.example.wongwien.adapter.AdapterReview;
import com.example.wongwien.model.ModelReview;

import java.util.ArrayList;


public class SearchReviewFragment extends Fragment {
    ArrayList<ModelReview>reviewList;
    RecyclerView rcReview;
    AdapterReview adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_search_review, container, false);

        rcReview=view.findViewById(R.id.rcReview);
        reviewList=new ArrayList<>();

        reviewList=getArguments().getParcelableArrayList("list");
        if(reviewList!=null){
            adapter=new AdapterReview(getContext(),reviewList);
        }
        rcReview.setLayoutManager(new LinearLayoutManager(getContext()));
        rcReview.setHasFixedSize(true);
        rcReview.setAdapter(adapter);

        return view;
    }
}