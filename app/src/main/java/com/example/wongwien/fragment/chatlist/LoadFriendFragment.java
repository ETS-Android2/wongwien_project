package com.example.wongwien.fragment.chatlist;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.wongwien.ChatlistActivity;
import com.example.wongwien.R;
import com.example.wongwien.adapter.AdapterFriend;
import com.example.wongwien.databinding.FragmentLoadFriendBinding;
import com.example.wongwien.model.ModelUser;

import java.util.ArrayList;

public class LoadFriendFragment extends Fragment {

    private FragmentLoadFriendBinding binding;
    ArrayList<ModelUser>friendlist;
    AdapterFriend adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentLoadFriendBinding.inflate(inflater,container,false);
        View view=binding.getRoot();

        binding.rcList.setHasFixedSize(true);
        binding.rcList.setLayoutManager(new LinearLayoutManager(getContext()));

        friendlist=new ArrayList<>();

        friendlist=getArguments().getParcelableArrayList("list");
        if(null != friendlist){
            loadFriendList(friendlist);
        }

        return view;

    }

    private void loadFriendList(ArrayList<ModelUser> friendlist) {
        adapter=new AdapterFriend(friendlist,getContext());
        binding.rcList.setAdapter(adapter);
    }

    @Override
    public void onAttach(@NonNull  Context context) {
        super.onAttach(context);
    }
}