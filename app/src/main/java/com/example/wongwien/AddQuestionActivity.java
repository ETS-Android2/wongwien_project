package com.example.wongwien;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wongwien.model.ModelUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddQuestionActivity extends AppCompatActivity {
    private static final String TAG = "AddQuestionActivity";
    private Spinner spinnerCollection;
    private EdittextV2 edTag,edDescription,edQuestion;
    private Button btnAdd,btnPost;
    private ChipGroup chipGroup;
    private TextView txtAddtag;
    private LinearLayout showTag;

    MaterialToolbar toolbar;
    ActionBar actionBar;
    String myUid,myImg,myName;
    String tag;

    FirebaseDatabase database;
    DatabaseReference ref;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_question);

        initView();
        checkUserStatus();

        txtAddtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showTag.getVisibility()==View.VISIBLE){
                    showTag.setVisibility(View.GONE);
                }else{
                    edTag.requestFocus();
                    showTag.setVisibility(View.VISIBLE);
                }
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag=edTag.getText().toString().trim();
                if(!TextUtils.isEmpty(tag)){
                    addToChipGroup(tag,chipGroup);
                    edTag.setText("");
                }
            }
        });

        loadUser();

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question=edQuestion.getText().toString().trim();
                String descrip=edDescription.getText().toString().trim();
                tag=getTag();
                String collection=spinnerCollection.getSelectedItem().toString();

                if(!TextUtils.isEmpty(question)){
                    if(TextUtils.isEmpty(descrip)){
                       descrip="";
                    }
                    if(TextUtils.isEmpty(tag)){
                        tag="";
                    }

                    uploadQuestion(question,descrip,tag,collection);
                }else{
                    Toast.makeText(AddQuestionActivity.this, "Question required", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void uploadQuestion(String question, String descrip, String tag, String collection) {
        String timeStamp=String.valueOf(System.currentTimeMillis());

        HashMap<String,String>hashMap=new HashMap<>();
        hashMap.put("question",question);
        hashMap.put("descrip",descrip);
        hashMap.put("tag",tag);
        hashMap.put("collection",collection);
        hashMap.put("timeStamp",timeStamp);
        hashMap.put("uId",myUid);
        hashMap.put("uImg",myImg);
        hashMap.put("uName",myName);

        ref=database.getReference("QuestionAns");
        ref.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {

            }
        });
    }

    private void loadUser() {
        ref=database.getReference("Users").child(myUid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                ModelUser modelUser=snapshot.getValue(ModelUser.class);
                myName=modelUser.getName();
                myImg=modelUser.getImage();
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    private void addToChipGroup(String tag, ChipGroup chipGroup) {
        Chip chip=new Chip(AddQuestionActivity.this,null,R.style.chipCustom);
        chip.setChipText(tag);
        chip.setCloseIconEnabled(true);
        chip.setClickable(false);
        chip.setCheckable(false);
        chipGroup.addView(chip);

        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chipGroup.removeView(chip);
            }
        });
//        printChipValue(chipGroup);
    }

    private String getTag() {
        String txtTag="";
        for(int i=0;i<chipGroup.getChildCount();i++){
            txtTag=txtTag+"::"+((Chip) chipGroup.getChildAt(i)).getText().toString();
            Log.d(TAG, "printChipValue: chip::"+((Chip) chipGroup.getChildAt(i)).getText().toString());
        }
            return txtTag;
    }

    private void initView() {
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        firebaseAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        spinnerCollection=findViewById(R.id.spinnerCollection);
        btnAdd=findViewById(R.id.btnAdd);
        edTag=findViewById(R.id.edTag);
        chipGroup=findViewById(R.id.chipGroup);
        showTag=findViewById(R.id.showTag);
        txtAddtag=findViewById(R.id.txtAddtag);
        edQuestion=findViewById(R.id.edTitle);
        edDescription=findViewById(R.id.edDescription);
        btnPost=findViewById(R.id.btnPost);
        tag="";

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.collections, R.layout.spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCollection.setAdapter(adapter);

    }
    private void checkUserStatus(){
        //get current user
        user= firebaseAuth.getCurrentUser();
        if(user !=null){
            myUid=user.getUid();
        }else{
            //go back to login
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        }
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
}