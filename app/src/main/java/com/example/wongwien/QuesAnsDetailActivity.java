package com.example.wongwien;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.wongwien.adapter.AdapterComment;
import com.example.wongwien.databinding.ActivityQuesAnsDetailBinding;
import com.example.wongwien.model.ModelComment;
import com.example.wongwien.model.ModelQuestionAns;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class QuesAnsDetailActivity extends AppCompatActivity {
    private static final String TAG = "QuesAnsDetailActivity";
    ActionBar actionBar;
    AdapterComment adapterComment;
    String myUid, myImage, myName;
    String qId, qUid;
    String mark = "none";
    String tag = "";
    List<ModelComment> comments;
    private @NonNull
    ActivityQuesAnsDetailBinding binding;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuesAnsDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        checkUserStatus();

        Intent intent = getIntent();
        if (intent != null) {
            qId = intent.getStringExtra("qId");
            qUid = intent.getStringExtra("qUid");
        }

        loadUser();
        loadPost();
        checkParticipation();
        loadComment();

        binding.btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mark.equals("markUp")) {
                    ref = database.getReference("QParticipations").child(qId).child(myUid);
                    ref.removeValue();

                    ref = database.getReference("QuestionAns");
                    Query query = ref.orderByChild("qId").equalTo(qId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot d : snapshot.getChildren()) {
                                ModelQuestionAns model = d.getValue(ModelQuestionAns.class);
                                Integer point = Integer.parseInt(model.getPoint());
                                point -= 1;

                                d.getRef().child("point").setValue(String.valueOf(point));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else if (mark.equals("markDown")) {
                    ref = database.getReference("QParticipations").child(qId).child(myUid);
                    ref.setValue("markUp");
                    ref = database.getReference("QuestionAns");

                    Query query = ref.orderByChild("qId").equalTo(qId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot d : snapshot.getChildren()) {
                                ModelQuestionAns model = d.getValue(ModelQuestionAns.class);
                                Integer point = Integer.parseInt(model.getPoint());
                                point += 1;

                                d.getRef().child("point").setValue(String.valueOf(point));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    ref = database.getReference("QuestionAns");
                    Query query = ref.orderByChild("qId").equalTo(qId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot d : snapshot.getChildren()) {
                                ModelQuestionAns model = d.getValue(ModelQuestionAns.class);
                                Integer point = Integer.parseInt(model.getPoint());
                                point += 1;

                                d.getRef().child("point").setValue(String.valueOf(point));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    DatabaseReference ref2 = database.getReference("QParticipations");
                    ref2.child(qId).child(myUid).setValue("markUp");
                }
            }
        });
        binding.btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mark.equals("markDown")) {
                    ref = database.getReference("QParticipations").child(qId).child(myUid);
                    ref.removeValue();

                    ref = database.getReference("QuestionAns");
                    Query query = ref.orderByChild("qId").equalTo(qId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot d : snapshot.getChildren()) {
                                ModelQuestionAns model = d.getValue(ModelQuestionAns.class);
                                Integer point = Integer.parseInt(model.getPoint());
                                point += 1;

                                d.getRef().child("point").setValue(String.valueOf(point));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else if (mark.equals("markUp")) {
                    ref = database.getReference("QParticipations").child(qId).child(myUid);
                    ref.setValue("markDown");

                    ref = database.getReference("QuestionAns");
                    Query query = ref.orderByChild("qId").equalTo(qId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot d : snapshot.getChildren()) {
                                ModelQuestionAns model = d.getValue(ModelQuestionAns.class);
                                Integer point = Integer.parseInt(model.getPoint());
                                point -= 1;

                                d.getRef().child("point").setValue(String.valueOf(point));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    ref = database.getReference("QuestionAns");
                    Query query = ref.orderByChild("qId").equalTo(qId);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot d : snapshot.getChildren()) {
                                ModelQuestionAns model = d.getValue(ModelQuestionAns.class);
                                Integer point = Integer.parseInt(model.getPoint());
                                point -= 1;

                                d.getRef().child("point").setValue(String.valueOf(point));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    DatabaseReference ref2 = database.getReference("QParticipations");
                    ref2.child(qId).child(myUid).setValue("markDown");
                }
            }
        });
        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = binding.edComment.getText().toString().trim();
                if (!TextUtils.isEmpty(comment)) {
                    addCommment(comment);

                    hideKeyboard(QuesAnsDetailActivity.this);
                }
            }
        });
    }

    private void checkParticipation() {
        ref = database.getReference("QParticipations").child(qId).child(myUid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mark = snapshot.getValue(String.class);
                if (mark != null) {
                    if (mark.equals("markUp")) {
                        binding.btnUp.setImageResource(R.drawable.ic_up_primary);
                        binding.btnDown.setImageResource(R.drawable.ic_down_secound_primary);
                    } else if (mark.equals("markDown")) {
                        binding.btnDown.setImageResource(R.drawable.ic_down_primary);
                        binding.btnUp.setImageResource(R.drawable.ic_up_secound_primary);
                    } else {
                        binding.btnDown.setImageResource(R.drawable.ic_down_secound_primary);
                        binding.btnUp.setImageResource(R.drawable.ic_up_secound_primary);
                    }
                } else {
                    mark = "";
                    binding.btnDown.setImageResource(R.drawable.ic_down_secound_primary);
                    binding.btnUp.setImageResource(R.drawable.ic_up_secound_primary);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void loadComment() {
        comments = new ArrayList<>();

        ref = database.getReference("QuestionAns").child(qId).child("Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    ModelComment comment = d.getValue(ModelComment.class);
                    comments.add(comment);
                }
                adapterComment = new AdapterComment(comments, QuesAnsDetailActivity.this);
                binding.rcComment.setHasFixedSize(true);
                binding.rcComment.setLayoutManager(new LinearLayoutManager(QuesAnsDetailActivity.this));
                binding.rcComment.setAdapter(adapterComment);
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

    private void addCommment(String comment) {
        ref = database.getReference("QuestionAns");
        Query query = ref.orderByChild("qId").equalTo(qId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    String timeStamp = String.valueOf(System.currentTimeMillis());

                    HashMap<String, String> hash = new HashMap<>();
                    hash.put("cId", timeStamp);
                    hash.put("cUid", myUid);
                    hash.put("cImage", myImage);
                    hash.put("cName", myName);
                    hash.put("comment", comment);
                    hash.put("timeStamp", timeStamp);

                    d.getRef().child("Comments").child(timeStamp).setValue(hash).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            binding.edComment.clearFocus();
                            binding.edComment.setText("");

                            hideKeyboard(QuesAnsDetailActivity.this);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(QuesAnsDetailActivity.this, "something error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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

    private void loadPost() {
        ref = database.getReference("QuestionAns");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clearAllChip();
                for (DataSnapshot d : snapshot.getChildren()) {
                    ModelQuestionAns model = d.getValue(ModelQuestionAns.class);

                    if (model.getqId().equals(qId)) {
                        binding.txtName.setText(model.getuName());
                        binding.txtEmail.setText(model.getuEmail());
                        binding.txtPoint.setText(model.getPoint());

                        String timeStamp = model.getTimeStamp();

                        //conver time stamp to dd/mm/yyyy hh:mm am/pm
                        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                        cal.setTimeInMillis(Long.parseLong(timeStamp));
                        String dateTime = (String) DateFormat.format("dd/MM/yyyy hh:mm:aa", cal);

                        binding.txtTime.setText(dateTime);

                        try {
                            Picasso.get().load(model.getuImg()).into(binding.imgPerson);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        binding.txtQues.setText(model.getQuestion());
                        binding.txtDescrip.setText(model.getDescrip());

                        tag = model.getTag();
                        if (!tag.equals("")) {
                            binding.txtStaticTag.setVisibility(View.VISIBLE);
                            String tags[] = tag.split("::");
                            for (String s : tags) {
                                if (!s.equals("")) {
                                    addToChipGroup(s, binding.chipGroup);
                                }
                            }
                        } else {
                            binding.txtStaticTag.setVisibility(View.GONE);
                        }
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clearAllChip() {
        for (int i = 0; i < binding.chipGroup.getChildCount(); i++) {
            binding.chipGroup.removeAllViews();
        }
    }

    private void getTag() {
        for (int i = 0; i < binding.chipGroup.getChildCount(); i++) {
            Log.d(TAG, "printChipValue: chip::" + ((Chip) binding.chipGroup.getChildAt(i)).getText().toString());
        }
    }

    private void addToChipGroup(String s, ChipGroup chipGroup) {
        Chip chip = new Chip(this);
        ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(this,
                null,
                0,
                R.style.chipCustom);
        chip.setChipDrawable(chipDrawable);

//        Chip chip = new Chip(QuesAnsDetailActivity.this, null, R.style.chipCustom);
        chip.setChipText(s);
        chip.setCloseIconEnabled(false);
        chip.setClickable(true);
        chip.setCheckable(false);
        chip.setTextAppearanceResource(R.style.chipCustom);
        chipGroup.addView(chip);

        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuesAnsDetailActivity.this, SearchActivity.class);
                intent.putExtra("tag", s);
                intent.putExtra("collection", "question");
                startActivity(intent);
            }
        });
    }

    private void initView() {
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        actionBar = getSupportActionBar();
        actionBar.setTitle("Q & A Details");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        if (qUid.equals(myUid)) {
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
               ref=database.getReference("QuestionAns").child(qId);
               ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void unused) {
                       onBackPressed();
                       finish();
                   }
               });
                return true;
            case R.id.action_edit:
                Intent intent = new Intent(QuesAnsDetailActivity.this, AddQuestionActivity.class);
                intent.putExtra("qId", qId);
                startActivity(intent);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}