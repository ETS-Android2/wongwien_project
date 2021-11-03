package com.example.wongwien.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.wongwien.ChatlistActivity;
import com.example.wongwien.MainActivity;
import com.example.wongwien.R;
import com.example.wongwien.SearchActivity;
import com.example.wongwien.SplashActivity;
import com.example.wongwien.WelcomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ReviewsFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private static final String TAG = "ReviewsFragment";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true); //show menu option in fragment
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_reviews, container, false);

        //firebase Auth
        firebaseAuth =FirebaseAuth.getInstance();

        return view;
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