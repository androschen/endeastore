package com.andros.ecommerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andros.ecommerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText emailInput,passInput;
    TextView registerText;
    Button loginButton;
    ProgressBar progressBar;

    FirebaseAuth mAuth;
    long timePress=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.email);
        passInput = findViewById(R.id.password);
        registerText = findViewById(R.id.register_text);
        loginButton = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progress_bar);

        mAuth = FirebaseAuth.getInstance();

        registerText.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        loginButton.setOnClickListener(view -> {
            login();
        });
    }

    private void login(){
        String email = emailInput.getText().toString();
        String pass = passInput.getText().toString();

        if(TextUtils.isEmpty(email)){
            showToast("Email input is required");
            emailInput.requestFocus();
        }else if(TextUtils.isEmpty(pass)) {
            showToast("Password input is required");
            passInput.requestFocus();
        }else{
            allowLogin(email,pass);
        }
    }

    private void allowLogin(String email, String password){
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }else{
                    progressBar.setVisibility(View.GONE);
                    showToast("Failed to login! Please check your credentials.");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Toast backToast = Toast.makeText(LoginActivity.this, "Double Press to exit", Toast.LENGTH_LONG);;
        if(timePress+2000>System.currentTimeMillis()){
            backToast.cancel();
            super.onBackPressed();
            return;
        }else{
            backToast.show();
        }
        timePress = System.currentTimeMillis();
    }

    private void showToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }
}