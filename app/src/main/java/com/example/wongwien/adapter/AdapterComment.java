package com.example.wongwien.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wongwien.EdittextV2;
import com.example.wongwien.MainActivity;
import com.example.wongwien.QuesAnsDetailActivity;
import com.example.wongwien.R;
import com.example.wongwien.model.ModelComment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterComment extends RecyclerView.Adapter<AdapterComment.Myholder> {
    public static final int COMMENT_NORMAL = 1001;
    public static final int COMMENT_ACTIVE = 1002;
    private static final String TAG = "AdapterComment";
    List<ModelComment> comments;
    Context context;
    boolean showCommentOption = false;
    String userUid;
    String postId;
    String type;
    DatabaseReference ref;


    public AdapterComment(List<ModelComment> comments, Context context, boolean showCommentOption, String postId, String type, String userUid) {
        this.comments = comments;
        this.context = context;
        this.showCommentOption = showCommentOption;
        this.postId = postId;
        this.type = type;
        this.userUid = userUid;
    }

    @Override
    public int getItemViewType(int position) {
        switch (comments.get(position).getIsUsefull()) {
            case "true":
                return COMMENT_ACTIVE;
            case "false":
                return COMMENT_NORMAL;
            default:
                return COMMENT_NORMAL;
        }
    }

    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case COMMENT_NORMAL:
                View view = LayoutInflater.from(context).inflate(R.layout.row_comment, parent, false);
                return new Myholder(view);
            case COMMENT_ACTIVE:
                view = LayoutInflater.from(context).inflate(R.layout.row_comment_active, parent, false);
                return new Myholder(view);
            default:
                view = LayoutInflater.from(context).inflate(R.layout.row_comment, parent, false);
                return new Myholder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterComment.Myholder holder, int position) {
        String name = comments.get(position).getcName();
        String image = comments.get(position).getcImage();
        String comment = comments.get(position).getComment();
        String timeStamp = comments.get(position).getTimeStamp();

        //conver time stamp to dd/mm/yyyy hh:mm am/pm
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = (String) DateFormat.format("dd/MM/yyyy hh:mm:aa", cal);

        holder.txtName.setText(name);
        holder.txtTime.setText(dateTime);
        holder.txtComment.setText(comment);

        if (userUid.equals(comments.get(position).getcUid())) {
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    View view1 = LayoutInflater.from(context).inflate(R.layout.dialog_manage_comment_user, null, false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

                    Button editComment = view1.findViewById(R.id.editComment);
                    Button deleteComment = view1.findViewById(R.id.deleteComment);

                    builder.setView(view1);
                    final AlertDialog show = builder.show();

                    editComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            View view2 = LayoutInflater.from(context).inflate(R.layout.dialog_edit_comment, null, false);
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

                            EdittextV2 edComment = view2.findViewById(R.id.edComment);

                            edComment.setText(comments.get(position).getComment());

                            Button btnCancel = view2.findViewById(R.id.btnCancel);
                            Button btnUpdate = view2.findViewById(R.id.btnUpdate);

                            builder1.setView(view2);
                            final AlertDialog show1 = builder1.show();

                            btnUpdate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (type.equals("QA")) {
                                        ref = FirebaseDatabase.getInstance().getReference("QuestionAns").child(postId).child("Comments").child(comments.get(position).getcId());
                                    } else {
                                        ref = FirebaseDatabase.getInstance().getReference("Reviews").child(postId).child("Comments").child(comments.get(position).getcId());
                                    }

                                    ref.child("comment").setValue(edComment.getText().toString().trim())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();
                                                    show1.dismiss();
                                                    show.dismiss();
                                                }
                                            });
                                }
                            });
                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    show1.dismiss();
                                    show.dismiss();
                                }
                            });
                        }
                    });

                    deleteComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (type.equals("QA")) {
                                ref = FirebaseDatabase.getInstance().getReference("QuestionAns").child(postId).child("Comments").child(comments.get(position).getcId());
                            } else {
                                ref = FirebaseDatabase.getInstance().getReference("Reviews").child(postId).child("Comments").child(comments.get(position).getcId());
                            }

                            ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                    show.dismiss();
                                }
                            });
                        }
                    });

                    return false;
                }
            });
        }

        if (showCommentOption) {
            holder.btnCommentOption.setVisibility(View.VISIBLE);
            holder.btnCommentOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    View view1 = LayoutInflater.from(context).inflate(R.layout.dialog_manage_comment, null, false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);

                    Button usefullComment = view1.findViewById(R.id.usefullComment);
                    Button deleteComment = view1.findViewById(R.id.deleteComment);

                    builder.setView(view1);
                    final AlertDialog show = builder.show();

                    usefullComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (type.equals("QA")) {
                                ref = FirebaseDatabase.getInstance().getReference("QuestionAns").child(postId).child("Comments").child(comments.get(position).getcId());
                            } else {
                                ref = FirebaseDatabase.getInstance().getReference("Reviews").child(postId).child("Comments").child(comments.get(position).getcId());
                            }
                            if (comments.get(position).getIsUsefull().equals("true")) {
                                ref.child("isUsefull").setValue("false").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();
                                        show.dismiss();
                                    }
                                });
                            } else {
                                ref.child("isUsefull").setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(context, "Updated successfully", Toast.LENGTH_SHORT).show();
                                        show.dismiss();
                                    }
                                });
                            }
                        }
                    });

                    deleteComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (type.equals("QA")) {
                                ref = FirebaseDatabase.getInstance().getReference("QuestionAns").child(postId).child("Comments").child(comments.get(position).getcId());
                            } else {
                                ref = FirebaseDatabase.getInstance().getReference("Reviews").child(postId).child("Comments").child(comments.get(position).getcId());
                            }

                            ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                    show.dismiss();
                                }
                            });
                        }
                    });


                }
            });
        } else {
            holder.btnCommentOption.setVisibility(View.GONE);
        }

        try {
            Picasso.get().load(image).into(holder.imgPerson);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class Myholder extends RecyclerView.ViewHolder {
        private TextView txtName, txtTime, txtComment;
        private CircleImageView imgPerson;
        private ImageView btnCommentOption;

        public Myholder(@NonNull View itemView) {
            super(itemView);

            btnCommentOption = itemView.findViewById(R.id.btnCommentOption);
            imgPerson = itemView.findViewById(R.id.imgPerson);
            txtComment = itemView.findViewById(R.id.tvComment);
            txtTime = itemView.findViewById(R.id.tvTime);
            txtName = itemView.findViewById(R.id.tvName);
        }
    }
}
