package com.example.wongwien.fragment.add_review;

import android.net.Uri;

import java.util.ArrayList;

public interface GetAllDataToActivity {
    void uploadReviewWithNoImage(String title, String descrip, String tag,String collection);
    void uploadReviewWithOneImage(String title, String descrip, String tag, String collection, Uri image_uri);
    void uploadReviewWithVerticalImage(String title, ArrayList<String> allDescrip, ArrayList<Uri> allImageUri, String tag, String collection, int count);
    void updateReview(boolean update);
}
