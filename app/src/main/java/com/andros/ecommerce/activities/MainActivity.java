package com.andros.ecommerce.activities;


import android.os.Bundle;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.andros.ecommerce.HomeFragment;
import com.andros.ecommerce.ProfileFragment;
import com.andros.ecommerce.databinding.ActivityMainBinding;

import com.andros.ecommerce.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;




public class MainActivity extends AppCompatActivity{
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        String fragmentName = "home";

        switch (fragmentName){
            case "home":
                replaceFragment(new HomeFragment());
                bottomNavigationView.setSelectedItemId(R.id.home_nav);
                break;
        }

        binding.navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home_nav) {
                replaceFragment(new HomeFragment());
            }else if (itemId == R.id.shop_nav) {

            }else if (itemId == R.id.profile_nav) {
                replaceFragment(new ProfileFragment());
            }

            return true;
        });

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
    }

}