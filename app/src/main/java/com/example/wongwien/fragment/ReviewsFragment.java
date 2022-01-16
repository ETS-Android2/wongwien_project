package com.example.wongwien.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.wongwien.AddReviewActivity;
import com.example.wongwien.ChatlistActivity;
import com.example.wongwien.MainActivity;
import com.example.wongwien.R;
import com.example.wongwien.SearchActivity;
import com.example.wongwien.SplashActivity;
import com.example.wongwien.WelcomeActivity;
import com.example.wongwien.adapter.AdapterQuestion;
import com.example.wongwien.adapter.AdapterReview;
import com.example.wongwien.databinding.FragmentReviewsBinding;
import com.example.wongwien.fragment.question.LoadListShowFragment;
import com.example.wongwien.model.ModelQuestionAns;
import com.example.wongwien.model.ModelReview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ReviewsFragment extends Fragment {
    private static final String TAG = "ReviewsFragment";
    private FragmentReviewsBinding binding;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;
    private FirebaseDatabase database;

    int category = 0;
    boolean isRecent = true;
    String categoryList[]={"General","Course","Food","Domitory","Tours"};

    AdapterReview adapter;
    ArrayList<ModelReview> reviewLists;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true); //show menu option in fragment
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding=FragmentReviewsBinding.inflate(inflater,container,false);
        View view=binding.getRoot();

        initView();
//        loadReivewList();
        checkLoadData();
//        loadQuestion();

        binding.swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkLoadData();
                binding.swiperefresh.setRefreshing(false);
            }
        });
        binding.liRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllMode();
                isRecent=true;
                checkLoadData();
            }
        });

        binding.liPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllMode();
                isRecent=false;
                checkLoadData();
            }
        });

        binding.txtGenneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllSelected();
                binding.txtGenneral.setTextColor(getResources().getColor(R.color.white));
                binding.txtGenneral.setBackground(getResources().getDrawable(R.drawable.background_review));
                category = 0;
                checkLoadData();
            }
        });
        binding.txtCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllSelected();
                binding.txtCourse.setTextColor(getResources().getColor(R.color.white));
                binding.txtCourse.setBackground(getResources().getDrawable(R.drawable.background_review));
                category = 1;
                checkLoadData();
            }
        });
        binding.txtFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllSelected();
                binding.txtFood.setTextColor(getResources().getColor(R.color.white));
                binding.txtFood.setBackground(getResources().getDrawable(R.drawable.background_review));
                category = 2;
                checkLoadData();
            }
        });
        binding.txtDormitory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllSelected();
                binding.txtDormitory.setTextColor(getResources().getColor(R.color.white));
                binding.txtDormitory.setBackground(getResources().getDrawable(R.drawable.background_review));
                category = 3;
                checkLoadData();
            }
        });
        binding.txtTours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllSelected();
                binding.txtTours.setTextColor(getResources().getColor(R.color.white));
                binding.txtTours.setBackground(getResources().getDrawable(R.drawable.background_review));
                category = 4;
                checkLoadData();
            }
        });

        return view;
    }
    private void clearAllMode(){
        binding.underlinePopular.setBackgroundColor(getResources().getColor(R.color.white));
        binding.underlineRecently.setBackgroundColor(getResources().getColor(R.color.white));
    }

    private void clearAllSelected() {
        binding.txtGenneral.setTextColor(getResources().getColor(R.color.gray));
        binding.txtCourse.setTextColor(getResources().getColor(R.color.gray));
        binding.txtFood.setTextColor(getResources().getColor(R.color.gray));
        binding.txtDormitory.setTextColor(getResources().getColor(R.color.gray));
        binding.txtTours.setTextColor(getResources().getColor(R.color.gray));

        binding.txtGenneral.setBackgroundColor(getResources().getColor(R.color.white));
        binding.txtCourse.setBackgroundColor(getResources().getColor(R.color.white));
        binding.txtFood.setBackgroundColor(getResources().getColor(R.color.white));
        binding.txtDormitory.setBackgroundColor(getResources().getColor(R.color.white));
        binding.txtTours.setBackgroundColor(getResources().getColor(R.color.white));
    }
    private void initView() {
        //firebase Auth
        firebaseAuth =FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();

        reviewLists=new ArrayList<>();
    }


    private void checkLoadData() {
        if (isRecent) {
            binding.underlinePopular.setBackgroundColor(getResources().getColor(R.color.white));
            binding.underlineRecently.setBackgroundColor(getResources().getColor(R.color.primary));

            loadRecentlyByCategory();

        } else {
            binding.underlinePopular.setBackgroundColor(getResources().getColor(R.color.primary));
            binding.underlineRecently.setBackgroundColor(getResources().getColor(R.color.white));

            loadPopularBycategory();
        }
    }

    private void loadRecentlyByCategory() {
        ref = FirebaseDatabase.getInstance().getReference("Reviews");
        Query query = ref.orderByChild("r_collection").equalTo(categoryList[category]);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewLists.clear();

                for (DataSnapshot d : snapshot.getChildren()) {
                    ModelReview model = d.getValue(ModelReview.class);

                    if(category==0){
                        reviewLists.add(model);
                    }else{
                        if (model.getR_collection().equals(categoryList[category])) {
                            reviewLists.add(model);
                        }
                    }

                }
                showReviewsToRecycler_reverse();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPopularBycategory() {
        ref=FirebaseDatabase.getInstance().getReference("Reviews");
        Query query=ref.orderByChild("r_point");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                reviewLists.clear();
                for(DataSnapshot d:snapshot.getChildren()){
                    ModelReview model=d.getValue(ModelReview.class);

                    if(category==0) {
                        reviewLists.add(model);
                    }else{
                        if(model.getR_collection().equals(categoryList[category])){
                            reviewLists.add(model);
                        }
                    }

                }
                showReviewsToRecycler_reverse();
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    private void showReviewsToRecycler_reverse(){
        adapter=new AdapterReview(getContext(),reviewLists);
        adapter.notifyDataSetChanged();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        binding.rcView.setLayoutManager(linearLayoutManager);
        binding.rcView.setHasFixedSize(true);

        binding.rcView.setAdapter(adapter);
    }

    private void showReviewsToRecycler(){
        adapter=new AdapterReview(getContext(),reviewLists);
        adapter.notifyDataSetChanged();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(true);

        binding.rcView.setLayoutManager(linearLayoutManager);
        binding.rcView.setHasFixedSize(true);

        binding.rcView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
//       checkLoadData();
        super.onResume();
    }

    /*inflate option menu*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_top,menu);

        //hide  icon
        menu.findItem(R.id.action_logout).setVisible(false);

        super.onCreateOptionsMenu(menu,inflater);
    }

    /*handle menu item clicked*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_logout:
                firebaseAuth.signOut();
                checkUserStatus();
                break;
            case R.id.action_chat:
                startActivity(new Intent(getContext(), ChatlistActivity.class));
                break;
            case R.id.action_search:
                startActivity(new Intent(getContext(), SearchActivity.class));
                break;
            case R.id.action_add:
                startActivity(new Intent(getContext(), AddReviewActivity.class));
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkUserStatus(){
        //get current user
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if(user !=null){
            Log.d(TAG, "checkUserStatus: check User::"+user.getEmail());

        }else{
            //go back to login
            startActivity(new Intent(getContext(), SplashActivity.class));
            getActivity().finish();
        }
    }
}