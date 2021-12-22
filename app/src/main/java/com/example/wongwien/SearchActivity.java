package com.example.wongwien;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.wongwien.databinding.ActivitySearchBinding;
import com.example.wongwien.fragment.search.SearchQuestionFragment;
import com.example.wongwien.fragment.search.SearchReviewFragment;
import com.example.wongwien.model.ModelQuestionAns;
import com.example.wongwien.model.ModelReview;
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
    ArrayList<ModelReview>reviewlist;

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

        Intent intent=getIntent();
        if(intent!=null){
            String tag=intent.getStringExtra("tag");
            String collection=intent.getStringExtra("collection");

            if(collection!=null && collection.equals("question")){
                isFindReview=false;
                binding.edSearch.setText(tag);
                message=tag;

                clearMode();
                binding.underlineQues.setBackgroundColor(getResources().getColor(R.color.primary));
                checkMessageAndCheckFilter();
            }else{
                isFindReview=true;
                binding.edSearch.setText(tag);
                message=tag;

                clearMode();
                binding.underlineReview.setBackgroundColor(getResources().getColor(R.color.primary));
                checkMessageAndCheckFilter();
            }
        }


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
                        if (checkFilterCategory()) {
                            findReviewWithFilter(s.toString());
                        } else {
                            findAllReview(s.toString());
                        }
                    }else{
                        Log.d(TAG, "checkMessageAndCheckFilter: isFindReview::"+isFindReview);
                        Log.d(TAG, "onTextChanged: form EDSEARCH");
                        if (checkFilterCategory()) {
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
//                Toast.makeText(this, "isFindReview::"+isFindReview, Toast.LENGTH_SHORT).show();
                if (checkFilterCategory()) {
                    findReviewWithFilter(message.toString());
                } else {
                    findAllReview(message.toString());
                }
            }else{
                Log.d(TAG, "checkMessageAndCheckFilter: isFindReview::"+isFindReview);
                Log.d(TAG, "checkMessageAndCheckFilter: from checkMessageAndCheckFilter");
                if (checkFilterCategory()) {
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

            if(isFindReview){

            }else{
//                binding.edSearch.setHint("Search question,user,tag,discription,email.");
            }
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
    private void findAllReview(String s) {
        reviewlist=new ArrayList<>();

        ref=database.getReference("Reviews");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                reviewlist.clear();

                for(DataSnapshot d:snapshot.getChildren()){
                    ModelReview model=d.getValue(ModelReview.class);

                    if(model.getR_title().toLowerCase().contains(s.toLowerCase())||
                            model.getR_desc0().toLowerCase().contains(s.toLowerCase())||

                            checkContainMessage(model,s)||

                            model.getR_tag().toLowerCase().contains(s.toLowerCase())||
                            model.getuName().toLowerCase().contains(s.toLowerCase())||
                            model.getuEmail().toLowerCase().contains(s.toLowerCase())){
                        reviewlist.add(model);
                    }
                }
                loadReviewToFragment(reviewlist);
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }
    private boolean checkContainMessage(ModelReview model,String s){
        try{
            if(
                    model.getR_desc1().toLowerCase().contains(s.toLowerCase())||
                            model.getR_desc2().toLowerCase().contains(s.toLowerCase())||
                            model.getR_desc3().toLowerCase().contains(s.toLowerCase())
            ){
                return true;
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

    private boolean checkFilterCategory(){
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
    private void findReviewWithFilter(String s) {
        reviewlist=new ArrayList<>();

        String keyword=key[category];
        ref=database.getReference("Reviews");
        Query query=ref.orderByChild("r_collection").equalTo(keyword);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                reviewlist.clear();

                for(DataSnapshot d:snapshot.getChildren()){

                    ModelReview model=d.getValue(ModelReview.class);
                    if(model.getR_title().toLowerCase().contains(s.toLowerCase())||
                            model.getR_desc0().toLowerCase().contains(s.toLowerCase())||
                            checkContainMessage(model,s)||
                            model.getR_tag().toLowerCase().contains(s.toLowerCase())||
                            model.getuName().toLowerCase().contains(s.toLowerCase())||
                            model.getuEmail().toLowerCase().contains(s.toLowerCase())){
                        reviewlist.add(model);
                    }
                }

                loadReviewToFragment(reviewlist);
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
    private void loadReviewToFragment(ArrayList<ModelReview> reviewlist) {
        Bundle bundle=new Bundle();
        bundle.putParcelableArrayList("list", reviewlist);
//        showList();
        SearchReviewFragment frag=new SearchReviewFragment();
        frag.setArguments(bundle);

        FragmentTransaction fr3=getSupportFragmentManager().beginTransaction();
        fr3.replace(R.id.fraglistSearch,frag,"");
        fr3.commit();
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