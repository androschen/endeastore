package com.andros.ecommerce.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.metrics.Event;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.andros.ecommerce.R;
import com.andros.ecommerce.UpdateCartEvent;
import com.andros.ecommerce.adapters.CartAdapter;
import com.andros.ecommerce.models.Cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView buttonBack;
    TextView totalPrice;
    Button checkout;

    String INSTANCE_URL_DATABASE = "https://ecommerce-dee3d-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onStop() {
        super.onStop();
        if(EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent.class)){
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent.class);
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onUpdateCart(UpdateCartEvent event){
        loadCartfromFirebase();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.cart_recycle_view);
        buttonBack = findViewById(R.id.button_back);
        totalPrice = findViewById(R.id.total_price);
        checkout = findViewById(R.id.checkout);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayout);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,linearLayout.getOrientation()));

        buttonBack.setOnClickListener(view->{
            finish();
        });

        loadCartfromFirebase();

    }

    private void loadCartfromFirebase(){
        List<Cart> cartList= new ArrayList<>();
        FirebaseDatabase.getInstance(INSTANCE_URL_DATABASE)
                .getReference("Cart")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot cartSnapshot: snapshot.getChildren()){
                                Cart cart = cartSnapshot.getValue(Cart.class);
                                cart.setId(Integer.parseInt(cartSnapshot.getKey()));
                                cartList.add(cart);
                            }
                            Log.d("TAG","SUCCES LOAD CART");
                            onCartLoadSuccess(cartList);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void onCartLoadSuccess(List<Cart> cartList){
        double sum= 0;
        for(Cart cart :cartList){
            sum+=cart.getTotalPrice();
        }
        CartAdapter cartAdapter = new CartAdapter(cartList,this);
        final DecimalFormat df = new DecimalFormat("0.00");
        totalPrice.setText("$"+String.valueOf(df.format(sum)));
        recyclerView.setAdapter(cartAdapter);

    }
}