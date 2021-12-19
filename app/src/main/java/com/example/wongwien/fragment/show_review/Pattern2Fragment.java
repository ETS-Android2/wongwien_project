package com.example.wongwien.fragment.show_review;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wongwien.R;
import com.example.wongwien.databinding.FragmentPattern1Binding;
import com.example.wongwien.databinding.FragmentPattern2Binding;
import com.example.wongwien.model.ModelReview;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Locale;

public class Pattern2Fragment extends Fragment {
    private FragmentPattern2Binding binding;
    ModelReview review;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding= FragmentPattern2Binding.inflate(inflater, container, false);
        View view=binding.getRoot();

        review=getArguments().getParcelable("list");

        String timeStamp=review.getR_timeStamp();

        //conver time stamp to dd/mm/yyyy hh:mm am/pm
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = (String) DateFormat.format("dd/MM/yyyy hh:mm:aa", cal);

        binding.rTitle.setText(review.getR_title());
        binding.rDesc0.setText(review.getR_desc0());
        binding.txtPoint.setText(review.getR_point());
        binding.txtName.setText(review.getuName());
        binding.txtEmail.setText(review.getuEmail());
        binding.txtTime.setText(dateTime);

        try{
            if(review.getuImage()!=null){
                Picasso.get().load(review.getuImage()).into(binding.imgPerson);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            if(review.getR_image0()!=null){
                Picasso.get().load(review.getR_image0()).into(binding.rImage0);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }
}