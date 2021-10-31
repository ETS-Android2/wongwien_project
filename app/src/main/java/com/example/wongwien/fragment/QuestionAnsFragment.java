package com.example.wongwien.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.example.wongwien.WelcomeActivity;
import com.example.wongwien.fragment.question.RecentlyFragmentQuestion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class QuestionAnsFragment extends Fragment {
    private static final String TAG = "QuestionAnsFragment";
    private FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_question_ans, container, false);

        loadRecentlyFragment();

        return view;
    }

    private void loadRecentlyFragment() {
        RecentlyFragmentQuestion frag=new RecentlyFragmentQuestion();
        FragmentTransaction tr=getChildFragmentManager().beginTransaction();
        tr.replace(R.id.fragList,frag,"").commit();
    }

    private void checkUserStatus(){
        //get current user
        FirebaseUser user= firebaseAuth.getCurrentUser();
        if(user !=null){
            Log.d(TAG, "checkUserStatus: check User::"+user.getEmail());

        }else{
            //go back to login
            startActivity(new Intent(getContext(), WelcomeActivity.class));
            getActivity().finish();
        }
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
            case R.id.action_add:
                startActivity(new Intent(getContext(), AddQuestionActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}