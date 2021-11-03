package com.example.wongwien.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
    int category = 1;
    boolean isRecent = true;
    ArrayList<ModelQuestionAns> questionLists;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference ref;
    private FragmentQuesAnsBinding binding;

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

        binding.txtGenneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllSelected();
                binding.txtGenneral.setTextColor(getResources().getColor(R.color.white));
                binding.txtGenneral.setBackground(getResources().getDrawable(R.drawable.rec_conner_color_selected));
                category = 1;
                checkLoadData();
            }
        });
        binding.txtCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllSelected();
                binding.txtCourse.setTextColor(getResources().getColor(R.color.white));
                binding.txtCourse.setBackground(getResources().getDrawable(R.drawable.rec_conner_color_selected));
                category = 2;
                checkLoadData();
            }
        });
        binding.txtDormitory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllSelected();
                binding.txtDormitory.setTextColor(getResources().getColor(R.color.white));
                binding.txtDormitory.setBackground(getResources().getDrawable(R.drawable.rec_conner_color_selected));
                category = 4;
                checkLoadData();
            }
        });
        binding.txtFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllSelected();
                binding.txtFood.setTextColor(getResources().getColor(R.color.white));
                binding.txtFood.setBackground(getResources().getDrawable(R.drawable.rec_conner_color_selected));
                category = 3;
                checkLoadData();
            }
        });
        binding.txtTours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllSelected();
                binding.txtTours.setTextColor(getResources().getColor(R.color.white));
                binding.txtTours.setBackground(getResources().getDrawable(R.drawable.rec_conner_color_selected));
                category = 5;
                checkLoadData();
            }
        });

        return view;
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
            switch (category) {
                case 1:
                    loadGeneral();
                    break;
                case 2:
                    loadCourse();
                    break;
                case 3:
                    loadFood();
                    break;
                case 4:
                    loadDomitory();
                    break;
                case 5:
                    loadTours();
                    break;
                default:
                    loadGeneral();
                    break;
            }

        } else {
            // TODO: 11/1/2021 loadPopular
        }
    }

    private void showArraylist() {
        for (ModelQuestionAns d : questionLists) {
            Log.d(TAG, "showArraylist: model" + d.toString());
        }
    }

    private void loadTours() {
        ref = FirebaseDatabase.getInstance().getReference("QuestionAns");
        Query query = ref.orderByChild("collection").equalTo("Tours");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questionLists.clear();

                for (DataSnapshot d : snapshot.getChildren()) {
                    ModelQuestionAns model = d.getValue(ModelQuestionAns.class);
                    if (model.getCollection().equals("Tours")) {
                        questionLists.add(model);
                    }
                }
                loadShowFragment();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadDomitory() {
        ref = FirebaseDatabase.getInstance().getReference("QuestionAns");
        Query query = ref.orderByChild("collection").equalTo("Domitory");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questionLists.clear();

                for (DataSnapshot d : snapshot.getChildren()) {
                    ModelQuestionAns model = d.getValue(ModelQuestionAns.class);
                    if (model.getCollection().equals("Domitory")) {
                        questionLists.add(model);
                    }
                }
                loadShowFragment();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadFood() {
        ref = FirebaseDatabase.getInstance().getReference("QuestionAns");
        Query query = ref.orderByChild("collection").equalTo("Food");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questionLists.clear();

                for (DataSnapshot d : snapshot.getChildren()) {
                    ModelQuestionAns model = d.getValue(ModelQuestionAns.class);
                    if (model.getCollection().equals("Food")) {
                        questionLists.add(model);
                    }
                }
                loadShowFragment();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadCourse() {
        ref = FirebaseDatabase.getInstance().getReference("QuestionAns");
        Query query = ref.orderByChild("collection").equalTo("Course");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questionLists.clear();

                for (DataSnapshot d : snapshot.getChildren()) {
                    ModelQuestionAns model = d.getValue(ModelQuestionAns.class);
                    if (model.getCollection().equals("Course")) {
                        questionLists.add(model);
                    }
                }
                loadShowFragment();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadGeneral() {
        ref = FirebaseDatabase.getInstance().getReference("QuestionAns");
        Query query = ref.orderByChild("collection").equalTo("General");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questionLists.clear();

                for (DataSnapshot d : snapshot.getChildren()) {
                    ModelQuestionAns model = d.getValue(ModelQuestionAns.class);
                    if (model != null) {
                        if (model.getCollection().equals("General")) {
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
        checkLoadData();
        super.onStart();
    }
}