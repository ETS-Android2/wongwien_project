package com.example.wongwien.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wongwien.ChatActivity;
import com.example.wongwien.R;
import com.example.wongwien.model.ModelUser;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterMessageList extends RecyclerView.Adapter<AdapterMessageList.Myholder>{
    Context context;
    List<ModelUser>userslist;
    HashMap<String,String> hashMapMessage;
    HashMap<String,String> hashMapTime;

    public AdapterMessageList(Context context, List<ModelUser> userslist) {
        this.context = context;
        this.userslist = userslist;
        hashMapMessage =new HashMap<>();
        hashMapTime =new HashMap<>();
    }

    public void setLastMessage(String uid,String lastMessage) {
        hashMapMessage.put(uid,lastMessage);
    }
    public void setLastTime(String uid,String lastTime) {
        hashMapTime.put(uid,lastTime);
    }

    @Override
    public Myholder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_chatlist_message,parent,false);
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  AdapterMessageList.Myholder holder, int position) {
        String name=userslist.get(position).getName();
        String image=userslist.get(position).getImage();

        String hisStatus=userslist.get(position).getStatus();

        String message= hashMapMessage.get(userslist.get(position).getUid());
        String time= hashMapTime.get(userslist.get(position).getUid());

//        //conver time stamp to dd/mm/yyyy hh:mm am/pm
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        if(null != time){
            cal.setTimeInMillis(Long.parseLong(time));
        }
            String dateTime = (String) DateFormat.format("dd/MM/yyyy hh:mm:aa", cal);

        if(hisStatus.equals("online")){
            holder.status.setVisibility(View.VISIBLE);
        }else{
            holder.status.setVisibility(View.GONE);
        }

        holder.txtName.setText(name);
        holder.txtTime.setText(dateTime);
        holder.txtMessage.setText(message);

        try{
            Picasso.get().load(image).resize(200,200) .onlyScaleDown().into(holder.imgPerson);
        }catch (Exception e){
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid",userslist.get(position).getUid());
                context.startActivity(intent);
//                ((Activity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userslist.size();
    }

    class Myholder extends RecyclerView.ViewHolder{
        private TextView txtName,txtTime,txtMessage;
        private CircleImageView imgPerson;
        private ImageView status;

        public Myholder(@NonNull  View itemView) {
            super(itemView);
            txtName=itemView.findViewById(R.id.txtName);
            txtTime=itemView.findViewById(R.id.txtTime);
            txtMessage=itemView.findViewById(R.id.txtMessage);
            status=itemView.findViewById(R.id.imgStatus);
            imgPerson=itemView.findViewById(R.id.imgPerson);
        }
    }
}
