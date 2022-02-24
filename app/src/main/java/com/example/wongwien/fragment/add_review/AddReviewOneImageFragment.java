package com.example.wongwien.fragment.add_review;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wongwien.MapsActivity;
import com.example.wongwien.R;
import com.example.wongwien.databinding.FragmentAddReviewOneImgBinding;
import com.example.wongwien.model.ModelMylocation;
import com.example.wongwien.model.ModelReview;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import javax.xml.transform.Result;

import static android.app.Activity.RESULT_OK;

import java.util.HashMap;

public class AddReviewOneImageFragment extends Fragment {
    public static final int ERROR_DIALOG_REQUESST = 9001;
    public static final int MY_LOCATION = 9002;
    private static final String TAG = "AddReviewOneImageFragme";
    //permissions constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    //permission pick image constants
    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 400;
    HashMap<String, String> mylocation;
    Uri image_uri;
    ModelReview review;
    DatabaseReference ref;
    FirebaseStorage storage;
    ProgressDialog progressDialog;
    boolean isUpdate = false;
    private FragmentAddReviewOneImgBinding binding;
    private GetAllDataToActivity getdata;

    ModelMylocation location;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddReviewOneImgBinding.inflate(inflater);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.collections, R.layout.spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCollection.setAdapter(adapter);

        progressDialog = new ProgressDialog(getContext());

