package com.example.wongwien.fragment.add_review;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.wongwien.AddQuestionActivity;
import com.example.wongwien.AddReviewActivity;
import com.example.wongwien.MapsActivity;
import com.example.wongwien.R;
import com.example.wongwien.databinding.FragmentAddReviewNoImageBinding;
import com.example.wongwien.model.ModelMylocation;
import com.example.wongwien.model.ModelReview;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddReviewNoImageFragment extends Fragment {
    private static final String TAG = "AddReviewNoImage";
    private FragmentAddReviewNoImageBinding binding;
    private GetAllDataToActivity getdata;
    public static final int ERROR_DIALOG_REQUESST=9001;
    public static final int MY_LOCATION=9002;

    DatabaseReference ref;
    ModelReview review;
    HashMap<String,String> mylocation;

    ModelMylocation location;

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

        Bundle bundle=getArguments();
        mylocation=new HashMap<>();
        if(bundle!=null){
            review=bundle.getParcelable("list");

            binding.edTitle.setText(review.getR_title());
            binding.edDescription.setText(review.getR_desc0());
            String tag=review.getR_tag();
            if (!tag.equals("")) {
                String tags[] = tag.split("::");
                for (String s : tags) {
                    if (!s.equals("")) {
                        addToChipGroup(s, binding.chipGroup);
                    }
                }
            }
            loadLocation(review.getrId());

            binding.btnPost.setText("Update");
            binding.btnPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String rId=review.getrId();

                    String tag=getAllTag();
                    String title = binding.edTitle.getText().toString().trim();
                    String collection = binding.spinnerCollection.getSelectedItem().toString();
                    String descipt = binding.edDescription.getText().toString().trim();

                    ref= FirebaseDatabase.getInstance().getReference("Reviews").child(rId);
                    ref.child("r_title").setValue(title);
                    ref.child("r_desc0").setValue(descipt);
                    ref.child("r_collection").setValue(collection);
                    ref.child("r_tag").setValue(tag)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            ref= FirebaseDatabase.getInstance().getReference("Reviews").child(rId).child("Mylocation");
                            ref.child("map_title").setValue(mylocation.get("map_title"));
                            ref.child("address").setValue(mylocation.get("address"));
                            ref.child("latitude").setValue(mylocation.get("latitude"));
                            ref.child("longitude").setValue(mylocation.get("longitude"))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            getdata.updateReview(true);
                                        }
                                    });
                        }
                    });
                }
            });

        }else{
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

                        getdata.uploadReviewWithNoImage(title, descipt,tag,collection,mylocation);
                    }else{
                        Toast.makeText(getContext(), "Please fill all", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        binding.txtAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isService()){
                    Intent intent=new Intent(getContext(), MapsActivity.class);
                    startActivityForResult(intent,MY_LOCATION);
                }
            }
        });


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


        return binding.getRoot();
    }

    private void loadLocation(String rId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Reviews").child(rId).child("Mylocation");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try{
                    location = snapshot.getValue(ModelMylocation.class);
                    Log.d(TAG, "onDataChange: location::"+mylocation);
                    if(location!=null){
                        binding.showAddress.setVisibility(View.VISIBLE);
                        binding.txtAddLocation.setVisibility(View.GONE);
                        binding.txtShowAddress.setText(location.getAddress());

                        mylocation.put("map_title",location.getMap_title());
                        mylocation.put("address",location.getAddress());
                        mylocation.put("latitude",location.getLatitude());
                        mylocation.put("longitude",location.getLongitude());

                        binding.showAddress.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent=new Intent(getContext(), MapsActivity.class);
                                String maptitle=location.getMap_title();
                                intent.putExtra("map_title",maptitle);
                                intent.putExtra("map_address",location.getAddress());
                                intent.putExtra("map_lo",location.getLongitude());
                                intent.putExtra("map_la",location.getLatitude());
                                startActivityForResult(intent,MY_LOCATION);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == MY_LOCATION) {
            Log.d(TAG, "onActivityResult: MY_LOCATION using");
            // here you can retrieve your bundle data.
            String yourdata = data.getStringExtra("Sent");

            mylocation.put("map_title",data.getStringExtra("title"));
            mylocation.put("address",data.getStringExtra("address"));
            mylocation.put("latitude",data.getStringExtra("loc1"));
            mylocation.put("longitude",data.getStringExtra("loc2"));

            Log.d(TAG, "onActivityResult: yourdata::"+yourdata);
            Log.d(TAG, "onActivityResult: yourdata::"+mylocation.get(0));
            showAddress();
        }
    }
    private void showAddress() {
        if (mylocation.size() > 0) {
            binding.txtAddLocation.setVisibility(View.GONE);
            binding.showAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getContext(), MapsActivity.class);
                    intent.putExtra("map_title",mylocation.get("map_title"));
                    intent.putExtra("map_address",mylocation.get("address"));
                    intent.putExtra("map_la",mylocation.get("latitude"));
                    intent.putExtra("map_lo",mylocation.get("longitude"));
                    startActivityForResult(intent,MY_LOCATION);
                }
            });
            binding.showAddress.setVisibility(View.VISIBLE);
            binding.txtShowAddress.setText(mylocation.get("address"));
            Log.d(TAG, "showAddress: name "+mylocation.get("map_title"));
        }
    }

    public boolean isService(){
        int avalible= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
        if(avalible== ConnectionResult.SUCCESS){
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(avalible)){
            //error occured but we can resolve it
            Dialog dialog=GoogleApiAvailability.getInstance().getErrorDialog((Activity) getContext(),avalible,ERROR_DIALOG_REQUESST);
            dialog.show();
        }else{
            Toast.makeText(getContext(), "Something occurs you can't make map requests ", Toast.LENGTH_SHORT).show();
        }
        return false;
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