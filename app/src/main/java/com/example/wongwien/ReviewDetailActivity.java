package com.example.wongwien;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReviewDetailActivity extends AppCompatActivity {
    private static final String TAG = "ReviewDetailActivity";
    private ActivityReviewDetailBinding binding;
    ActionBar actionBar;

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    ModelReview review;
    String myUid;
    String rUid;
    String rId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityReviewDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initview();

        Intent intent=getIntent();
        Bundle bundle=intent.getBundleExtra("bundle");
        review=bundle.getParcelable("list");

        rUid=review.getuId();
        rId=review.getrId();

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
    private void loadReviewPost(ModelReview review) {
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
    private void loadReviewFromDatabase(){
        ref=database.getReference("Reviews").child(rId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    ModelReview model=snapshot.getValue(ModelReview.class);
                    loadReviewPost(model);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initview() {
        firebaseAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        actionBar = getSupportActionBar();
        actionBar.setTitle("Review Details");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
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

    @Override
    protected void onRestart() {
        loadReviewFromDatabase();
        super.onRestart();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (rUid.equals(myUid)) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_post, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_remove:
                ref=database.getReference("Reviews").child(rId);
                ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        onBackPressed();
                        finish();
                    }
                });
                loadReviewPost();

                return true;
            case R.id.action_edit:
                Intent intent = new Intent(ReviewDetailActivity.this, AddReviewActivity.class);
                Bundle bundle=new Bundle();
                bundle.putParcelable("list",review);
                bundle.putString("rId",rId);
                bundle.putString("rtype",review.getR_type());
                intent.putExtras(bundle);
                startActivity(intent);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}