package com.example.wongwien;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.wongwien.databinding.ActivitySearchBinding;
import com.example.wongwien.fragment.search.SearchQuestionFragment;
import com.example.wongwien.model.ModelQuestionAns;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private ActivitySearchBinding binding;

    ActionBar actionbar;
    boolean isFindReview=true;
    int category=111;
    String key[]={"","General","Course","Food","Domitory","Tours"};
    ArrayList<ModelQuestionAns>questionlist;

    FirebaseDatabase database;
    DatabaseReference ref;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        actionbar=getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle("Search");
        actionbar.setDisplayShowHomeEnabled(true);

        database=FirebaseDatabase.getInstance();

//        clearMode();
        clearFilter();
        binding.linearReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMode();
                binding.underlineReview.setBackgroundColor(getResources().getColor(R.color.primary));
                isFindReview=true;
                checkMessageAndCheckFilter();
            }
        });
        binding.linearQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearMode();
                binding.underlineQues.setBackgroundColor(getResources().getColor(R.color.primary));
                isFindReview=false;
                checkMessageAndCheckFilter();
            }
        });
        binding.txtGenneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(category!=1){
                    clearFilter();
                    binding.txtGenneral.setBackground(getResources().getDrawable(R.drawable.rec_conner_color_selected));
                    binding.txtGenneral.setTextColor(getResources().getColor(R.color.white));
                    category=1;
                    checkMessageAndCheckFilter();
                }else{
                    category=111;
                    clearFilter();
                    checkMessageAndCheckFilter();
                }

            }
        });
        binding.txtCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(category!=2){
                    clearFilter();
                    binding.txtCourse.setBackground(getResources().getDrawable(R.drawable.rec_conner_color_selected));
                    binding.txtCourse.setTextColor(getResources().getColor(R.color.white));
                    category=2;
                    checkMessageAndCheckFilter();
                }else{
                    category=111;
                    clearFilter();
                    checkMessageAndCheckFilter();
                }

            }
        });
        binding.txtFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(category!=3){
                    clearFilter();
                    binding.txtFood.setBackground(getResources().getDrawable(R.drawable.rec_conner_color_selected));
                    binding.txtFood.setTextColor(getResources().getColor(R.color.white));
                    category=3;
                    checkMessageAndCheckFilter();
                }else{
                    category=111;
                    clearFilter();
                    checkMessageAndCheckFilter();
                }

            }
        });

        binding.txtDormitory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(category!=4){
                    clearFilter();
                    binding.txtDormitory.setBackground(getResources().getDrawable(R.drawable.rec_conner_color_selected));
                    binding.txtDormitory.setTextColor(getResources().getColor(R.color.white));
                    category=4;
                    checkMessageAndCheckFilter();
                }else{
                    category=111;
                    clearFilter();
                    checkMessageAndCheckFilter();
                }

            }
        });
        binding.txtTours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(category!=5){
                    clearFilter();
                    binding.txtTours.setBackground(getResources().getDrawable(R.drawable.rec_conner_color_selected));
                    binding.txtTours.setTextColor(getResources().getColor(R.color.white));
                    category=5;
                    checkMessageAndCheckFilter();
                }else{
                    category=111;
                    clearFilter();
                    checkMessageAndCheckFilter();
                }

            }
        });
        binding.edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!TextUtils.isEmpty(s)){
                    message=s.toString();

                    if(isFindReview){
                        Log.d(TAG, "checkMessageAndCheckFilter: isFindReview::"+isFindReview);
                    }else{
                        Log.d(TAG, "checkMessageAndCheckFilter: isFindReview::"+isFindReview);
                        Log.d(TAG, "onTextChanged: form EDSEARCH");
                        if (checkFilterQuestion()) {
                            findQuestionWithFilter(s.toString());
                        } else {
                            findAllQuestion(s.toString());
                        }
                    }
                    binding.linearShowResult.setVisibility(View.VISIBLE);
                    binding.linearShowEmpty.setVisibility(View.GONE);
                }else{
                    binding.linearShowResult.setVisibility(View.GONE);
                    binding.linearShowEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void checkMessageAndCheckFilter() {
        if(!TextUtils.isEmpty(message)){
            if(isFindReview){
                Log.d(TAG, "checkMessageAndCheckFilter: isFindReview::"+isFindReview);
                Toast.makeText(this, "isFindReview::"+isFindReview, Toast.LENGTH_SHORT).show();
            }else{
                Log.d(TAG, "checkMessageAndCheckFilter: isFindReview::"+isFindReview);
                Log.d(TAG, "checkMessageAndCheckFilter: from checkMessageAndCheckFilter");
                if (checkFilterQuestion()) {
                    findQuestionWithFilter(message.toString());
                } else {
                    findAllQuestion(message.toString());
                }
            }
            binding.linearShowResult.setVisibility(View.VISIBLE);
            binding.linearShowEmpty.setVisibility(View.GONE);
        }else {
            binding.linearShowResult.setVisibility(View.GONE);
            binding.linearShowEmpty.setVisibility(View.VISIBLE);
        }
    }

    private void clearMode(){
        binding.underlineReview.setBackgroundColor(getResources().getColor(R.color.white));
        binding.underlineQues.setBackgroundColor(getResources().getColor(R.color.white));
    }
    private void clearFilter(){
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


    private void findAllQuestion(String s) {
        questionlist=new ArrayList<>();

        ref=database.getReference("QuestionAns");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for(DataSnapshot d:snapshot.getChildren()){
                    ModelQuestionAns model=d.getValue(ModelQuestionAns.class);

                    if(model.getQuestion().toLowerCase().contains(s.toLowerCase())||
                            model.getDescrip().toLowerCase().contains(s.toLowerCase())||
                            model.getTag().toLowerCase().contains(s.toLowerCase())||
                            model.getuName().toLowerCase().contains(s.toLowerCase())||
                            model.getuEmail().toLowerCase().contains(s.toLowerCase())){
                        questionlist.add(model);
                    }
                }
                loadQuestionToFragment(questionlist);
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    private boolean checkFilterQuestion(){
        if(category==111){
            return false;
        }
        return true;
    }

    private void findQuestionWithFilter(String s) {
        questionlist=new ArrayList<>();

        String keyword=key[category];
        ref=database.getReference("QuestionAns");
        Query query=ref.orderByChild("collection").equalTo(keyword);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                questionlist.clear();

                for(DataSnapshot d:snapshot.getChildren()){

                    ModelQuestionAns model=d.getValue(ModelQuestionAns.class);
                    if(model.getQuestion().toLowerCase().contains(s.toLowerCase())||
                    model.getDescrip().toLowerCase().contains(s.toLowerCase())||
                    model.getTag().toLowerCase().contains(s.toLowerCase())||
                    model.getuName().toLowerCase().contains(s.toLowerCase())||
                    model.getuEmail().toLowerCase().contains(s.toLowerCase())){
                        questionlist.add(model);
                    }
                }

                loadQuestionToFragment(questionlist);
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

    }


    private void loadQuestionToFragment(ArrayList<ModelQuestionAns> questionlist) {
        Bundle bundle=new Bundle();
        bundle.putParcelableArrayList("list", questionlist);
//        showList();
        SearchQuestionFragment frag=new SearchQuestionFragment();
        frag.setArguments(bundle);

        FragmentTransaction fr3=getSupportFragmentManager().beginTransaction();
        fr3.replace(R.id.fraglistSearch,frag,"");
        fr3.commit();
    }

    private void showList(){
        for(ModelQuestionAns d:questionlist){
            Log.d(TAG, "showList: "+d.toString());
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}