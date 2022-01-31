package com.example.wongwien.fragment.add_review;

import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;

public interface GetAllDataToActivity {
    void uploadReviewWithNoImage(String title, String descrip, String tag, String collection, HashMap<String, String> mylocation);
    void uploadReviewWithOneImage(String title, String descrip, String tag, String collection, Uri image_uri, HashMap<String, String> mylocation);
    void uploadReviewWithVerticalImage(String title, ArrayList<String> allDescrip, ArrayList<Uri> allImageUri, String tag, String collection, int count, HashMap<String, String> mylocation);
    void updateReview(boolean update);
}
