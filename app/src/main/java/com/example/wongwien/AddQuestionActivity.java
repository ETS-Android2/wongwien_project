package com.example.wongwien;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wongwien.databinding.ActivityAddQuestionBinding;
import com.example.wongwien.model.ModelQuestionAns;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class AddQuestionActivity extends AppCompatActivity {
    private static final String TAG = "AddQuestionActivity";
    private ActivityAddQuestionBinding binding;

    MaterialToolbar toolbar;
    ActionBar actionBar;
    String myUid,myImg,myName,myEmail;
    String tag;
    String qId="";

    FirebaseDatabase database;
    DatabaseReference ref;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityAddQuestionBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        initView();

        checkUserStatus();

        Intent intent=getIntent();
        if(null != intent){
            qId=intent.getStringExtra("qId");
            if(qId!=null && !TextUtils.isEmpty(qId)){
                Log.d(TAG, "onCreate: QId"+qId);
                loadPost();
            }
        }

        binding.txtAddtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(binding.showTag.getVisibility()==View.VISIBLE){
                    binding.showTag.setVisibility(View.GONE);
                }else{
                    binding.edTag.requestFocus();
                    binding.showTag.setVisibility(View.VISIBLE);
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

                    hideKeyboard(AddQuestionActivity.this);

                }
            }
        });

        loadUser();

        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question=binding.edQuestion.getText().toString().trim();
                String descrip=binding.edDescription.getText().toString().trim();
                tag=getTag();
                String collection=binding.spinnerCollection.getSelectedItem().toString();

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

    private void loadPost() {
        ref=database.getReference("QuestionAns");
        Query query=ref.orderByChild("qId").equalTo(qId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for(DataSnapshot d:snapshot.getChildren()){
                    ModelQuestionAns model=d.getValue(ModelQuestionAns.class);

                    if(model!=null){
                        binding.edQuestion.setText(model.getQuestion());
                        binding.edDescription.setText(model.getDescrip());

                        String alltag=model.getTag();
                        if(alltag!=null){
                            try{
                            String tags[] = alltag.split("::");
                            for (String s : tags) {
                                if(!s.equals("")){
                                    addToChipGroup(s, binding.chipGroup);
                                }
                            }

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        String collection=model.getCollection();
                        int category;
                        switch (collection){
                            case "General":
                                category=0;
                                break;
                            case "Course":
                                category=1;
                                break;
                            case "Food":
                                category=2;
                                break;
                            case "Domitory":
                                category=3;
                                break;
                            case "Tours":
                                category=4;
                                break;
                            default:
                                category=0;
                                break;

                        }
                        binding.spinnerCollection.setSelection(category);
                        binding.btnPost.setText("Update");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
        hashMap.put("point","0");
        hashMap.put("uImg",myImg);
        hashMap.put("uName",myName);
        hashMap.put("uEmail",myEmail);
        hashMap.put("qId",timeStamp);


        if(binding.btnPost.getText().equals("Update")){
            ref=database.getReference("QuestionAns").child(qId);
            ref.child("question").setValue(question);
            ref.child("descrip").setValue(descrip);
            ref.child("tag").setValue(tag);
            ref.child("collection").setValue(collection).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    onBackPressed();
                    finish();
                }
            });
        }else{
            ref = database.getReference("QuestionAns");
            ref.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    Intent intent=new Intent(AddQuestionActivity.this,MainActivity.class);
                    intent.putExtra("refresh","question");
                    startActivity(intent);

                    onBackPressed();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

        }
    }

    private void loadUser() {
        ref=database.getReference("Users").child(myUid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                ModelUser modelUser=snapshot.getValue(ModelUser.class);
                myName=modelUser.getName();
                myImg=modelUser.getImage();
                myEmail=modelUser.getEmail();
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    private void addToChipGroup(String tag, ChipGroup chipGroup) {
        Chip chip = new Chip(this);
        ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(this,
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

    private String getTag() {
        String txtTag="";
        for(int i=0;i<binding.chipGroup.getChildCount();i++){
            txtTag=txtTag+"::"+((Chip) binding.chipGroup.getChildAt(i)).getText().toString();
            Log.d(TAG, "printChipValue: chip::"+((Chip) binding.chipGroup.getChildAt(i)).getText().toString());
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

        tag="";

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.collections, R.layout.spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCollection.setAdapter(adapter);

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
}