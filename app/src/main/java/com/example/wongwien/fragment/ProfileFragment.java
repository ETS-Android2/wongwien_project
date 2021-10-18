package com.example.wongwien.fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.wongwien.R;
import com.example.wongwien.model.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    DatabaseReference ref;
    private TextView tvName, tvEmail;
    private CircleImageView imagePerson;
    private ImageView imageCover;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initView(view);

        getProfileUser();

        return view;
    }

    private void getProfileUser() {
        String uid = firebaseAuth.getCurrentUser().getUid();

        ref = database.getReference("Users").child(uid);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                ModelUser user = dataSnapshot.getValue(ModelUser.class);
                Log.d(TAG, "onDataChange: :" + user.toString());

                tvName.setText(user.getName());
                tvEmail.setText(user.getEmail());

                if (!user.getImage().equals("")) {
                    Picasso.get().load(user.getImage()).into(imagePerson);
                }
                if (!user.getCover_image().equals("")) {
                    Picasso.get().load(user.getCover_image()).into(imageCover);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref.addValueEventListener(postListener);
    }

    private void initView(View view) {
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        imagePerson = view.findViewById(R.id.imagePerson);
        imageCover = view.findViewById(R.id.imageCover);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}