        mylocation = new HashMap<>();
        Bundle bundle = getArguments();
        if (bundle != null) {
            review = bundle.getParcelable("list");

            binding.edTitle.setText(review.getR_title());
            binding.edDescription.setText(review.getR_desc0());

            String image = review.getR_image0();
            try {
                if (image != null || !image.equals("")) {
                    changeImageToImageView(binding.tvImage, image);
                    image_uri = Uri.parse(image);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String tag = review.getR_tag();
            if (!tag.equals("")) {
                String tags[] = tag.split("::");
                for (String s : tags) {
                    if (!s.equals("")) {
                        addToChipGroup(s);
                    }
                }
            }
            isUpdate = true;

            loadLocation(review.getrId());
            binding.btnPost.setText("Update");
            binding.btnPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String rId = review.getrId();

                    String tag = getAllTag();
                    String title = binding.edTitle.getText().toString().trim();
                    String collection = binding.spinnerCollection.getSelectedItem().toString();
                    String descipt = binding.edDescription.getText().toString().trim();

                    ref = FirebaseDatabase.getInstance().getReference("Reviews").child(rId);
                    ref.child("r_title").setValue(title);
                    ref.child("r_desc0").setValue(descipt);
                    ref.child("r_image0").setValue(String.valueOf(image_uri));
                    ref.child("r_collection").setValue(collection);
                    ref.child("r_tag").setValue(tag).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            ref= FirebaseDatabase.getInstance().getReference("Reviews").child(rId).child("Mylocation");
                            ref.child("map_title").setValue(mylocation.get("map_title"));
                            ref.child("address").setValue(mylocation.get("address"));
                            ref.child("latitude").setValue(mylocation.get("latitude"));
                            ref.child("longitude").setValue(mylocation.get("longitude"))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            getdata.updateReview(true);
                                        }
                                    });
                        }
                    });
                }
            });

        } else {
            binding.btnPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadImage(image_uri);
                }
            });
        }

        binding.txtAddtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.showTag.getVisibility() == View.VISIBLE) {
                    binding.showTag.setVisibility(View.GONE);
                } else {
                    binding.showTag.setVisibility(View.VISIBLE);
                    binding.edTag.requestFocus();
                }
            }
        });

        binding.txtAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isService()) {
                    Intent intent = new Intent(getContext(), MapsActivity.class);
                    startActivityForResult(intent, MY_LOCATION);
                }
            }
        });

        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = binding.edTag.getText().toString().trim();
                if (!TextUtils.isEmpty(tag)) {
                    addToChipGroup(tag);
                    binding.edTag.setText("");

                    hideKeyboard(getActivity());
                }
            }
        });

        /*
         * first  show dialog--> check permission
         * return true pick image
         *        false request permission--> on result permission
         *
         * to pick image--> intent code to pick uri --> on activity on result
         * return image by code id
         * */
        binding.tvImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String options[] = {"Camera", "Gallery"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                builder.setTitle("Pick Image From");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (!checkCameraPermission()) {
                                    requestCameraPermission();
                                } else {
                                    pickFromCamera();
                                }
                                break;
                            case 1:
                                if (!checkStoragePermission()) {
                                    requestStoragePermissions();
                                } else {
                                    pickFromGallery();
                                }
                                break;

                        }
                    }
                });

                builder.create().show();
            }
        });


        return binding.getRoot();
    }

    public boolean isService() {
        int avalible = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
        if (avalible == ConnectionResult.SUCCESS) {
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(avalible)) {
            //error occured but we can resolve it
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog((Activity) getContext(), avalible, ERROR_DIALOG_REQUESST);
            dialog.show();
        } else {
            Toast.makeText(getContext(), "Something occurs you can't make map requests ", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case IMAGE_PICK_CAMERA_REQUEST_CODE:
                    changeImageToImageView(binding.tvImage, image_uri);
                    break;

                case IMAGE_PICK_GALLERY_REQUEST_CODE:
                    image_uri = data.getData();

                    changeImageToImageView(binding.tvImage, image_uri);
                    break;

                case MY_LOCATION:
                    // here you can retrieve your bundle data.
                    String yourdata = data.getStringExtra("Sent");

                    mylocation.put("map_title", data.getStringExtra("title"));
                    mylocation.put("address", data.getStringExtra("address"));
                    mylocation.put("latitude", data.getStringExtra("loc1"));
                    mylocation.put("longitude", data.getStringExtra("loc2"));

                    Log.d(TAG, "onActivityResult: yourdata::" + yourdata);
                    Log.d(TAG, "onActivityResult: yourdata::" + mylocation.get(0));

                    showAddress();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showAddress() {
        if (mylocation.size() > 0) {
            binding.txtAddLocation.setVisibility(View.GONE);
            binding.showAddress.setVisibility(View.VISIBLE);
            binding.showAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(getContext(), MapsActivity.class);
                    intent.putExtra("map_title",mylocation.get("map_title"));
                    intent.putExtra("map_address",mylocation.get("address"));
                    intent.putExtra("map_la",mylocation.get("latitude"));
                    intent.putExtra("map_lo",mylocation.get("longitude"));
                    startActivityForResult(intent,MY_LOCATION);
                }
            });
            binding.txtMapTitle.setText(mylocation.get("map_title"));
            binding.txtShowAddress.setText(mylocation.get("address"));
        }
    }

    public void changeImageToImageView(ImageView imageView, String img) {
        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        try {
            Picasso.get().load(img).into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeImageToImageView(ImageView imageView, Uri img) {
        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setImageURI(img);

        if (isUpdate) {
            progressDialog.setMessage("Upload image...");
            progressDialog.show();

            String timeStamp = String.valueOf(System.currentTimeMillis());

            String filePathAndName = "Review_image/" + review.getuId() + "_" + timeStamp;
            storage = FirebaseStorage.getInstance();

            StorageReference s_ref = storage.getReference().child(filePathAndName);
            s_ref.putFile(img).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask = s_ref.putFile(img).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return s_ref.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                Log.d(TAG, "onComplete: img Uri::" + downloadUri);

                                image_uri = downloadUri;
                                progressDialog.dismiss();
                            }
                        }
                    });
                }
            });
        }
    }
    private void loadLocation(String rId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Reviews").child(rId).child("Mylocation");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                try{
                    location = snapshot.getValue(ModelMylocation.class);
                    Log.d(TAG, "onDataChange: location::"+mylocation);
                    if(location!=null){
                        binding.showAddress.setVisibility(View.VISIBLE);
                        binding.txtAddLocation.setVisibility(View.GONE);

                        String title=location.getMap_title();
                        if(title.equals("My Location")){
                            title=review.getuName()+" Location";
                        }
                        binding.txtMapTitle.setText(title);
                        binding.txtShowAddress.setText(location.getAddress());

                        mylocation.put("map_title",location.getMap_title());
                        mylocation.put("address",location.getAddress());
                        mylocation.put("latitude",location.getLatitude());
                        mylocation.put("longitude",location.getLongitude());

                        binding.showAddress.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent=new Intent(getContext(), MapsActivity.class);
                                String maptitle=location.getMap_title();
                                intent.putExtra("map_title",maptitle);
                                intent.putExtra("map_address",location.getAddress());
                                intent.putExtra("map_lo",location.getLongitude());
                                intent.putExtra("map_la",location.getLatitude());
                                startActivityForResult(intent,MY_LOCATION);
                            }
                        });
                    }else{
                        binding.txtShowAddress.setText("hello");
                        binding.showAddress.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void uploadImage(Uri image_uri) {
        String tag = getAllTag();
        String title = binding.edTitle.getText().toString().trim();
        String collection = binding.spinnerCollection.getSelectedItem().toString();
        String descipt = binding.edDescription.getText().toString().trim();

        if (!TextUtils.isEmpty(title) || !TextUtils.isEmpty(descipt)) {

            if (TextUtils.isEmpty(tag)) {
                tag = "";
            }

            if (!image_uri.equals("") && image_uri != null) {
                Log.d(TAG, "uploadImage: mylocation :"+mylocation);
                getdata.uploadReviewWithOneImage(title, descipt, tag, collection, image_uri, mylocation);

//                Log.d(TAG, "uploadImage: upload image::"+image_uri);
//                Picasso.get().load(image_uri).into(binding.tvImage);
            }else{
                Toast.makeText(getContext(), "Image can't be empty", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Please fill all", Toast.LENGTH_SHORT).show();
        }
    }

    private void pickFromGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setType("image/*");
        startActivityForResult(gallery, IMAGE_PICK_GALLERY_REQUEST_CODE);
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "wongwieng");
        values.put(MediaStore.Images.Media.DESCRIPTION, "wongwieng review");

        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(getActivity(), "Plase enable camera & storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAcceipted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageAcceipted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(getActivity(), "Plase enable storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void requestCameraPermission() {
        String cameraPermissions[] = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void requestStoragePermissions() {
        String storagePermissions[] = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }


    private boolean checkCameraPermission() {
        boolean result = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result && result2;
    }

    private boolean checkStoragePermission() {
        boolean result2 = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result2;
    }

    private void addToChipGroup(String tag) {
        Chip chip = new Chip(getContext());
        ChipDrawable chipDrawable = ChipDrawable.createFromAttributes(getContext(),
                null,
                0,
                R.style.chipCustom);
        chip.setChipDrawable(chipDrawable);

        chip.setText(tag);
        chip.setCloseIconEnabled(true);
        chip.setTextAppearance(R.style.chipCustom);
        chip.setClickable(false);
        chip.setCheckable(false);

        binding.chipGroup.addView(chip);

        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.chipGroup.removeView(chip);
            }
        });

    }

    private void hideKeyboard(FragmentActivity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private String getAllTag() {
        String txtTag = "";

        for (int i = 0; i < binding.chipGroup.getChildCount(); i++) {
            txtTag = txtTag + "::" + ((Chip) binding.chipGroup.getChildAt(i)).getText().toString();
        }
        return txtTag;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        getdata = (GetAllDataToActivity) getActivity();
        super.onAttach(context);
    }

}