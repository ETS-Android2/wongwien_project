package com.example.wongwien.fragment.add_review;

import static android.app.Activity.RESULT_OK;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.wongwien.EdittextV2;
import com.example.wongwien.R;
import com.example.wongwien.databinding.FragmentAddReviewImageVertitcalBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;

import java.util.ArrayList;

public class AddReviewImageVertitcalFragment extends Fragment {
    private static final String TAG = "AddReviewImageVertitcal";
    private FragmentAddReviewImageVertitcalBinding binding;
    //permissions constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;


    //permission pick image constants
    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE_1 = 301;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE_1 = 401;

    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE_2 = 302;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE_2 = 402;

    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE_3 = 303;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE_3 = 403;

    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE_4 = 304;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE_4 = 404;

    private GetAllDataToActivity getdata;

    int codeRequest_camera;
    int codeRequest_gallery;
    Uri image_uri;

    ArrayList<Uri>allUri;
    ArrayList<String>allDescrip;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentAddReviewImageVertitcalBinding.inflate(inflater);

        allUri=new ArrayList<>();
        allDescrip=new ArrayList<>();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.collections, R.layout.spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCollection.setAdapter(adapter);

        binding.addmore1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.addmore1.getVisibility()==View.VISIBLE){
                    binding.addmore1.setVisibility(View.GONE);
                    binding.more2.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.addmore2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.addmore2.getVisibility()==View.VISIBLE){
                    binding.addmore2.setVisibility(View.GONE);
                    binding.more3.setVisibility(View.VISIBLE);
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
                    addToChipGroup(tag);
                    binding.edTag.setText("");

                    hideKeyboard(getActivity());
                }
            }
        });

        binding.im1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeRequest_gallery=IMAGE_PICK_GALLERY_REQUEST_CODE_1;
                codeRequest_camera=IMAGE_PICK_CAMERA_REQUEST_CODE_1;
                showDialogPickImage();
            }
        });

        binding.im2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeRequest_gallery=IMAGE_PICK_GALLERY_REQUEST_CODE_2;
                codeRequest_camera=IMAGE_PICK_CAMERA_REQUEST_CODE_2;
                showDialogPickImage();
            }
        });
        binding.im3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeRequest_gallery=IMAGE_PICK_GALLERY_REQUEST_CODE_3;
                codeRequest_camera=IMAGE_PICK_CAMERA_REQUEST_CODE_3;
                showDialogPickImage();
            }
        });
        binding.im4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                codeRequest_gallery=IMAGE_PICK_GALLERY_REQUEST_CODE_4;
                codeRequest_camera=IMAGE_PICK_CAMERA_REQUEST_CODE_4;
                showDialogPickImage();
            }
        });


        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });
        return binding.getRoot();
    }

    private void uploadData() {
        String tag=getAllTag();
        String title = binding.edTitle.getText().toString().trim();
        String collection = binding.spinnerCollection.getSelectedItem().toString();

        if (!TextUtils.isEmpty(title) ) {

            if(TextUtils.isEmpty(tag)){
                tag="";
            }

            EdittextV2[] edViews={
                    binding.edD1,binding.edD2,binding.edD3,binding.edD4
            };

            for(int i=0;i<allUri.size();i++){
                allDescrip.add(edViews[i].getText().toString().trim());
            }

            getdata.uploadReviewWithVerticalImage(title,allDescrip,allUri,tag,collection);


        }else{
            Toast.makeText(getContext(), "Please fill title review", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialogPickImage(){
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
                            pickFromCamera(codeRequest_camera);
                        }
                        break;
                    case 1:
                        if(!checkStoragePermission()){
                            requestStoragePermissions();
                        }else{
                            pickFromGallery(codeRequest_gallery);
                        }
                        break;

                }
            }
        });

        builder.create().show();
    }


    /*
     * check and request permission
     * --image
     *
     * */

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK){
            switch(requestCode){
                case IMAGE_PICK_CAMERA_REQUEST_CODE_1:
                    changeImageToImageView(binding.im1,image_uri);
                    Log.d(TAG, "onActivityResult: image::"+image_uri);
                    break;

                case IMAGE_PICK_GALLERY_REQUEST_CODE_1:
                    image_uri=data.getData();
                    changeImageToImageView(binding.im1,image_uri);
                    Log.d(TAG, "onActivityResult: requestcode::"+codeRequest_gallery);
                    Log.d(TAG, "onActivityResult: image::"+image_uri);
                    break;

                case IMAGE_PICK_CAMERA_REQUEST_CODE_2:
                    changeImageToImageView(binding.im2,image_uri);
                    break;

                case IMAGE_PICK_GALLERY_REQUEST_CODE_2:
                    image_uri=data.getData();
                    changeImageToImageView(binding.im2,image_uri);
                    break;

                case IMAGE_PICK_CAMERA_REQUEST_CODE_3:
                    changeImageToImageView(binding.im3,image_uri);
                    break;

                case IMAGE_PICK_GALLERY_REQUEST_CODE_3:
                    image_uri=data.getData();
                    changeImageToImageView(binding.im3,image_uri);
                    break;

                case IMAGE_PICK_CAMERA_REQUEST_CODE_4:
                    changeImageToImageView(binding.im4,image_uri);
                    break;

                case IMAGE_PICK_GALLERY_REQUEST_CODE_4:
                    image_uri=data.getData();
                    changeImageToImageView(binding.im4,image_uri);
                    break;

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull  int[] grantResults) {
        switch(requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length>0){
                    boolean cameraAccepted=grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted=grantResults[1]==PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted&&writeStorageAccepted){
                        pickFromCamera(codeRequest_camera);
                    }else{
                        Toast.makeText(getActivity(), "Plase enable camera & storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAcceipted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAcceipted) {
                        pickFromGallery(codeRequest_gallery);
                    } else {
                        Toast.makeText(getActivity(), "Plase enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void pickFromGallery(int codeRequest) {
        Intent gallery=new Intent(Intent.ACTION_PICK);
        gallery.setType("image/*");
        startActivityForResult(gallery,codeRequest);
        Log.d(TAG, "pickFromGallery: requestcode::"+codeRequest);
    }

    private void pickFromCamera(int codeRequest) {
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"wongwieng");
        values.put(MediaStore.Images.Media.DESCRIPTION,"wongwieng review");

        image_uri=getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,codeRequest);
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

    /******************************/


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

    public void changeImageToImageView(ImageView imageView, Uri img){
        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setImageURI(img);

        allUri.add(img);

        Log.d(TAG, "changeImageToImageView: img::"+img);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        getdata= (GetAllDataToActivity) getActivity();
        super.onAttach(context);
    }
}