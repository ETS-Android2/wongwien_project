package com.example.wongwien;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wongwien.adapter.AdapterComment;
import com.example.wongwien.adapter.AdapterReview;
import com.example.wongwien.databinding.ActivityQuesAnsDetailBinding;
import com.example.wongwien.databinding.ActivityReviewDetailBinding;
import com.example.wongwien.fragment.question.LoadListShowFragment;
import com.example.wongwien.fragment.show_review.Pattern1Fragment;
import com.example.wongwien.fragment.show_review.Pattern2Fragment;
import com.example.wongwien.fragment.show_review.Pattern3Fragment;
import com.example.wongwien.model.ModelComment;
import com.example.wongwien.model.ModelMylocation;
import com.example.wongwien.model.ModelReview;
import com.example.wongwien.model.ModelUser;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ReviewDetailActivity extends AppCompatActivity {
    private static final String TAG = "ReviewDetailActivity";
    private ActivityReviewDetailBinding binding;
    ActionBar actionBar;

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    ModelReview review;
    ArrayList<ModelComment> comments;
    AdapterComment adapterComment;

    String myUid;
    String rUid;
    String rId;
    String myName,myImage;

    int star,point,starBeforeChange;
    boolean isUseReview;

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

        loadUser();
        checkUserStatus();
        loadReviewPost();
        loadComment();
        checkReviewPointStatus(review.getrId());

        Log.d(TAG, "onCreate: Myuid::"+myUid);

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = binding.edComment.getText().toString().trim();
                if (!TextUtils.isEmpty(comment)) {
                    addCommment(comment);

                    hideKeyboard(ReviewDetailActivity.this);
                }
            }
        });

        binding.score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ReviewDetailActivity.this, R.style.CustomAlertDialog);
                View view= LayoutInflater.from(ReviewDetailActivity.this).inflate(R.layout.dialog_review_score,null,false);

                checkPaticipation(review.getrId(),myUid,view);

                builder.setView(view);

                AlertDialog show=builder.show();

                ImageView star1=view.findViewById(R.id.star1);
                ImageView star2=view.findViewById(R.id.star2);
                ImageView star3=view.findViewById(R.id.star3);
                ImageView star4=view.findViewById(R.id.star4);
                ImageView star5=view.findViewById(R.id.star5);


                star1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        star=1;
                        checkStatusStar(view);
                    }
                });
                star2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        star=2;
                        checkStatusStar(view);
                    }
                });
                star3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        star=3;
                        checkStatusStar(view);
                    }
                });
                star4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        star=4;
                        checkStatusStar(view);
                    }
                });
                star5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        star=5;
                        checkStatusStar(view);
                    }
                });

                Button btnSend=view.findViewById(R.id.btnSend);
                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("RParticipations").child(review.getrId());
                        ref.child(myUid).setValue(String.valueOf(star));

                        calculateScore(review,star);

                        binding.star.setImageResource(R.drawable.ic_star_primary);
                        show.dismiss();
                    }
                });
            }
        });
    }
    private void calculateScore(ModelReview review, int star){

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Reviews");
        Query q=ref.orderByChild("rId").equalTo(review.getrId());
        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot d:snapshot.getChildren()){
                    ModelReview model=d.getValue(ModelReview.class);
                    point=model.getR_point();


                    Log.d(TAG, "calculateScore: star::"+star);
                    Log.d(TAG, "calculateScore: beforechange::"+starBeforeChange);
                    Log.d(TAG, "calculateScore: point::"+point);
                    Log.d(TAG, "calculateScore: isUseReview::"+isUseReview);

                    if(isUseReview){
                        point=point+(star-starBeforeChange);
                    }else{
                        point=point+star;
                    }
                    int point2=point;

                    Log.d(TAG, "calculateScore: point afterchange::"+point);
                    Log.d(TAG, "calculateScore:***********************");

                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Reviews");
                    Query q=ref.orderByChild("rId").equalTo(review.getrId());
                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot d:snapshot.getChildren()){
                                ModelReview model=d.getValue(ModelReview.class);
                                if(model.getrId().equals(review.getrId())){
                                    d.getRef().child("r_point").setValue(point2);
                                    loadReviewFromDatabase();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void loadUser() {
        ref = database.getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    ModelUser model = d.getValue(ModelUser.class);
                    if (model.getUid().equals(myUid)) {
                        myName = model.getName();
                        myImage = model.getImage();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void checkPaticipation(String rId,String myUid,View view){
        try{
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("RParticipations").child(rId).child(myUid);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String num=snapshot.getValue(String.class);
                    if(num!=null){
                        isUseReview=true;
                        starBeforeChange=Integer.parseInt(num);
                        star=Integer.parseInt(num);
                        checkStatusStar(view);
                        Log.d(TAG, "onDataChange::"+rId+":: star:::"+star);
                    }else{
                        star=0;
                        isUseReview=false;
                        Log.d(TAG, "onDataChange::"+rId+":: star:::"+star);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: notfound");
                }
            });
        }catch (Exception e){
            star=0;
            isUseReview=false;
            starBeforeChange=0;
            checkStatusStar(view);
        }
    }
    private void checkReviewPointStatus(String rId){
        Log.d(TAG, "checkReviewPointStatus: rId::"+rId);
        Log.d(TAG, "checkReviewPointStatus: myUid::"+myUid);
        try{
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("RParticipations").child(rId).child(myUid);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String num=snapshot.getValue(String.class);
                    if(num!=null){
                        isUseReview=true;
                        binding.star.setImageResource(R.drawable.ic_star_primary);
                        Log.d(TAG, "checkReviewPointStatus: change");
                    }else{
                        isUseReview=false;
                        Log.d(TAG, "checkReviewPointStatus: not");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: notfound");
                }
            });
        }catch (Exception e){
            isUseReview=false;
        }
    }
    private void checkStatusStar(View view) {
        ImageView star1=view.findViewById(R.id.star1);
        ImageView star2=view.findViewById(R.id.star2);
        ImageView star3=view.findViewById(R.id.star3);
        ImageView star4=view.findViewById(R.id.star4);
        ImageView star5=view.findViewById(R.id.star5);

        switch (star){
            case 1:
                star1.setImageResource(R.drawable.ic_star_white);
                star2.setImageResource(R.drawable.ic_star_empty);
                star3.setImageResource(R.drawable.ic_star_empty);
                star4.setImageResource(R.drawable.ic_star_empty);
                star5.setImageResource(R.drawable.ic_star_empty);
                break;
            case 2:
                star1.setImageResource(R.drawable.ic_star_white);
                star2.setImageResource(R.drawable.ic_star_white);
                star3.setImageResource(R.drawable.ic_star_empty);
                star4.setImageResource(R.drawable.ic_star_empty);
                star5.setImageResource(R.drawable.ic_star_empty);
                break;
            case 3:
                star1.setImageResource(R.drawable.ic_star_white);
                star2.setImageResource(R.drawable.ic_star_white);
                star3.setImageResource(R.drawable.ic_star_white);
                star4.setImageResource(R.drawable.ic_star_empty);
                star5.setImageResource(R.drawable.ic_star_empty);
                break;
            case 4:
                star1.setImageResource(R.drawable.ic_star_white);
                star2.setImageResource(R.drawable.ic_star_white);
                star3.setImageResource(R.drawable.ic_star_white);
                star4.setImageResource(R.drawable.ic_star_white);
                star5.setImageResource(R.drawable.ic_star_empty);
                break;
            case 5:
                star1.setImageResource(R.drawable.ic_star_white);
                star2.setImageResource(R.drawable.ic_star_white);
                star3.setImageResource(R.drawable.ic_star_white);
                star4.setImageResource(R.drawable.ic_star_white);
                star5.setImageResource(R.drawable.ic_star_white);
                break;
            default:
                star1.setImageResource(R.drawable.ic_star_empty);
                star2.setImageResource(R.drawable.ic_star_empty);
                star3.setImageResource(R.drawable.ic_star_empty);
                star4.setImageResource(R.drawable.ic_star_empty);
                star5.setImageResource(R.drawable.ic_star_empty);
                break;
        }
    }
    private void loadComment() {
        comments = new ArrayList<>();

        ref = database.getReference("Reviews").child(rId).child("Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    ModelComment comment = d.getValue(ModelComment.class);
                    comments.add(comment);
                }
                adapterComment = new AdapterComment(comments, ReviewDetailActivity.this);
                binding.rcComment.setHasFixedSize(true);
                binding.rcComment.setLayoutManager(new LinearLayoutManager(ReviewDetailActivity.this));
                binding.rcComment.setAdapter(adapterComment);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

    private void addCommment(String comment) {
        ref = database.getReference("Reviews");
        Query query = ref.orderByChild("rId").equalTo(rId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    String timeStamp = String.valueOf(System.currentTimeMillis());

                    HashMap<String, String> hash = new HashMap<>();
                    hash.put("cId", timeStamp);
                    hash.put("cUid", myUid);
                    hash.put("cImage",myImage);
                    hash.put("cName", myName);
                    hash.put("comment", comment);
                    hash.put("timeStamp", timeStamp);

                    d.getRef().child("Comments").child(timeStamp).setValue(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            binding.edComment.clearFocus();
                            binding.edComment.setText("");

                            hideKeyboard(ReviewDetailActivity.this);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ReviewDetailActivity.this, "something error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                review=snapshot.getValue(ModelReview.class);
                loadReviewPost(review);

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
                        Intent intent=new Intent(ReviewDetailActivity.this,MainActivity.class);
                        intent.putExtra("refresh","review");
                        startActivity(intent);

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