package com.example.wongwien;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wongwien.adapter.AdapterFriend;
import com.example.wongwien.adapter.AdapterQuestion;
import com.example.wongwien.adapter.AdapterReview;
import com.example.wongwien.databinding.FragmentLoadDataToProfileBinding;
import com.example.wongwien.model.ModelQuestionAns;
import com.example.wongwien.model.ModelReview;

import java.util.ArrayList;

public class loadDataToProfileFragment extends Fragment {
    private FragmentLoadDataToProfileBinding binding;
    ArrayList<ModelReview>listR;
    ArrayList<ModelQuestionAns>listQ;
    AdapterReview radapter;
    AdapterQuestion qadapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentLoadDataToProfileBinding.inflate(inflater,container,false);
        View view=binding.getRoot();

        binding.rcList.setHasFixedSize(true);
        binding.rcList.setLayoutManager(new LinearLayoutManager(getContext()));

        listQ=new ArrayList<>();
        listR=new ArrayList<>();

        listQ=getArguments().getParcelableArrayList("question");
        listR=getArguments().getParcelableArrayList("review");

        if(null != listQ){
            qadapter=new AdapterQuestion(getContext(),listQ);
            binding.rcList.setAdapter(qadapter);
        }
        if(null != listR){
            radapter=new AdapterReview(getContext(),listR);
            binding.rcList.setAdapter(radapter);
        }
        return view;
    }
}