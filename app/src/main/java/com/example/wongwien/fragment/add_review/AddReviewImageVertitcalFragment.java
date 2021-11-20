package com.example.wongwien.fragment.add_review;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wongwien.R;
import com.example.wongwien.databinding.FragmentAddReviewImageVertitcalBinding;

public class AddReviewImageVertitcalFragment extends Fragment {
    private FragmentAddReviewImageVertitcalBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding=FragmentAddReviewImageVertitcalBinding.inflate(inflater);
       return binding.getRoot();
    }
}