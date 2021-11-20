package com.example.wongwien.fragment.add_review;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wongwien.R;
import com.example.wongwien.databinding.FragmentAddReviewOneImgBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;

import javax.xml.transform.Result;

import static android.app.Activity.RESULT_OK;

public class AddReviewOneImageFragment extends Fragment {
    private static final String TAG = "AddReviewOneImageFragme";
    private FragmentAddReviewOneImgBinding binding;

    //permissions constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    //permission pick image constants
    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 400;

    Uri image_uri;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentAddReviewOneImgBinding.inflate(inflater);

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
                    addToChipGroup(tag);
                    binding.edTag.setText("");

                    hideKeyboard(getActivity());
                }
            }
        });

        /*
        * first  show dialog--> check permission
        * return true pick image
        *        false request permission--> on result permission
        *
        * to pickk image--> intent code to pick uri --> on activity on result
        * return image by code id
        * */
        binding.tvImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String options[]={"Camera","Gallery"};
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext(),R.style.CustomAlertDialog);
                builder.setTitle("Pick Image From");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which){
                            case 0:
                                if(!checkCameraPermission()){
                                    requestCameraPermission();
                                }else{
                                    pickFromCamera();
                                }
                                break;
                            case 1:
                                if(!checkStoragePermission()){
                                    requestStoragePermissions();
                                }else{
                                    pickFromGallery();
                                }
                                break;

                        }
                    }
                });

                builder.create().show();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        if(resultCode==RESULT_OK){
            switch(resultCode){
                case IMAGE_PICK_CAMERA_REQUEST_CODE:
                    uploadImage(image_uri);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImage(Uri image_uri) {

    }

    private void pickFromGallery() {

    }

    private void pickFromCamera() {
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"wongwieng");
        values.put(MediaStore.Images.Media.DESCRIPTION,"wongwieng review");

        image_uri=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {
        switch(requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted&&writeStorageAccepted){
                        pickFromCamera();
                    }else{
                        Toast.makeText(getActivity(), "Plase enable camera & storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void requestCameraPermission() {
        String cameraPermissions[]=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }
    private void requestStoragePermissions() {
        String storagePermissions[]=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }


    private boolean checkCameraPermission() {
        boolean result= ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
        ==(PackageManager.PERMISSION_GRANTED);
        boolean result2= ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ==(PackageManager.PERMISSION_GRANTED);
        return result&&result2;
    }
    private boolean checkStoragePermission() {
        boolean result2= ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ==(PackageManager.PERMISSION_GRANTED);
        return result2;
    }

    private void addToChipGroup(String tag) {
        Chip chip=new Chip(getContext());
        ChipDrawable chipDrawable=ChipDrawable.createFromAttributes(getContext(),
                null,
                0,
                R.style.chipCustom);
        chip.setChipDrawable(chipDrawable);

        chip.setText(tag);
        chip.setCloseIconEnabled(true);
        chip.setTextAppearance(R.style.chipCustom);
        chip.setClickable(false);
        chip.setCheckable(false);

        binding.chipGroup.addView(chip);

        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chipGroup.removeView(chip);
            }
        });

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

    private String getAllTag(){
        String txtTag="";

        for(int i=0;i<binding.chipGroup.getChildCount();i++){
            txtTag=txtTag+"::"+((Chip)binding.chipGroup.getChildAt(i)).getText().toString();
        }
        return txtTag;
    }

    @Override
    public void onAttach(@NonNull Context context) {

        super.onAttach(context);
    }
}