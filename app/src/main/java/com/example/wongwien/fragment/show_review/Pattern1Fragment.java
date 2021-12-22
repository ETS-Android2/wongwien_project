package com.example.wongwien.fragment.show_review;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wongwien.R;
import com.example.wongwien.SearchActivity;
import com.example.wongwien.databinding.FragmentPattern1Binding;
import com.example.wongwien.model.ModelReview;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Locale;

public class Pattern1Fragment extends Fragment {
    private static final String TAG = "Pattern1Fragment";
    private FragmentPattern1Binding binding;
    ModelReview review;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentPattern1Binding.inflate(inflater, container, false);
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

        Log.d(TAG, "onCreateView: image::"+review.getuImg());
        try{
            if(review.getuImg()!=null){
                Picasso.get().load(review.getuImg()).into(binding.imgPerson);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        String tag=review.getR_tag();
        if (!tag.equals("")) {
            String tags[] = tag.split("::");
            for (String s : tags) {
                if (!s.equals("")) {
                    addToChipGroup(s, binding.chipGroup);
                }
            }
        }


        return view;
    }
    private void addToChipGroup(String s, ChipGroup chipGroup) {
        Chip chip = new Chip(getContext());
        ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(getContext(),
                null,
                0,
                R.style.chipCustom);
        chip.setChipDrawable(chipDrawable);

//        Chip chip = new Chip(QuesAnsDetailActivity.this, null, R.style.chipCustom);
        chip.setChipText(s);
        chip.setCloseIconEnabled(false);
        chip.setClickable(true);
        chip.setCheckable(false);
        chip.setTextAppearanceResource(R.style.chipCustom);
        chipGroup.addView(chip);

        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                intent.putExtra("tag", s);
                intent.putExtra("collection", "review");
                startActivity(intent);
            }
        });
    }
}