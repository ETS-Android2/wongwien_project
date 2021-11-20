package com.example.wongwien.fragment.add_review;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wongwien.R;
import com.example.wongwien.databinding.FragmentAddReviewImageHorizontalBinding;

public class AddReviewImageHorizontalFragment extends Fragment {
    private FragmentAddReviewImageHorizontalBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding =FragmentAddReviewImageHorizontalBinding.inflate(inflater);
        return binding.getRoot();
    }
}