package com.example.wongwien.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wongwien.QuesAnsDetailActivity;
import com.example.wongwien.R;
import com.example.wongwien.model.ModelQuestionAns;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterQuestion extends  RecyclerView.Adapter<AdapterQuestion.Myholder>{

    Context context;
    List<ModelQuestionAns>list;

    public AdapterQuestion(Context context, List<ModelQuestionAns> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.row_question,parent,false);

        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterQuestion.Myholder holder, int position) {
        String question=list.get(position).getQuestion();
        String qd=list.get(position).getDescrip();

        String timeStamp=list.get(position).getTimeStamp();
        String qId=list.get(position).getqId();
        String qUid=list.get(position).getuId();


        //conver time stamp to dd/mm/yyyy hh:mm am/pm
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = (String) DateFormat.format("dd/MM/yyyy hh:mm:aa", cal);

        holder.txtQuestion.setText(question);
        holder.txtQd.setText(qd);
        holder.txtTime.setText(dateTime);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, QuesAnsDetailActivity.class);
                intent.putExtra("qId",qId);
                intent.putExtra("qUid",qUid);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Myholder extends RecyclerView.ViewHolder {
        private TextView txtQuestion,txtTime,txtQd;

        public Myholder(@NonNull View itemView) {
            super(itemView);
            txtQuestion=itemView.findViewById(R.id.txtQuestion);
            txtTime=itemView.findViewById(R.id.txtTime);
            txtQd=itemView.findViewById(R.id.txtQdetail);
        }
    }
}
