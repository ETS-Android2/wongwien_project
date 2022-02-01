package com.example.wongwien;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //endble firebase offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

//        By default, 10MB of previously synced data is cached.
//        This should be enough for most applications.
//        If the cache outgrows its configured size,
//        the Firebase Realtime Database purges data that has been used least recently.
//        Data that is kept in sync is not purged from the cache.
    }
}