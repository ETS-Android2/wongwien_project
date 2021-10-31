package com.example.wongwien.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wongwien.ChatlistActivity;
import com.example.wongwien.MainActivity;
import com.example.wongwien.R;
import com.example.wongwien.WelcomeActivity;
import com.example.wongwien.ZoomActivity;
import com.example.wongwien.model.ModelUser;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    //permissions constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    //permission pick image constants
    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 400;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    FirebaseUser user;
    DatabaseReference ref;
    FirebaseStorage storage;

    //array of permission to be requested
    String cameraPermissions[];
    String storagePermissions[];
    Uri image_uri;
    String imgType;

    String imgPerson,imgCover;

    private TextView tvName, tvEmail;
    private CircleImageView imagePerson;
    private ImageView imageCover;
    private FloatingActionButton btnFloat;

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initView(view);
        initPermission();
        getProfileUser();


        imagePerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imgPerson!=null && !imgPerson.equals("")){
                Intent intent=new Intent(getActivity(), ZoomActivity.class);
                intent.putExtra("image",imgPerson);
                getActivity().startActivity(intent);
                }
            }
        });

        imageCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imgCover!=null && !imgCover.equals("")){
                Intent intent=new Intent(getActivity(), ZoomActivity.class);
                intent.putExtra("image",imgCover);
                getActivity().startActivity(intent);
                }
            }
        });
        return view;
    }

    private void initPermission() {
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }

    private void getProfileUser() {
        String uid = firebaseAuth.getCurrentUser().getUid();

        ref = database.getReference("Users").child(uid);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                ModelUser user = dataSnapshot.getValue(ModelUser.class);

                tvName.setText(user.getName());
                tvEmail.setText(user.getEmail());

                if (!user.getImage().equals("")) {
                    Picasso.get().load(user.getImage()).into(imagePerson);
                    imgPerson=user.getImage();
                }
                if (!user.getCover_image().equals("")) {
                    Picasso.get().load(user.getCover_image()).into(imageCover);
                    imgCover=user.getCover_image();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        ref.addValueEventListener(postListener);
    }

    private void initView(View view) {
        btnFloat = view.findViewById(R.id.btnFloat);
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        imagePerson = view.findViewById(R.id.imagePerson);
        imageCover = view.findViewById(R.id.imageCover);
        progressDialog=new ProgressDialog(getContext());

        firebaseAuth = FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

        btnFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditDialog();
            }
        });
    }

    private void showEditDialog() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        LinearLayout edName = view.findViewById(R.id.layoutEdName);
        LinearLayout edProfile = view.findViewById(R.id.layoutEdProfile);
        LinearLayout edProfielCover = view.findViewById(R.id.layoutEdProfileCover);
        LinearLayout addReview = view.findViewById(R.id.layoutAddReview);
        builder.setView(view);
        final AlertDialog show = builder.show();

        edName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();

                View view=LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_name,null,false);
                AlertDialog.Builder builder1=new AlertDialog.Builder(getContext(),R.style.CustomAlertDialog);
                Button btnRename=view.findViewById(R.id.btnRename);
                Button btnCancel=view.findViewById(R.id.btnCancel);
                EditText edName=view.findViewById(R.id.edName);
                builder1.setView(view);
                final AlertDialog show2 = builder1.show();

                btnRename.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show2.dismiss();
                       String name=edName.getText().toString().trim();
                       progressDialog.setMessage("Updating name...");
                       progressDialog.show();

                       ref=database.getReference("Users").child(user.getUid()).child("name");
                       ref.setValue(name).addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {
                               progressDialog.dismiss();
                               Toast.makeText(getContext(), "Update successfully", Toast.LENGTH_SHORT).show();
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull  Exception e) {
                               progressDialog.dismiss();
                               Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                           }
                       });
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        show2.dismiss();
                    }
                });
            }
        });

        edProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgType = "image";
                showImagePickerDialog();
                show.dismiss();
            }
        });
        edProfielCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgType = "cover_image";
                showImagePickerDialog();
                show.dismiss();
            }
        });

    }

    private void showImagePickerDialog() {
        //dialog contain camera and gallery
        String options[] = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
        builder.setTitle("Pick Image From");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /*
                 * check permission before using
                 * if not request permission
                 * */
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
                            requestStoragePermission();
                        } else {
                            pickFromGallery();
                        }
                        break;
                }
            }
        });
        builder.create().show();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private boolean checkStoragePermission() {
        /*
         * check if storage permission is enable or not
         * return true otherwise false
         * */
        boolean result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private boolean checkCameraPermission() {
        /*
         * check if camera permission is enable or not
         * return true otherwise false
         * */
        boolean result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                == (PackageManager.PERMISSION_GRANTED);
        boolean result2 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == (PackageManager.PERMISSION_DENIED);
        return result && result2;
    }

    private void requestCameraPermission() {
        //request runtime storage permission
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE);
    }

    private void requestStoragePermission() {
        //request runtime storage permission
        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        /*
         * method called when user press allow or deny from permission request dialog
         * handle permission cases(allowed &denied)
         */
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAcceipted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && writeStorageAcceipted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(getActivity(), "Plase enable camera & storage permission", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean writeStorageAcceipted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
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

    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, IMAGE_PICK_GALLERY_REQUEST_CODE);
    }

    private void pickFromCamera() {
        //intent of picking image form device camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "wongwieng");
        values.put(MediaStore.Images.Media.DESCRIPTION, "wongwieng picture");
        //put image uri
        image_uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        //intent to start camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case IMAGE_PICK_CAMERA_REQUEST_CODE:
                    //image is picked from gallery,get uri of image
                    uploadImageProfileAndCover(image_uri);
                    break;
                case IMAGE_PICK_GALLERY_REQUEST_CODE:
                    //image is picked from camera,get uri of image
                    //image_uri=data.getData();
                    image_uri = data.getData();
                    uploadImageProfileAndCover(image_uri);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadImageProfileAndCover(Uri image_uri) {
        progressDialog.setMessage("Uploading Image...");
        progressDialog.show();

        String filePathAndName="User_Profile_Image/"+imgType+user.getUid();
        StorageReference s_ref = storage.getReference().child(filePathAndName);
        s_ref.putFile(image_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> urlTask = s_ref.putFile(image_uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                                    Log.d(TAG, "onComplete: img Uri::"+downloadUri);


                                    ref=database.getReference("Users").child(user.getUid())
                                            .child(imgType);
                                    ref.setValue(String.valueOf(downloadUri)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getContext(), "Upload successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressDialog.dismiss();
                                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(getContext(), ""+task.getResult().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    /*inflate option menu*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_top,menu);

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
            startActivity(new Intent(getContext(), WelcomeActivity.class));
            getActivity().finish();
        }
    }


}
