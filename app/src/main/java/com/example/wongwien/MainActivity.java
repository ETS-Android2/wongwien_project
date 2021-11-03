package com.example.wongwien;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.wongwien.fragment.ProfileFragment;
import com.example.wongwien.fragment.QuesAnsFragment;
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

    boolean doubleBackToExitPressedOnce = false;
    String myUid="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        Log.d(TAG, "onCreate: usr:::"+myUid);

    }

    private void initView() {
        //firebase Auth
        firebaseAuth =FirebaseAuth.getInstance();
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
                        actionbar.setTitle("Question-Ans");
                    QuesAnsFragment questionAnsFragment=new QuesAnsFragment();
                        FragmentTransaction fr2=getSupportFragmentManager().beginTransaction();
                        fr2.replace(R.id.container,questionAnsFragment,"");
                        fr2.commit();
                        return true;
                    case R.id.action_profile:
                        actionbar.setTitle("Profile");
                        ProfileFragment profileFragment=new ProfileFragment();
                        FragmentTransaction fr3=getSupportFragmentManager().beginTransaction();
                        fr3.replace(R.id.container,profileFragment,"");
                        fr3.commit();
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
    private void checkUserStatus(){
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //get user
            myUid=user.getUid();
            Log.d(TAG, "checkUserStatus: User"+myUid);

            try{
                WelcomeActivity.welcomeActivity.finish();
            }catch (Exception e){
                e.printStackTrace();
            }

        }else{
            //go back to login
            startActivity(new Intent(MainActivity.this,SplashActivity.class));
            finish();
        }
    }
    private void checkOnlineStatus(String status){
        if(myUid != null ||!myUid.equals("")){
            DatabaseReference onlineRef = database.getReference("Users").child(myUid);

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("status", status);

            onlineRef.getRef().updateChildren(hashMap);
        }

    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        checkUserStatus();
        checkOnlineStatus("online");
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        String timeStamp=String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timeStamp);
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        checkUserStatus();

        checkOnlineStatus("online");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        String timeStamp=String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timeStamp);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}