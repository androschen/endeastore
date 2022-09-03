package com.andros.ecommerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.andros.ecommerce.R;
import com.andros.ecommerce.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText nameInput,emailInput,passInput,confirmPassInput;
    Button registerButton;
    ProgressBar progressBar;
    ProgressDialog progressDialog;

    private FirebaseAuth mAuth;

    String INSTANCE_URL_DATABASE = "https://ecommerce-dee3d-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameInput = findViewById(R.id.name);
        emailInput = findViewById(R.id.email);
        passInput = findViewById(R.id.password);
        confirmPassInput = findViewById(R.id.confirm_password);
        registerButton = findViewById(R.id.register_button);
        progressBar = findViewById(R.id.progress_bar);
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        registerButton.setOnClickListener(view -> {
            createAccount();
        });
    }

    private void createAccount(){
        String name = nameInput.getText().toString();
        String email = emailInput.getText().toString();
        String pass = passInput.getText().toString();
        String confirmPass = confirmPassInput.getText().toString();

        if(TextUtils.isEmpty(name)){
            showToast("Name input is required");
            nameInput.requestFocus();
        }else if(TextUtils.isEmpty(email)){
            showToast("Email input is required");
            emailInput.requestFocus();
        }else if(TextUtils.isEmpty(pass)){
            showToast("Password input is required");
            passInput.requestFocus();
        }else if(TextUtils.isEmpty(confirmPass)|| !pass.equals(confirmPass)) {
            showToast("Password doesn't match");
            confirmPassInput.requestFocus();
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showToast("Please provide valid email");
            emailInput.requestFocus();
        }else{
            progressDialog.setTitle("Create Account");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            validateUser(name,email,pass,confirmPass);
        }
    }

    private void validateUser(String name, String email, String pass, String confirmPass){
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user= new User(name,email,pass);

                            FirebaseDatabase.getInstance(INSTANCE_URL_DATABASE).getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressBar.setVisibility(View.VISIBLE);
                                        progressDialog.dismiss();
                                        showToast("Your account has been created.");
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        showToast("Network Error: Please try again.");
                                        progressBar.setVisibility(View.GONE);
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }else{
                            showToast("Network Error: Please try again.");
                            progressBar.setVisibility(View.GONE);
                            progressDialog.dismiss();
                        }
                    }
                });

    }

    private void showToast(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
    }
}