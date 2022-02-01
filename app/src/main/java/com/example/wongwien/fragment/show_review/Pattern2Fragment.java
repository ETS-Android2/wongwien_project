package com.example.wongwien.fragment.show_review;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wongwien.MapsActivity;
import com.example.wongwien.QuesAnsDetailActivity;
import com.example.wongwien.R;
import com.example.wongwien.SearchActivity;
import com.example.wongwien.databinding.FragmentPattern1Binding;
import com.example.wongwien.databinding.FragmentPattern2Binding;
import com.example.wongwien.model.ModelMylocation;
import com.example.wongwien.model.ModelReview;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Locale;

public class Pattern2Fragment extends Fragment {
    private FragmentPattern2Binding binding;
    ModelReview review;
    ModelMylocation mylocation;

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
        binding.txtPoint.setText(String.valueOf(review.getR_point()));
        binding.txtName.setText(review.getuName());
        binding.txtEmail.setText(review.getuEmail());
        binding.txtTime.setText(dateTime);

        loadLocation(review.getrId());

        try{
            if(review.getuImg()!=null){
                Picasso.get().load(review.getuImg()).into(binding.imgPerson);
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
    private void loadLocation(String rId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Reviews").child(rId).child("Mylocation");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try{
                    mylocation = snapshot.getValue(ModelMylocation.class);
                    if(mylocation!=null){
                        binding.showAddress.setVisibility(View.VISIBLE);
                        String title=mylocation.getMap_title();
                        if(title.equals("My Location")){
                            title=review.getuName()+" Location";
                        }
                        binding.txtMapTitle .setText(title);
                        String add=mylocation.getAddress();
                        String address[] = add.split(",");
                        binding.txtShowAddress.setText(address[address.length-2]+","+address[address.length-1]);

                        binding.showAddress.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent=new Intent(getContext(), MapsActivity.class);
                                String maptitle=mylocation.getMap_title();
                                if(maptitle.equals("My Location")){
                                    maptitle=review.getuName() +" Location";
                                }
                                intent.putExtra("view","view");
                                intent.putExtra("map_title",maptitle);
                                intent.putExtra("map_address",mylocation.getAddress());
                                intent.putExtra("map_lo",mylocation.getLongitude());
                                intent.putExtra("map_la",mylocation.getLatitude());
                                startActivity(intent);
                            }
                        });
                    }else{
                        binding.txtShowAddress.setText("hello");
                        binding.showAddress.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}