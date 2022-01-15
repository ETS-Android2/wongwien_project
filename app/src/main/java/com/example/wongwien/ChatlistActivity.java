package com.example.wongwien;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
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
import com.example.wongwien.databinding.ActivityChatlistBinding;
import com.example.wongwien.fragment.chatlist.LoadFriendFragment;
import com.example.wongwien.fragment.chatlist.LoadMessageFragment;
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
    private ActivityChatlistBinding binding;

    String myUid;
    int  showMessage = 1;

    List<ModelContact> contactlist;
    ArrayList<ModelUser> userList;
    ArrayList<ModelUser> userContactlist;

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatlistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        checkUserStatus();

        loadUserInfoToActionBar();

        checkShowDisplay();

        binding.linearFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.underlineFriends.setBackgroundColor(getResources().getColor(R.color.primary));
                binding.underlineMessage.setBackgroundColor(getResources().getColor(R.color.white));
                showMessage=2;
                checkShowDisplay();
            }
        });

        binding.linearMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.underlineFriends.setBackgroundColor(getResources().getColor(R.color.white));
                binding.underlineMessage.setBackgroundColor(getResources().getColor(R.color.primary));
                showMessage=1;
                checkShowDisplay();
            }
        });

        binding.edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s.toString().trim())) {
                    searchFriend(s.toString().trim());
                } else {
                    checkShowDisplay();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void checkShowDisplay() {
        switch(showMessage){
            case 1:
                loadMessageList();
                break;
            case 2:
                loadFriend();
                break;
            default:
                break;
        }
    }

    /*
     * load contact->load model user contact ->find last message
     * */
    private void loadMessageList() {
        Log.d(TAG, "loadMessageList: ");
        contactlist = new ArrayList<>();

        ref = database.getReference("MessageList").child(myUid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactlist.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    ModelContact id = d.getValue(ModelContact.class);
                    contactlist.add(id);

                }
                loadModelUserContact();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadModelUserContact() {
        userContactlist = new ArrayList<>();

        ref = database.getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userContactlist.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    ModelUser user = d.getValue(ModelUser.class);
                    for (ModelContact contact : contactlist) {
                        if (contact.getId().equals(user.getUid())) {
                            userContactlist.add(user);
                        }
                    }
                }
                loadMessageToFragment();
//                adapterMessageList = new AdapterMessageList(ChatlistActivity.this, userContactlist);
//
//                for (int i = 0; i < userContactlist.size(); i++) {
//                    findLastMessage(userContactlist.get(i).getUid());
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });

    }


    private void searchFriend(String query) {
        ref = database.getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    ModelUser user1 = d.getValue(ModelUser.class);

                    if (!user1.getUid().equals(user.getUid())) {
                        if (user1.getName().toLowerCase().contains(query.toLowerCase()) ||
                                user1.getEmail().toLowerCase().contains(query.toLowerCase())) {
                            userList.add(user1);
                        }
                    }
                    loadFriendToFragment();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }

    private void loadFriend() {
        Log.d(TAG, "loadFriend: ");
        ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();

                for (DataSnapshot d : snapshot.getChildren()) {
                    ModelUser user1 = d.getValue(ModelUser.class);
                    if (!user1.getUid().equals(user.getUid())) {
                        userList.add(user1);
                    }
                    loadFriendToFragment();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }

    private void loadFriendToFragment() {

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("list", userList);
        try {
            LoadFriendFragment frag = new LoadFriendFragment();
            frag.setArguments(bundle);

            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.replace(R.id.container, frag, "");
            trans.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void loadMessageToFragment() {

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("list", userContactlist);
        bundle.putString("myUid", myUid);
        try {
            LoadMessageFragment frag = new LoadMessageFragment();
            frag.setArguments(bundle);

            FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
            trans.replace(R.id.container, frag, "");
            trans.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadUserInfoToActionBar() {
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
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        userList = new ArrayList<>();
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

    private void checkOnlineStatus(String status) {
        DatabaseReference onlineRef = database.getReference("Users").child(myUid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

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

        checkShowDisplay();
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        String timeStamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timeStamp);

        super.onPause();
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        String timeStamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timeStamp);
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        Log.d(TAG, "onResume: showStatus::" + showMessage);

        checkOnlineStatus("online");

        super.onResume();
    }
}