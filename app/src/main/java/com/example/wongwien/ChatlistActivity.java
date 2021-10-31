package com.example.wongwien;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.wongwien.adapter.AdapterMessageList;
import com.example.wongwien.model.ModelChat;
import com.example.wongwien.model.ModelUser;
import com.example.wongwien.model.ModelContact;
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

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference ref;


    private View underlineMessage, underlineFriend;
    private TextView tvMessages, tvFriends;
    private EdittextV2 edSearch;
    private RecyclerView rcList;
    AdapterFriend adapterFriend;
    AdapterMessageList adapterMessageList;
    String myUid;
    boolean showMessage=true;
    String lastMessage;
    String lastTime;

    List<ModelUser> userList;
    List<ModelContact>contactlist;
    List<ModelUser>userContactlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);

        initView();
        checkUserStatus();

        loadUserInfo();

        initViewFunction();

        checkShowDisplay();

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
                    checkShowDisplay();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void checkShowDisplay() {
        Log.d(TAG, "checkShowDisplay: showStatus::"+showMessage);
        if(showMessage){
            loadMessageList();
            underlineFriend.setBackgroundColor(getResources().getColor(R.color.white));
            underlineMessage.setBackgroundColor(getResources().getColor(R.color.primary));

        }else{
            loadFriend();
            underlineFriend.setBackgroundColor(getResources().getColor(R.color.primary));
            underlineMessage.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }

    /*
    * load contact->load model user contact ->find last message
    * */
    private void loadMessageList() {
        Log.d(TAG, "loadMessageList: ");
        contactlist=new ArrayList<>();

        ref=database.getReference("MessageList").child(myUid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                contactlist.clear();
                for(DataSnapshot d:snapshot.getChildren()){
                    ModelContact id=d.getValue(ModelContact.class);
                    contactlist.add(id);

                }
                loadModelUserContact();
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    private void loadModelUserContact() {
        userContactlist=new ArrayList<>();

        ref=database.getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                userContactlist.clear();
                for(DataSnapshot d:snapshot.getChildren()){
                    ModelUser user=d.getValue(ModelUser.class);
                    for(ModelContact contact:contactlist){
                        if(contact.getId().equals(user.getUid())){
                            userContactlist.add(user);
                        }
                    }
                }

                adapterMessageList=new AdapterMessageList(ChatlistActivity.this,userContactlist);

                for(int i=0;i<userContactlist.size();i++){
                    findLastMessage(userContactlist.get(i).getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });

    }

    private void findLastMessage(String uid) {
        ref=database.getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for(DataSnapshot d:snapshot.getChildren()){

                    ModelChat chat=d.getValue(ModelChat.class);
                    if((chat.getSender().equals(uid)&&chat.getReceiver().equals(myUid))||
                            (chat.getReceiver().equals(uid)&&chat.getSender().equals(myUid))){

                        lastMessage =chat.getMessage();
                        lastTime =chat.getTimeStamp();
                    }
                }
                adapterMessageList.notifyDataSetChanged();
                adapterMessageList.setLastMessage(uid,lastMessage);
                adapterMessageList.setLastTime(uid,lastTime);
                rcList.setAdapter(adapterMessageList);
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

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
        Log.d(TAG, "loadFriend: ");
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

                showMessage=false;
                checkShowDisplay();
            }
        });
        underlineFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                underlineFriend.setBackgroundColor(getResources().getColor(R.color.primary));
                underlineMessage.setBackgroundColor(getResources().getColor(R.color.white));

                showMessage=false;
                checkShowDisplay();
            }
        });
        tvMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                underlineFriend.setBackgroundColor(getResources().getColor(R.color.white));
                underlineMessage.setBackgroundColor(getResources().getColor(R.color.primary));

                showMessage=true;
                checkShowDisplay();
            }
        });
        underlineMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                underlineFriend.setBackgroundColor(getResources().getColor(R.color.white));
                underlineMessage.setBackgroundColor(getResources().getColor(R.color.primary));

                showMessage=true;
                checkShowDisplay();
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

        rcList.setHasFixedSize(true);
        rcList.setLayoutManager(new LinearLayoutManager(ChatlistActivity.this));

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        userList = new ArrayList<>();
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
        Log.d(TAG, "onStart: ");
        Log.d(TAG, "onStart: showStatus::"+showMessage);

        checkUserStatus();
        checkOnlineStatus("online");

        checkShowDisplay();
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        String timeStamp=String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timeStamp);
//        try{
//            userList.clear();
//            contactlist.clear();
//            userContactlist.clear();
//            adapterFriend.notifyDataSetChanged();
//            adapterMessageList.notifyDataSetChanged();
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        super.onPause();
    }

    @Override
    protected void onStop() {
//        try{
//            userList.clear();
//            contactlist.clear();
//            userContactlist.clear();
//            adapterFriend.notifyDataSetChanged();
//            adapterMessageList.notifyDataSetChanged();
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        String timeStamp=String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timeStamp);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        Log.d(TAG, "onResume: showStatus::"+showMessage);

        checkOnlineStatus("online");

        super.onResume();
    }
}