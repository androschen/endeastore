package com.andros.ecommerce.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.andros.ecommerce.R;
import com.google.firebase.auth.FirebaseAuth;

public class SettingActivity extends AppCompatActivity {
    Button logoutButton;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        logoutButton = findViewById(R.id.logout_button);

        mAuth = FirebaseAuth.getInstance();

        logoutButton.setOnClickListener(v->{
            mAuth.signOut();
            Intent intent = new Intent(SettingActivity.this,LoginActivity.class);
            startActivity(intent);
//            finish();
        });
    }
}