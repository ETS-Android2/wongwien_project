package com.example.wongwien;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.wongwien.fragment.ProfileFragment;
import com.example.wongwien.fragment.QuestionAnsFragment;
import com.example.wongwien.fragment.ReviewsFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private FirebaseUser user;

    String myUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initView();

    }

    private void initView() {
        database=FirebaseDatabase.getInstance();

        //actionbar
        ActionBar actionbar=getSupportActionBar();

        actionbar.setTitle("Reviews");
//        actionbar.setDisplayHomeAsUpEnabled(true);
//        actionbar.setDisplayShowHomeEnabled(true);

        //set fragment transaction(default)
        ReviewsFragment reviewsFragment=new ReviewsFragment();
        FragmentTransaction fr1=getSupportFragmentManager().beginTransaction();
        fr1.replace(R.id.container,reviewsFragment,"");
        fr1.commit();

        //bottom nav
        BottomNavigationView navigationView=findViewById(R.id.navigation_bottom);
        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull  MenuItem item) {
                switch(item.getItemId()){
                    case R.id.action_review:
                        actionbar.setTitle("Reviews");
                        ReviewsFragment reviewsFragment=new ReviewsFragment();
                        FragmentTransaction fr1=getSupportFragmentManager().beginTransaction();
                        fr1.replace(R.id.container,reviewsFragment,"");
                        fr1.commit();
                        return true;
                    case R.id.action_question:
                        actionbar.setTitle("Reviews");
                        QuestionAnsFragment questionAnsFragment=new QuestionAnsFragment();
                        FragmentTransaction fr2=getSupportFragmentManager().beginTransaction();
                        fr2.replace(R.id.container,questionAnsFragment,"");
                        fr2.commit();
                        return true;
                    case R.id.action_profile:
                        actionbar.setTitle("Reviews");
                        ProfileFragment profileFragment=new ProfileFragment();
                        FragmentTransaction fr3=getSupportFragmentManager().beginTransaction();
                        fr3.replace(R.id.container,profileFragment,"");
                        fr3.commit();
                        return true;
                }
                return false;
            }
        });

        //firebase Auth
        firebaseAuth =FirebaseAuth.getInstance();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    private void checkUserStatus(){
        //get current user
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if(user !=null){
            //get user
            myUid=user.getUid();
        }else{
            //go back to login
            startActivity(new Intent(MainActivity.this,WelcomeActivity.class));
            finish();
        }
    }
    private void checkOnlineStatus(String status){
        DatabaseReference onlineRef=database.getReference("Users").child(myUid);

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);

        onlineRef.getRef().updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        String timeStamp=String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timeStamp);
        super.onPause();
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}