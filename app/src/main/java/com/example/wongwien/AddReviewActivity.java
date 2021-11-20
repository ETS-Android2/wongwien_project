package com.example.wongwien;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.wongwien.databinding.ActivityAddReviewBinding;
import com.example.wongwien.fragment.add_review.AddReviewImageHorizontalFragment;
import com.example.wongwien.fragment.add_review.AddReviewImageVertitcalFragment;
import com.example.wongwien.fragment.add_review.AddReviewNoImageFragment;
import com.example.wongwien.fragment.add_review.AddReviewOneImageFragment;
import com.example.wongwien.fragment.add_review.GetAllDataToActivity;
import com.example.wongwien.model.ModelUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
            pattern=0;
        }else if(pattern >3){
            pattern=3;
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
            case 2:
                loadPatternHorizontalImg();
                binding.txtDisplayDescript.setText("( Pair Images )");
                break;
            case 3:
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
        ref=database.getReference("Reviews");
        String timeStamp=String.valueOf(System.currentTimeMillis());

        HashMap<String,String> hashMap=new HashMap<>();
        hashMap.put("title",title);
        hashMap.put("descrip",descrip);
        hashMap.put("tag",tag);
        hashMap.put("collection",collection);
        hashMap.put("timeStamp",timeStamp);
        hashMap.put("point","0");
        hashMap.put("uId",myUid);
        hashMap.put("uImg",myImg);
        hashMap.put("uName",myName);
        hashMap.put("uEmail",myEmail);
        hashMap.put("rId",timeStamp);

        ref.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                onBackPressed();
                finish();
            }
        });

    }
}