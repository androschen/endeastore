package com.andros.ecommerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.andros.ecommerce.R;
import com.andros.ecommerce.models.Cart;
import com.andros.ecommerce.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BalanceActivity extends AppCompatActivity {
    EditText amountInput;

    String INSTANCE_URL_DATABASE = "https://ecommerce-dee3d-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        amountInput = findViewById(R.id.amount_input);
        Button topUpButton = findViewById(R.id.top_up_btn);
        topUpButton.setOnClickListener(view -> topUpMoney());

    }

    private void topUpMoney(){
        String amount = amountInput.getText().toString();

        if(!isValidTopUp(amount)) {
            return;
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Top up")
                .setMessage("Top Up Amount: " + amount+"$")
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    topUp(amount);

                    dialogInterface.dismiss();
                }).create();

        dialog.show();
    }

    private void topUp(String amount){
        Double money = Double.parseDouble(amount);

        FirebaseDatabase.getInstance(INSTANCE_URL_DATABASE)
                .getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            user.setMoney(user.getMoney()+money);
                            updateFirebase(user);
                            amountInput.setText("");
                            showToast("Top up Success");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("TAG","FAIL");
                    }
                });
    }

    private void updateFirebase(User user){
        FirebaseDatabase.getInstance(INSTANCE_URL_DATABASE)
                .getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user)
                .addOnSuccessListener(v->{
                    Log.d("User","UPDATE");
                });
    }

    private boolean isValidTopUp(String amount) {
        boolean isValid = false;

        if (amount.matches("")) {
            showToast("Amount input is required");
        } else {
            isValid = true;
        }

        return isValid;
    }

    private void showToast(String message) {
        Toast.makeText(BalanceActivity.this, message, Toast.LENGTH_LONG).show();
    }
}