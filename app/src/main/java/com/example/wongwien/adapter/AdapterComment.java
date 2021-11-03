package com.example.wongwien.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wongwien.R;
import com.example.wongwien.model.ModelComment;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.Myholder>{
    List<ModelComment>comments;
    Context context;

    public AdapterComment(List<ModelComment> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    @Override
    public Myholder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_comment,parent,false);
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  AdapterComment.Myholder holder, int position) {
        String name=comments.get(position).getcName();
        String image=comments.get(position).getcImage();
        String comment=comments.get(position).getComment();
        String timeStamp=comments.get(position).getTimeStamp();

        //conver time stamp to dd/mm/yyyy hh:mm am/pm
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = (String) DateFormat.format("dd/MM/yyyy hh:mm:aa", cal);

        holder.txtName.setText(name);
        holder.txtTime.setText(dateTime);
        holder.txtComment.setText(comment);

        try{
            Picasso.get().load(image).into(holder.imgPerson);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class Myholder extends RecyclerView.ViewHolder{
        private TextView txtName,txtTime,txtComment;
        private CircleImageView imgPerson;
        public Myholder(@NonNull  View itemView) {
            super(itemView);
            imgPerson=itemView.findViewById(R.id.imgPerson);
            txtComment=itemView.findViewById(R.id.tvComment);
            txtTime=itemView.findViewById(R.id.tvTime);
            txtName=itemView.findViewById(R.id.tvName);
        }
    }
}
