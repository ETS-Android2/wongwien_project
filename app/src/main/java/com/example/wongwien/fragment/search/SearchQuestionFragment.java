package com.example.wongwien.fragment.search;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.wongwien.R;
import com.example.wongwien.adapter.AdapterQuestion;
import com.example.wongwien.databinding.FragmentSearchQuestionBinding;
import com.example.wongwien.model.ModelQuestionAns;

import java.util.ArrayList;

public class SearchQuestionFragment extends Fragment {
    private static final String TAG = "SearchQuestionFragment";

//    private FragmentSearchQuestionBinding binding;
    AdapterQuestion adapter;
    ArrayList<ModelQuestionAns>qlist;

    RecyclerView rcQustion;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        binding=FragmentSearchQuestionBinding.inflate(inflater,container,false);
//        View view=binding.getRoot();
        View view= inflater.inflate(R.layout.fragment_search_question, container, false);

        rcQustion=view.findViewById(R.id.rcQustion);
        qlist=new ArrayList<>();

        qlist=getArguments().getParcelableArrayList("list");
        if(qlist!=null){
            adapter=new AdapterQuestion(getContext(),qlist);
            showList();
        }
        rcQustion.setLayoutManager(new LinearLayoutManager(getContext()));
        rcQustion.setHasFixedSize(true);
        rcQustion.setAdapter(adapter);

        return view;
    }
    private void showList(){
        for(ModelQuestionAns d:qlist){
            Log.d(TAG, "showList: "+d.toString());
        }
    }
}