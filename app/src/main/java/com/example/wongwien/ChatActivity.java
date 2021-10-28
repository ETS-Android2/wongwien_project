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
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wongwien.adapter.AdapterChat;
import com.example.wongwien.model.ModelChat;
import com.example.wongwien.model.ModelUser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private MaterialToolbar toolbar;
    private ImageButton btnSend;
    private CircleImageView imgPerson;
    private TextView txtName,txtStatus;
    private ImageView imgStatus;
    private RecyclerView rcChat;
    private EdittextV2 edMessage;

    FirebaseDatabase database;
    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    DatabaseReference ref;
    DatabaseReference refSeen;
    ValueEventListener seenlinener;

    String myUid;
    String hisUid;
    String hisImg;

    List<ModelChat>chatList;
    AdapterChat adapterChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar=getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);

        Intent intent=getIntent();
        hisUid=intent.getStringExtra("hisUid");

        initView();

        //find myUid first to use in loadMessage
        checkUserStatus();

        loadHisUser();
        readMessage();
        seenMessage();

        edMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s)){
                    checkTypingStatus("Typing...");
                }else{
                    checkTypingStatus("none");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=edMessage.getText().toString().trim();
                if(!TextUtils.isEmpty(message)){
                    sendMessage(message);
                }else{
                    Toast.makeText(ChatActivity.this, "Can not send the empty message...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void seenMessage() {
        refSeen=database.getReference("Chats");
         seenlinener = refSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    ModelChat chat = d.getValue(ModelChat.class);
                    if (chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", "true");
                        d.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }

    private void readMessage() {
        chatList=new ArrayList<>();

        ref=database.getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                chatList.clear();

                for(DataSnapshot d:snapshot.getChildren()){
                    ModelChat chat=d.getValue(ModelChat.class);
                    if(chat.getSender().equals(myUid) && chat.getReceiver().equals(hisUid)||
                            chat.getReceiver().equals(myUid) && chat.getSender().equals(hisUid)){
                        chatList.add(chat);
                    }
                    adapterChat=new AdapterChat(hisImg,chatList,ChatActivity.this);
                    rcChat.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                    rcChat.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    private void sendMessage(String message) {
        String timeStamp=""+System.currentTimeMillis();

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",myUid);
        hashMap.put("receiver",hisUid);
        hashMap.put("message",message);
        hashMap.put("timeStamp",timeStamp);
        hashMap.put("type","text");
        hashMap.put("isSeen","false");

        ref=database.getReference("Chats");
        ref.push().setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                edMessage.setText("");
                Toast.makeText(ChatActivity.this, "success", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                Toast.makeText(ChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onFailure: "+e.getMessage());
            }
        });
    }

    private void loadHisUser() {
        ref=database.getReference("Users");

        Query userquery=ref.orderByChild("uid").equalTo(hisUid);
        userquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for(DataSnapshot d:snapshot.getChildren()){
                    String name= (String) d.child("name").getValue();
                    hisImg= (String) d.child("image").getValue();

                    txtName.setText(name);
                    try{
                        Picasso.get().load(hisImg).into(imgPerson);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });

    }

    private void initView() {
        btnSend=findViewById(R.id.btnSend);
        imgPerson=findViewById(R.id.imgPerson);
        imgStatus=findViewById(R.id.imgStatus);
        txtName=findViewById(R.id.txtName);
        txtStatus=findViewById(R.id.txtStatus);
        rcChat=findViewById(R.id.rcChat);
        edMessage=findViewById(R.id.edMessage);

        firebaseAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

    }
    private void checkUserStatus(){
        //get current user
         user= firebaseAuth.getCurrentUser();
        if(user !=null){
            myUid=user.getUid();
            Log.d(TAG, "checkUserStatus: myUid::"+myUid);

        }else{
            //go back to login
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        }
    }

    private void checkOnlineStatus(String status){
        DatabaseReference onlineRef=database.getReference("Users").child(myUid);
        Log.d(TAG, "checkOnlineStatus: myUid::"+myUid);

        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("status",status);

        onlineRef.getRef().updateChildren(hashMap);
    }

    private void checkTypingStatus(String status){
        DatabaseReference onlineRef=database.getReference("Users").child(myUid);
        Log.d(TAG, "checkOnlineStatus: myUid::"+myUid);

        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("typingStatus",status);

        onlineRef.getRef().updateChildren(hashMap);
    }

    private void checkHisStatus(){
        //check his status online
        ref=FirebaseDatabase.getInstance().getReference("Users").child(hisUid);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                ModelUser hisUser=snapshot.getValue(ModelUser.class);
                try{
                    if(hisUser.getStatus().equals("online")){
                        //check typing
                        if(hisUser.getTypingStatus().equals("Typing...")){
                            txtStatus.setText(hisUser.getTypingStatus());
                        }else{
                            txtStatus.setText("Online");
                        }
                        imgStatus.setVisibility(View.VISIBLE);
                    }else{
                        imgStatus.setVisibility(View.GONE);

                        //conver time stamp to dd/mm/yyyy hh:mm am/pm
                        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                        cal.setTimeInMillis(Long.parseLong(hisUser.getStatus()));
                        String dateTime = (String) DateFormat.format("dd/MM/yyyy hh:mm:aa", cal);

                        txtStatus.setText("Seen last "+dateTime);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        checkOnlineStatus("online");
        checkHisStatus();
        checkTypingStatus("none");
        super.onStart();
    }

    @Override
    protected void onPause() {
        String timeStamp=String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timeStamp);
        checkTypingStatus("none");

        refSeen.removeEventListener(seenlinener);
        super.onPause();
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        checkHisStatus();
        checkTypingStatus("none");
        super.onResume();
    }
}