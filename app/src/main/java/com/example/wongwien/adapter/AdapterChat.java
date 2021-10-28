package com.example.wongwien.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wongwien.R;
import com.example.wongwien.model.ModelChat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterChat extends RecyclerView.Adapter<AdapterChat.Myholder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private static final String TAG = "AdapterChat";
    String hisImgUrl;
    String hisUid;
    String myUid;
    List<ModelChat> chatList;
    Context context;

    FirebaseUser user;
    DatabaseReference ref;

    AlertDialog.Builder builder;

    public AdapterChat(String imgUrl, List<ModelChat> chatList, Context context) {
        this.hisImgUrl = imgUrl;
        this.chatList = chatList;
        this.context = context;
    }

    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case MSG_TYPE_LEFT:
                View view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false);
                return new Myholder(view);

            case MSG_TYPE_RIGHT:
                view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false);
                return new Myholder(view);
            default:
                return null;
        }
    }

    ;

    @Override
    public int getItemViewType(int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        hisUid = user.getUid();

        if (chatList.get(position).getSender().equals(user.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }


    @Override
    public void onBindViewHolder(@NonNull AdapterChat.Myholder holder, int position) {
        String message = chatList.get(position).getMessage();
        String timeStamp = chatList.get(position).getTimeStamp();
        String isSeen = chatList.get(position).getIsSeen();

        //conver time stamp to dd/mm/yyyy hh:mm am/pm
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = (String) DateFormat.format("dd/MM/yyyy hh:mm:aa", cal);

        holder.txtMessage.setText(message);
        holder.txtDate.setText(dateTime);


        //show only last message
        try {
            if (position == chatList.size() - 1) {
                holder.txtStatus.setVisibility(View.VISIBLE);
                if (isSeen.equals("true")) {
                    holder.txtStatus.setText("Seen");
                } else {
                    holder.txtStatus.setText("Delivered");
                }
            } else {
                holder.txtStatus.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.txtMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.showDetails.getVisibility() == View.VISIBLE) {
                    holder.showDetails.setVisibility(View.GONE);
                } else {
                    holder.showDetails.setVisibility(View.VISIBLE);
                }
            }
        });

        try {
            Picasso.get().load(hisImgUrl).into(holder.imgPerson);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.txtMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                removeMessage(position);
                return true;
            }
        });
    }

    private void removeMessage(int position) {
        myUid = FirebaseAuth.getInstance().getUid();
        String timeStamp = chatList.get(position).getTimeStamp();

        ref = FirebaseDatabase.getInstance().getReference("Chats").child(myUid).child(hisUid);
        Query query = ref.orderByChild("timeStamp").equalTo(timeStamp);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {

                    ModelChat chat = d.getValue(ModelChat.class);

                    Log.d(TAG, "onDataChange: myUid::"+myUid);
                    Log.d(TAG, "onDataChange: message::" + chat.getMessage());

                    if (chat.getSender().equals(myUid)) {
                        Log.d(TAG, "onDataChange: message::match" + chat.getMessage());

                        HashMap<String, Object> hash = new HashMap<>();
                        hash.put("message", "Message removed");

                        builder=new AlertDialog.Builder(context);
                        builder.setTitle("Are you sure to delete ?");
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                d.getRef().removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Message removed...", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: " + error.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    class Myholder extends RecyclerView.ViewHolder {
        private TextView txtStatus, txtDate, txtMessage;
        private CircleImageView imgPerson;
        private LinearLayout showDetails;
        private ImageView imgStatus;

        public Myholder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtDate = itemView.findViewById(R.id.txtTime);
            imgPerson = itemView.findViewById(R.id.imgPerson);
            showDetails = itemView.findViewById(R.id.showDetails);
            imgStatus = itemView.findViewById(R.id.imgStatus);

            try {
                txtStatus = itemView.findViewById(R.id.txtStatus);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
