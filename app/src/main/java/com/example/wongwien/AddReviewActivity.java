package com.example.wongwien;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.wongwien.databinding.ActivityAddReviewBinding;
import com.example.wongwien.fragment.add_review.AddReviewImageHorizontalFragment;
import com.example.wongwien.fragment.add_review.AddReviewImageVertitcalFragment;
import com.example.wongwien.fragment.add_review.AddReviewNoImageFragment;
import com.example.wongwien.fragment.add_review.AddReviewOneImageFragment;
import com.example.wongwien.fragment.add_review.GetAllDataToActivity;
import com.example.wongwien.model.ModelUser;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
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

import java.util.ArrayList;
import java.util.HashMap;

public class AddReviewActivity extends AppCompatActivity implements GetAllDataToActivity {
    private static final String TAG = "AddReviewActivity";
    private ActivityAddReviewBinding binding;
    int pattern=0;
    String myUid,myImg,myName,myEmail;

    MaterialToolbar toolbar;
    ActionBar actionBar;

    FirebaseDatabase database;
    DatabaseReference ref;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;

    FirebaseStorage storage;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddReviewBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        initView();
        checkUserStatus();

        loadUser();
        checkPattern();

        binding.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pattern+=1;
                checkPattern();
            }
        });
        binding.btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pattern-=1;
                checkPattern();
            }
        });
    }

    private void checkPattern() {
        if(pattern<0){
            pattern=2;
        }else if(pattern >3){
            pattern=0;
        }

        switch(pattern){
            case 0:
                loadPatternNoImg();
                binding.txtDisplayDescript.setText("( No image )");
                break;
            case 1:
                loadPatternOneImg();
                binding.txtDisplayDescript.setText("( 1 Image )");
                break;
//            case 2:
//                loadPatternHorizontalImg();
//                binding.txtDisplayDescript.setText("( Pair Images )");
//                break;
            case 2:
                loadPatternVerticalImg();
                binding.txtDisplayDescript.setText("( Discription of each Images )");
                break;
            default:
                loadPatternNoImg();
                break;
        }
    }

    private void loadPatternVerticalImg() {
        AddReviewImageVertitcalFragment frag=new AddReviewImageVertitcalFragment();
        FragmentTransaction tr=getSupportFragmentManager().beginTransaction();
        tr.replace(R.id.layoutAddReview,frag,"").commit();
    }

    private void loadPatternHorizontalImg() {
        AddReviewImageHorizontalFragment frag=new AddReviewImageHorizontalFragment();
        FragmentTransaction tr=getSupportFragmentManager().beginTransaction();
        tr.replace(R.id.layoutAddReview,frag,"").commit();
    }

    private void loadPatternOneImg() {
        AddReviewOneImageFragment frag=new AddReviewOneImageFragment();
        FragmentTransaction tr=getSupportFragmentManager().beginTransaction();
        tr.replace(R.id.layoutAddReview,frag,"").commit();
    }

    private void loadPatternNoImg() {
        AddReviewNoImageFragment frag=new AddReviewNoImageFragment();
        FragmentTransaction tr=getSupportFragmentManager().beginTransaction();
        tr.replace(R.id.layoutAddReview,frag,"").commit();
    }
    private void initView() {
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        firebaseAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Uploading ...");
    }
    private void loadUser() {
        ref=database.getReference("Users").child(myUid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser modelUser=snapshot.getValue(ModelUser.class);
                myName=modelUser.getName();
                myImg=modelUser.getImage();
                myEmail=modelUser.getEmail();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void checkUserStatus(){
        //get current user
        user= firebaseAuth.getCurrentUser();
        if(user !=null){
            myUid=user.getUid();
        }else{
            //go back to login
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    public void uploadReviewWithNoImage(String title, String descrip, String tag, String collection) {
        progressDialog.show();

        ref=database.getReference("Reviews");
        String timeStamp=String.valueOf(System.currentTimeMillis());

        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("rId",timeStamp);
        hashMap.put("r_title",title);
        hashMap.put("r_tag",tag);
        hashMap.put("r_type","pattern1");
        hashMap.put("r_num",String.valueOf(0));
        hashMap.put("r_collection",collection);
        hashMap.put("r_desc0",descrip);
        hashMap.put("r_timeStamp",timeStamp);
        hashMap.put("r_point","0");
        hashMap.put("uId",myUid);
        hashMap.put("uImg",myImg);
        hashMap.put("uName",myName);
        hashMap.put("uEmail",myEmail);

        ref.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                onBackPressed();
                finish();
            }
        });

    }

    @Override
    public void uploadReviewWithOneImage(String title, String descrip, String tag, String collection, Uri image_uri) {
        progressDialog.show();
//        Log.d(TAG, "uploadReviewWithOneImage: UriExample::"+image_uri);
        String timeStamp=String.valueOf(System.currentTimeMillis());

        String filePathAndName="Review_image/"+user.getUid()+"_"+timeStamp;
        StorageReference s_ref = storage.getReference().child(filePathAndName);
        s_ref.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = s_ref.putFile(image_uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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

                            ref=database.getReference("Reviews");

                            HashMap<String,String> hashMap=new HashMap<>();
                            hashMap.put("rId",timeStamp);
                            hashMap.put("r_title",title);
                            hashMap.put("r_tag",tag);
                            hashMap.put("r_type","pattern2");
                            hashMap.put("r_num",String.valueOf(1));
                            hashMap.put("r_collection",collection);
                            hashMap.put("r_image0",String.valueOf(downloadUri));
                            hashMap.put("r_desc0",descrip);
                            hashMap.put("r_timeStamp",timeStamp);
                            hashMap.put("r_point","0");
                            hashMap.put("uId",myUid);
                            hashMap.put("uImg",myImg);
                            hashMap.put("uName",myName);
                            hashMap.put("uEmail",myEmail);

                            ref.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressDialog.dismiss();
                                    onBackPressed();
                                    finish();
                                }
                            });


                        }
                    }
                });
            }
        });

    }

    @Override
    public void uploadReviewWithVerticalImage(String title, ArrayList<String> allDescrip, ArrayList<Uri> allImageUri, String tag, String collection) {
        progressDialog.show();

        int numOfImage=allImageUri.size();
        ArrayList<Uri>imageLinks=new ArrayList<>();
        int count=0;

        for(int i=0;i<numOfImage;i++){
            Uri image_uri=allImageUri.get(i);
            String timeStamp=String.valueOf(System.currentTimeMillis());

            Log.d(TAG, "uploadReviewWithVerticalImage: imageUri::"+image_uri);

            String filePathAndName="Review_image/"+user.getUid()+"_"+timeStamp;
            StorageReference s_ref = storage.getReference().child(filePathAndName);
            s_ref.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = s_ref.putFile(image_uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                                Log.d(TAG, "onComplete new: img Uri::" + downloadUri);
                                imageLinks.add(downloadUri);

                                if(checkNumberOfImageLinks(numOfImage,imageLinks)){
//                                    progressDialog.dismiss();
                                    uploadDataToSever(title,allDescrip,imageLinks,tag,collection);
                                    Log.d(TAG, "uploadReviewWithVerticalImage: total::"+imageLinks.toString());
                                }
                            }
                        }
                    });
                }
            });
        }


    }

    private boolean checkNumberOfImageLinks(int numberOfImage,ArrayList<Uri>imageLinks){
        int linksize=imageLinks.size();

        if(numberOfImage==linksize){
            return true;
        }

        return false;
    }
    private void uploadDataToSever(String title, ArrayList<String> allDescrip,ArrayList<Uri>imageLinks, String tag, String collection){
        String timeStamp=String.valueOf(System.currentTimeMillis());

        ref=database.getReference("Reviews");

        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("rId",timeStamp);
        hashMap.put("r_title",title);
        hashMap.put("r_tag",tag);
        hashMap.put("r_type","pattern3");
        hashMap.put("r_num",String.valueOf(imageLinks.size()));
        hashMap.put("r_collection",collection);
        hashMap.put("r_timeStamp",timeStamp);
        hashMap.put("r_point","0");
        hashMap.put("uId",myUid);
        hashMap.put("uImg",myImg);
        hashMap.put("uName",myName);
        hashMap.put("uEmail",myEmail);

        for(int i=0;i<imageLinks.size();i++){
            hashMap.put("r_image"+i,String.valueOf(imageLinks.get(i)));
            hashMap.put("r_desc"+i,allDescrip.get(i));
        }

        ref.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                onBackPressed();
                finish();
            }
        });
    }
}