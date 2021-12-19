package com.example.wongwien;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.wongwien.databinding.ActivityQuesAnsDetailBinding;
import com.example.wongwien.databinding.ActivityReviewDetailBinding;
import com.example.wongwien.fragment.question.LoadListShowFragment;
import com.example.wongwien.fragment.show_review.Pattern1Fragment;
import com.example.wongwien.fragment.show_review.Pattern2Fragment;
import com.example.wongwien.fragment.show_review.Pattern3Fragment;
import com.example.wongwien.model.ModelReview;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ReviewDetailActivity extends AppCompatActivity {
    private static final String TAG = "ReviewDetailActivity";
    private ActivityReviewDetailBinding binding;

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    ModelReview review;
    String myUid;
    String rUid;
    String qId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityReviewDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initview();

        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra("bundle");
        review=bundle.getParcelable("list");

        Log.d(TAG, "onCreate: review model*****::"+review.toString());

        loadReviewPost();

    }

    private void loadReviewPost() {
        switch(review.getR_type()){
            case "pattern1":
                Pattern1Fragment frag=new Pattern1Fragment();
                Bundle bundle=new Bundle();
                bundle.putParcelable("list",review);
                frag.setArguments(bundle);
                try {
                    FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
                    tr.replace(R.id.fragReview, frag, "").commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case "pattern2":
                Pattern2Fragment frag2=new Pattern2Fragment();
                bundle=new Bundle();
                bundle.putParcelable("list",review);
                frag2.setArguments(bundle);
                try {
                    FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
                    tr.replace(R.id.fragReview, frag2, "").commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case "pattern3":
                Pattern3Fragment frag3=new Pattern3Fragment();
                bundle=new Bundle();
                bundle.putParcelable("list",review);
                frag3.setArguments(bundle);
                try {
                    FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
                    tr.replace(R.id.fragReview, frag3, "").commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void initview() {
        firebaseAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
    }

    private void checkUserStatus() {
        //get current user
        user = firebaseAuth.getCurrentUser();
        if (user != null) {
            myUid = user.getUid();

        } else {
            //go back to login
            startActivity(new Intent(this, SplashActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        if (rUid.equals(myUid)) {
//            MenuInflater inflater = getMenuInflater();
//            inflater.inflate(R.menu.menu_post, menu);
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_remove:
//                ref=database.getReference("Reviews").child(qId);
//                ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        onBackPressed();
//                        finish();
//                    }
//                });
//                return true;
//            case R.id.action_edit:
//                Intent intent = new Intent(QuesAnsDetailActivity.this, AddQuestionActivity.class);
//                intent.putExtra("qId", qId);
//                startActivity(intent);
//
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
}