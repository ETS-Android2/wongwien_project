package com.example.wongwien.fragment.chatlist;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wongwien.R;
import com.example.wongwien.adapter.AdapterMessageList;
import com.example.wongwien.databinding.FragmentLoadMessageBinding;
import com.example.wongwien.model.ModelChat;
import com.example.wongwien.model.ModelUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class LoadMessageFragment extends Fragment {
    private FragmentLoadMessageBinding binding;
    ArrayList<ModelUser>userContactlist;
    AdapterMessageList adapterMessageList;

    FirebaseDatabase database;
    DatabaseReference ref;
    String lastMessage,lastTime,myUid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentLoadMessageBinding.inflate(inflater,container,false);
        View view=binding.getRoot();

        binding.rcList.setHasFixedSize(true);
        binding.rcList.setLayoutManager(new LinearLayoutManager(getContext()));
        userContactlist=new ArrayList<>();
        database=FirebaseDatabase.getInstance();

        userContactlist=getArguments().getParcelableArrayList("list");
        myUid=getArguments().getString("myUid");

        if(null != userContactlist){
            adapterMessageList=new AdapterMessageList(getContext(),userContactlist);

            for (int i = 0; i < userContactlist.size(); i++) {
                    findLastMessage(userContactlist.get(i).getUid());
                }
        }
        return view;
    }

    private void findLastMessage(String uid) {
        ref = database.getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {

                    ModelChat chat = d.getValue(ModelChat.class);
                    if ((chat.getSender().equals(uid) && chat.getReceiver().equals(myUid)) ||
                            (chat.getReceiver().equals(uid) && chat.getSender().equals(myUid))) {

                        lastMessage = chat.getMessage();
                        lastTime = chat.getTimeStamp();
                    }
                }
                adapterMessageList.notifyDataSetChanged();
                adapterMessageList.setLastMessage(uid, lastMessage);
                adapterMessageList.setLastTime(uid, lastTime);

                binding.rcList.setAdapter(adapterMessageList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}