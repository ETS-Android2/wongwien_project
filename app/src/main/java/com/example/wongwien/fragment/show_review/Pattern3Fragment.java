package com.example.wongwien.fragment.show_review;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wongwien.R;
import com.example.wongwien.databinding.FragmentPattern2Binding;
import com.example.wongwien.databinding.FragmentPattern3Binding;

public class Pattern3Fragment extends Fragment {
    FragmentPattern3Binding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding= FragmentPattern3Binding.inflate(inflater, container, false);
        View view=binding.getRoot();

        return view;
    }
}