package com.example.wongwien.fragment.add_review;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.wongwien.AddQuestionActivity;
import com.example.wongwien.AddReviewActivity;
import com.example.wongwien.R;
import com.example.wongwien.databinding.FragmentAddReviewNoImageBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;

public class AddReviewNoImageFragment extends Fragment {
    private FragmentAddReviewNoImageBinding binding;
    private GetAllDataToActivity getdata;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddReviewNoImageBinding.inflate(inflater);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.collections, R.layout.spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCollection.setAdapter(adapter);


        binding.txtAddtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.showTag.getVisibility()==View.VISIBLE){
                    binding.showTag.setVisibility(View.GONE);
                }else{
                    binding.showTag.setVisibility(View.VISIBLE);
                    binding.edTag.requestFocus();
                }
            }
        });

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag=binding.edTag.getText().toString().trim();
                if(!TextUtils.isEmpty(tag)){
                    addToChipGroup(tag,binding.chipGroup);
                    binding.edTag.setText("");
//                    edTag.clearFocus();

                    hideKeyboard(getActivity());

                }
            }
        });

        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag=getAllTag();
                String title = binding.edTitle.getText().toString().trim();
                String collection = binding.spinnerCollection.getSelectedItem().toString();
                String descipt = binding.edDescription.getText().toString().trim();

                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(descipt)) {

                    if(TextUtils.isEmpty(tag)){
                        tag="";
                    }

                    getdata.uploadReviewWithNoImage(title, descipt,tag,collection);
                }else{
                    Toast.makeText(getContext(), "Please fill all", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return binding.getRoot();
    }

    private void hideKeyboard(FragmentActivity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void addToChipGroup(String tag, ChipGroup chipGroup) {
        Chip chip = new Chip(getContext());
        ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(getContext(),
                null,
                0,
                R.style.chipCustom);
        chip.setChipDrawable(chipDrawable);

//        Chip chip=new Chip(AddQuestionActivity.this,null,R.style.chipCustom);
        chip.setChipText(tag);
        chip.setCloseIconEnabled(true);
        chip.setTextAppearanceResource(R.style.chipCustom);
        chip.setClickable(false);
        chip.setCheckable(false);
        chipGroup.addView(chip);

        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(chip);
            }
        });
    }
    private String getAllTag() {
        String txtTag="";
        for(int i=0;i<binding.chipGroup.getChildCount();i++){
            txtTag=txtTag+"::"+((Chip) binding.chipGroup.getChildAt(i)).getText().toString();
        }
        return txtTag;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        getdata = (GetAllDataToActivity) getActivity();
        super.onAttach(context);
    }
}