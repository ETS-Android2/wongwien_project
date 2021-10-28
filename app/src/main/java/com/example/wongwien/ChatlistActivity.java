package com.example.wongwien;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wongwien.adapter.AdapterFriend;
import com.example.wongwien.model.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatlistActivity extends AppCompatActivity {
    private static final String TAG = "ChatlistActivity";
    List<ModelUser> userList;

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference ref;


    private View underlineMessage, underlineFriend;
    private TextView tvMessages, tvFriends;
    private EdittextV2 edSearch;
    private RecyclerView rcList;
    AdapterFriend adapterFriend;
    String myUid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);

        initView();

        loadUserInfo();

        initViewFunction();

        loadFriend();

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString().trim())) {
                    searchFriend(s.toString().trim());
                } else {
                    loadFriend();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void searchFriend(String query) {
            ref = database.getReference("Users");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    userList.clear();
                    for(DataSnapshot d:snapshot.getChildren()){
                        ModelUser user1 = d.getValue(ModelUser.class);

                        if (!user1.getUid().equals(user.getUid())) {
                            if (user1.getName().toLowerCase().contains(query.toLowerCase()) ||
                                    user1.getEmail().toLowerCase().contains(query.toLowerCase())) {
                                userList.add(user1);
                            }
                        }
                        adapterFriend=new AdapterFriend(userList,ChatlistActivity.this);
                        rcList.setLayoutManager(new LinearLayoutManager(ChatlistActivity.this));
                        rcList.setAdapter(adapterFriend);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "onCancelled: "+error.getMessage());
                }
            });
    }

    private void loadFriend() {
        ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                userList.clear();

                for(DataSnapshot d:snapshot.getChildren()){
                    ModelUser user1=d.getValue(ModelUser.class);
                    if(!user1.getUid().equals(user.getUid())){
                        userList.add(user1);
                    }
                    adapterFriend=new AdapterFriend(userList,ChatlistActivity.this);
                    rcList.setLayoutManager(new LinearLayoutManager(ChatlistActivity.this));
                    rcList.setAdapter(adapterFriend);
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });

    }
    private void initViewFunction() {
        tvFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                underlineFriend.setBackgroundColor(getResources().getColor(R.color.primary));
                underlineMessage.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });
        underlineFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                underlineFriend.setBackgroundColor(getResources().getColor(R.color.primary));
                underlineMessage.setBackgroundColor(getResources().getColor(R.color.white));
            }
        });
        tvMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                underlineFriend.setBackgroundColor(getResources().getColor(R.color.white));
                underlineMessage.setBackgroundColor(getResources().getColor(R.color.primary));
            }
        });
        underlineMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                underlineFriend.setBackgroundColor(getResources().getColor(R.color.white));
                underlineMessage.setBackgroundColor(getResources().getColor(R.color.primary));
            }
        });
    }

    private void loadUserInfo() {
        ref = database.getReference("Users").child(user.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser userInfo = snapshot.getValue(ModelUser.class);

                ActionBar actionbar = getSupportActionBar();
                actionbar.setTitle(userInfo.getName());
                actionbar.setDisplayHomeAsUpEnabled(true);
                actionbar.setDisplayShowHomeEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatlistActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        tvMessages = findViewById(R.id.tvMessages);
        tvFriends = findViewById(R.id.tvFriends);
        edSearch = findViewById(R.id.edSearch);
        underlineMessage = findViewById(R.id.underlineMessage);
        underlineFriend = findViewById(R.id.underlineFriends);
        rcList=findViewById(R.id.rcList);


        /*
        testingggggggggggggg
         */
        rcList.setHasFixedSize(true);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        userList = new ArrayList<>();
    }

    private void checkUserStatus(){
        //get current user
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if(user !=null){
            myUid=user.getUid();

        }else{
            //go back to login
            startActivity(new Intent(this, WelcomeActivity.class));
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
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
}