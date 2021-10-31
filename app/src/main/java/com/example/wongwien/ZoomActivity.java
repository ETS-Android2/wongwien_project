package com.example.wongwien;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class ZoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom);

        Intent intent=getIntent();
        if(null !=intent){
            String image=intent.getStringExtra("image");
            if(image!=null){
                PhotoView photoView = (PhotoView) findViewById(R.id.zoomPhoto);
                try{
                    Picasso.get().load(image).into(photoView);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }
}