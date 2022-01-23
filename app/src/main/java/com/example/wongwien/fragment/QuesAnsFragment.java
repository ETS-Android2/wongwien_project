package com.example.wongwien.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.wongwien.AddQuestionActivity;
import com.example.wongwien.ChatlistActivity;
import com.example.wongwien.R;
import com.example.wongwien.SearchActivity;
import com.example.wongwien.SplashActivity;
import com.example.wongwien.databinding.FragmentQuesAnsBinding;
import com.example.wongwien.fragment.question.LoadListShowFragment;
import com.example.wongwien.model.ModelQuestionAns;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class QuesAnsFragment extends Fragment {
    private static final String TAG = "QuesAnsFragment";
    int category = 0;
    boolean isRecent = true;
    ArrayList<ModelQuestionAns> questionLists;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;
    private FragmentQuesAnsBinding binding;

    String categoryList[]={"General","Course","Food","Domitory","Tours"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentQuesAnsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
//        View view= inflater.inflate(R.layout.fragment_ques_ans, container, false);

        questionLists = new ArrayList<>();
        checkLoadData();

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
        ref = FirebaseDatabase.getInstance().getReference("QuestionAns");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questionLists.clear();

                for (DataSnapshot d : snapshot.getChildren()) {
                    ModelQuestionAns model = d.getValue(ModelQuestionAns.class);

                    if(category==0) {
                        questionLists.add(model);
                    }else{
                        if (model.getCollection().equals(categoryList[category])) {
                            questionLists.add(model);
                        }
                    }

                }
                loadShowFragment();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPopularBycategory() {
        ref=FirebaseDatabase.getInstance().getReference("QuestionAns");
        Query query=ref.orderByChild("point");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                questionLists.clear();
                for(DataSnapshot d:snapshot.getChildren()){
                    ModelQuestionAns model=d.getValue(ModelQuestionAns.class);

                    if(category==0){
                        questionLists.add(model);
                    }else{
                        if(model.getCollection().equals(categoryList[category])){
                            questionLists.add(model);
                        }
                    }

                }
                loadShowFragment();
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    private void showArraylist() {
        for (ModelQuestionAns d : questionLists) {
            Log.d(TAG, "showArraylist: model" + d.toString());
        }
    }


    private void loadShowFragment() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("list", questionLists);

        LoadListShowFragment frag = new LoadListShowFragment();
        frag.setArguments(bundle);
        try {
            FragmentTransaction tr = getChildFragmentManager().beginTransaction();
            tr.replace(R.id.fragList, frag, "").commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void checkUserStatus() {
        //get current user
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            Log.d(TAG, "checkUserStatus: check User::" + user.getEmail());

        } else {
            //go back to login
            startActivity(new Intent(getContext(), SplashActivity.class));
            getActivity().finish();
        }
    }

    /*inflate option menu*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_top, menu);
        //hide  icon
        menu.findItem(R.id.action_logout).setVisible(false);

        super.onCreateOptionsMenu(menu, inflater);
    }

    /*handle menu item clicked*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                firebaseAuth.signOut();
                checkUserStatus();
                break;
            case R.id.action_chat:
                startActivity(new Intent(getContext(), ChatlistActivity.class));
                break;
            case R.id.action_add:
                startActivity(new Intent(getContext(), AddQuestionActivity.class));
                break;
            case R.id.action_search:
                startActivity(new Intent(getContext(), SearchActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

}