package com.example.wongwien.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wongwien.R;
import com.example.wongwien.ReviewDetailActivity;
import com.example.wongwien.model.ModelReview;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.Myholder>{

    public static final int PATTERN_REVIEW_1=0;
    public static final int PATTERN_REVIEW_2=1;
    public static final int PATTERN_REVIEW_3=2;

    Context context;
    ArrayList<ModelReview> reviews;

    public AdapterReview(Context context, ArrayList<ModelReview> reviews) {
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch(viewType){
            case PATTERN_REVIEW_1:
                View view= LayoutInflater.from(context).inflate(R.layout.row_review_pattern1,parent,false);
                return new Myholder(view);
            case PATTERN_REVIEW_2:
                view=LayoutInflater.from(context).inflate(R.layout.row_review_pattern2,parent,false);
                return new Myholder(view);
            case PATTERN_REVIEW_3:
                view=LayoutInflater.from(context).inflate(R.layout.row_review_pattern3,parent,false);
                return new Myholder(view);
            default:
                view=LayoutInflater.from(context).inflate(R.layout.row_review_pattern1,parent,false);
                return new Myholder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull Myholder holder, int position) {
        String timeStamp = reviews.get(position).getR_timeStamp();
        String title=reviews.get(position).getR_title();
        String desc0=reviews.get(position).getR_desc0();
        String point=reviews.get(position).getR_point();

        //conver time stamp to dd/mm/yyyy hh:mm am/pm
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = (String) DateFormat.format("dd/MM/yyyy hh:mm:aa", cal);

        holder.rTitile.setText(title);
        holder.rDesc0.setText(desc0);
        holder.txtPoint.setText(point);

        try{
            Picasso.get().load(reviews.get(position).getR_image0()).into(holder.r_image0);
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            Picasso.get().load(reviews.get(position).getR_image1()).into(holder.r_image1);
        }catch (Exception e){
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ReviewDetailActivity.class);
                Bundle bundle=new Bundle();
                bundle.putParcelable("list",reviews.get(position));

                intent.putExtra("bundle",bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        switch(reviews.get(position).getR_type()){
            case "pattern1":
                return PATTERN_REVIEW_1;
            case "pattern2":
                return PATTERN_REVIEW_2;
            case "pattern3":
                return PATTERN_REVIEW_3;
            default:
                return PATTERN_REVIEW_1;
        }
    }




    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class Myholder extends RecyclerView.ViewHolder{
        private TextView rTitile,rDesc0,txtPoint;
        private ImageView r_image0,r_image1;

        public Myholder(@NonNull View itemView) {
            super(itemView);
            rTitile=itemView.findViewById(R.id.r_title);
            rDesc0=itemView.findViewById(R.id.r_desc0);
            txtPoint=itemView.findViewById(R.id.txtPoint);

            try{
                r_image0=itemView.findViewById(R.id.r_image0);
                r_image1=itemView.findViewById(R.id.r_image1);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
