package com.example.wongwien.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wongwien.ChatActivity;
import com.example.wongwien.ChatlistActivity;
import com.example.wongwien.R;
import com.example.wongwien.model.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class AdapterFriend extends RecyclerView.Adapter<AdapterFriend.Myholder>{
    @NonNull
    List<ModelUser>users;
    Context context;

    public AdapterFriend(@NonNull List<ModelUser> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @Override
    public Myholder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.row_chatlist_friend,parent,false);
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  AdapterFriend.Myholder holder, int position) {
        String name=users.get(position).getName();
        String email=users.get(position).getEmail();
        String image=users.get(position).getImage();
        String hisUid=users.get(position).getUid();
        String hisStatus=users.get(position).getStatus();

        holder.tvName.setText(name);
        holder.tvEmail.setText(email);

        try{
            Picasso.get().load(image).into(holder.imgPerson);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(hisStatus.equals("online")){
            holder.imgStatus.setVisibility(View.VISIBLE);
        }else{
            holder.imgStatus.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid",hisUid);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class Myholder extends RecyclerView.ViewHolder{
        private CircleImageView imgPerson;
        private TextView tvName,tvEmail;
        private ImageView imgStatus;
        public Myholder(@NonNull  View itemView) {
            super(itemView);
            imgPerson=itemView.findViewById(R.id.imgPerson);
            tvEmail=itemView.findViewById(R.id.tvEmail);
            tvName=itemView.findViewById(R.id.tvName);
            imgStatus=itemView.findViewById(R.id.imgStatus);

        }
    }
}
