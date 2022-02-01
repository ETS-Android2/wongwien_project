package com.example.wongwien.fragment.add_review;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import com.example.wongwien.MapsActivity;
import com.example.wongwien.R;
import com.example.wongwien.SplashActivity;
import com.example.wongwien.databinding.FragmentAddReviewImageVertitcalBinding;
import com.example.wongwien.model.ModelMylocation;
import com.example.wongwien.model.ModelReview;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

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

    public static final int ERROR_DIALOG_REQUESST=9001;
    public static final int MY_LOCATION=9002;
    HashMap<String,String> mylocation;

    int codeRequest_camera;
    int codeRequest_gallery;
    Uri image_uri;

    Uri[] allUriPosition;
    ArrayList<String>allDescrip;

    ProgressDialog progressDialog;
    FirebaseStorage storage;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    String myUid;

    ModelReview review;
    ModelMylocation location;
    DatabaseReference ref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentAddReviewImageVertitcalBinding.inflate(inflater);

        initView();
        checkUserStatus();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.collections, R.layout.spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCollection.setAdapter(adapter);
        mylocation=new HashMap<>();

        binding.txtAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isService()){
                    Intent intent=new Intent(getContext(), MapsActivity.class);
                    startActivityForResult(intent,MY_LOCATION);
                }
            }
        });

        Bundle bundle=getArguments();
        if(bundle!=null){

            review=bundle.getParcelable("list");
            binding.edTitle.setText(review.getR_title());

            switch (Integer.parseInt(review.getR_num())){
                case 1:
                    binding.edD1.setText(review.getR_desc0());
                    changeImageToImageView(binding.im1,review.getR_image0(),0);
                    break;

                case 2:
                    binding.edD1.setText(review.getR_desc0());
                    binding.edD2.setText(review.getR_desc1());

                    changeImageToImageView(binding.im1,review.getR_image0(),0);
                    changeImageToImageView(binding.im2,review.getR_image1(),1);
                    break;

                case 3:
                    binding.more2.setVisibility(View.VISIBLE);

                    binding.addmore1.setVisibility(View.GONE);

                    binding.edD1.setText(review.getR_desc0());
                    binding.edD2.setText(review.getR_desc1());
                    binding.edD3.setText(review.getR_desc2());

                    changeImageToImageView(binding.im1,review.getR_image0(),0);
                    changeImageToImageView(binding.im2,review.getR_image1(),1);
                    changeImageToImageView(binding.im3,review.getR_image2(),2);
                    break;
                case 4:
                    binding.more2.setVisibility(View.VISIBLE);
                    binding.more3.setVisibility(View.VISIBLE);

                    binding.addmore1.setVisibility(View.GONE);
                    binding.addmore2.setVisibility(View.GONE);

                    binding.edD1.setText(review.getR_desc0());
                    binding.edD2.setText(review.getR_desc1());
                    binding.edD3.setText(review.getR_desc2());
                    binding.edD4.setText(review.getR_desc3());

                    changeImageToImageView(binding.im1,review.getR_image0(),0);
                    changeImageToImageView(binding.im2,review.getR_image1(),1);
                    changeImageToImageView(binding.im3,review.getR_image2(),2);
                    changeImageToImageView(binding.im4,review.getR_image3(),3);
                    break;
                default:
                    break;
            }
            String tag=review.getR_tag();
            if (!tag.equals("")) {
                String tags[] = tag.split("::");
                for (String s : tags) {
                    if (!s.equals("")) {
                        addToChipGroup(s);
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

                    ref= FirebaseDatabase.getInstance().getReference("Reviews").child(rId);
                    ref.child("r_title").setValue(title);

                    int count=0;
                    for(int i=0;i<allUriPosition.length;i++){
                        if(allUriPosition[i]!=null){
                            count++;
                        }
                    }
//                    Log.d(TAG, "onClick: count::"+count);

                    try{
                        ref.child("r_desc0").setValue(binding.edD1.getText().toString().trim());
                        ref.child("r_desc1").setValue(binding.edD2.getText().toString().trim());
                        ref.child("r_desc2").setValue(binding.edD3.getText().toString().trim());
                        ref.child("r_desc3").setValue(binding.edD4.getText().toString().trim());
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    try{
                        ref.child("r_image0").setValue(String.valueOf(allUriPosition[0]));
                        ref.child("r_image1").setValue(String.valueOf(allUriPosition[1]));
                        ref.child("r_image2").setValue(String.valueOf(allUriPosition[2]));
                        ref.child("r_image3").setValue(String.valueOf(allUriPosition[3]));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    ref.child("r_num").setValue(String.valueOf(count));
                    ref.child("r_collection").setValue(collection);
                    ref.child("r_tag").setValue(tag).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                    uploadData();
                }
            });
        }

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



    private void initView() {
        allUriPosition=new Uri[4];
        allDescrip=new ArrayList<>();
        progressDialog=new ProgressDialog(getContext());
        storage=FirebaseStorage.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
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



            for(int i=0;i<allUriPosition.length;i++){
                if (!TextUtils.isEmpty(edViews[i].getText().toString().trim())){
                    allDescrip.add(edViews[i].getText().toString().trim());
                }else{
                    allDescrip.add("");
                }
            }

            ArrayList<Uri>allImageUri=new ArrayList<>();

            int count=0;

            for(int i=0;i<allUriPosition.length;i++){
                if(allUriPosition[i]!=null){
                    count++;
                    allImageUri.add(allUriPosition[i]);
                }
            }

            for(int i=0;i<allImageUri.size();i++){
                Log.d(TAG, "uploadData: image::"+allImageUri.get(i));
            }

            getdata.uploadReviewWithVerticalImage(title,allDescrip,allImageUri,tag,collection,count,mylocation);


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
                    changeImageToImageView(binding.im1,image_uri,0);
                    Log.d(TAG, "onActivityResult: image::"+image_uri);
                    break;

                case IMAGE_PICK_GALLERY_REQUEST_CODE_1:
                    image_uri=data.getData();
                    changeImageToImageView(binding.im1,image_uri,0);
                    break;

                case IMAGE_PICK_CAMERA_REQUEST_CODE_2:
                    changeImageToImageView(binding.im2,image_uri,1);
                    break;

                case IMAGE_PICK_GALLERY_REQUEST_CODE_2:
                    image_uri=data.getData();
                    changeImageToImageView(binding.im2,image_uri,1);
                    break;

                case IMAGE_PICK_CAMERA_REQUEST_CODE_3:
                    changeImageToImageView(binding.im3,image_uri,2);
                    break;

                case IMAGE_PICK_GALLERY_REQUEST_CODE_3:
                    image_uri=data.getData();
                    changeImageToImageView(binding.im3,image_uri,2);
                    break;

                case IMAGE_PICK_CAMERA_REQUEST_CODE_4:
                    changeImageToImageView(binding.im4,image_uri,3);
                    break;

                case IMAGE_PICK_GALLERY_REQUEST_CODE_4:
                    image_uri=data.getData();
                    changeImageToImageView(binding.im4,image_uri,3);
                    break;

                case  MY_LOCATION :
                    // here you can retrieve your bundle data.
                    String yourdata = data.getStringExtra("Sent");

                    mylocation.put("map_title",data.getStringExtra("title"));
                    mylocation.put("address",data.getStringExtra("address"));
                    mylocation.put("latitude",data.getStringExtra("loc1"));
                    mylocation.put("longitude",data.getStringExtra("loc2"));

                    Log.d(TAG, "onActivityResult: yourdata::"+yourdata);
                    Log.d(TAG, "onActivityResult: yourdata::"+mylocation.get(0));
                    showAddress();

                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void showAddress() {
        if (mylocation.size() > 0) {
            binding.txtAddLocation.setVisibility(View.GONE);
            binding.showAddress.setVisibility(View.VISIBLE);
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
            binding.txtShowAddress.setText(mylocation.get("address"));
        }
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
                    boolean writeStorageAcceipted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
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

    public void changeImageToImageView(ImageView imageView, Uri img, int i){
        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
//        imageView.setImageURI(img);

        progressDialog.setMessage("Upload image...");
        progressDialog.show();

        String timeStamp = String.valueOf(System.currentTimeMillis());

        String filePathAndName = "Review_image/" + myUid + "_" + timeStamp;
        storage= FirebaseStorage.getInstance();

        StorageReference s_ref = storage.getReference().child(filePathAndName);
        s_ref.putFile(img).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = s_ref.putFile(img).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return s_ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Log.d(TAG, "onComplete: img Uri::" + downloadUri);

                            image_uri=downloadUri;
                            allUriPosition[i]=image_uri;

                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });

        try{
            Picasso.get().load(image_uri).into(imageView);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    public void changeImageToImageView(ImageView imageView, String img,int i){
        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        try{
            Picasso.get().load(img).into(imageView);
            allUriPosition[i]=Uri.parse(img);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        getdata= (GetAllDataToActivity) getActivity();
        super.onAttach(context);
    }

    private void checkUserStatus() {
        //get current user
        user = firebaseAuth.getCurrentUser();
        if (user != null) {
            myUid = user.getUid();
        } else {
            //go back to login
            startActivity(new Intent(getContext(), SplashActivity.class));
        }
    }
}