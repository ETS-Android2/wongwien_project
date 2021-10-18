package com.example.wongwien;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    ProgressDialog progressDialog;
    private EditText edEmail, edPassword, edPassword2,edName;
    private Button btnRegister;
    private TextView tvLogin;
    private FirebaseAuth mAuth;
    FirebaseDatabase database ;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        iniView();

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=edName.getText().toString().trim();
                String email = edEmail.getText().toString().trim();
                String password = edPassword.getText().toString().trim();
                String password2 = edPassword2.getText().toString().trim();

                //validate
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    edEmail.setError("Invalid Email");
                    edEmail.setFocusable(true);
                } else if (password.length() < 6) {
                    edPassword.setError("Password length at least 6 characters");
                    edPassword.setFocusable(true);
                } else if (!password.equals(password2)) {
                    edPassword2.setError("Password does not match");
                    edPassword2.setFocusable(true);
                } else {
                    registerUser(name,email, password);
                }
            }
        });
    }

    private void registerUser(String name, String email, String password) {
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            progressDialog.dismiss();

                            FirebaseUser user = mAuth.getCurrentUser();

                            //get user and uid from auth
                            String email=user.getEmail();
                            String uid=user.getUid();

                            HashMap hash=new HashMap();
                            hash.put("email",email);
                            hash.put("uid",uid);
                            hash.put("name",name);
                            hash.put("image","");
                            hash.put("cover_image","");

                             ref = database.getReference("Users");
                             ref.child(uid).setValue(hash);


//                            Toast.makeText(RegisterActivity.this, "Registered..."+user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this,MainActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void iniView() {
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        edPassword2 = findViewById(R.id.edPassword2);
        btnRegister = findViewById(R.id.btnRegister2);
        tvLogin = findViewById(R.id.btnRecovery);
        edName=findViewById(R.id.edName);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        //progressbar to display
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        //firebase database
         database = FirebaseDatabase.getInstance();
    }
}