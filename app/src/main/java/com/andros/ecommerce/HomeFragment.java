package com.andros.ecommerce;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.andros.ecommerce.activities.ItemDetailsActivity;
import com.andros.ecommerce.adapters.ItemAdapter;
import com.andros.ecommerce.models.Cart;
import com.andros.ecommerce.models.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements ItemAdapter.OnClickShowListener {
    RecyclerView recyclerView;
    List<Item> itemDataHolder = new ArrayList<>();
    ItemAdapter itemAdapter;
    SearchView searchBar;

    FrameLayout btnCart;
    NotificationBadge notificationBadge;

    String INSTANCE_URL_DATABASE = "https://ecommerce-dee3d-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    public void onResume() {
        super.onResume();
        countCartItem();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = v.findViewById(R.id.item_recycle_view);
        searchBar = v.findViewById(R.id.search_bar);
        notificationBadge = v.findViewById(R.id.badge);
        btnCart = v.findViewById(R.id.cart_button_frame);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        itemAdapter = new ItemAdapter(itemDataHolder,this);

        btnCart.setOnClickListener(view ->{
            Intent intent = new Intent(getActivity(),CartActivity.class);
            startActivity(intent);
        });

        getItem(v);

        getFilteredItem();
        countCartItem();
        return v;
    }

    private void getItem(View v){
        ItemClient call = new ItemClient();

        call.getApi().getItem().enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                Log.d("TAG","RESPONSE");
                if (response.isSuccessful() && response.body() != null) {
                    itemDataHolder = response.body();

                    itemAdapter.setData(itemDataHolder);
                    recyclerView.setAdapter(itemAdapter);
                } else if (response.errorBody() != null) {
                    try {
                        JSONObject error = new JSONObject(response.errorBody().string());
                        String message = error.getString("message");
                        showToast(message);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Item>> call, Throwable t) {
                Log.d("TAG","FAIL");
            }
        });
    }

    public void getFilteredItem(){
        searchBar.clearFocus();
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filter(s);
                return true;
            }
        });
    }

    private void filter(String text) {
        List<Item> filterList = new ArrayList<>();

        for(Item item : itemDataHolder){
            if(item.getTitle().toLowerCase().contains(text.toLowerCase())){
                filterList.add(item);
            }
        }
        itemAdapter.setData(filterList);
    }

    @Override
    public void onClickShowListener(Item item) {
        Intent intent = new Intent(getActivity(), ItemDetailsActivity.class).putExtra("data", item);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }


    public void onItemLoadSuccess(List<Cart> cartList) {
        int cartSum=0;

        for(Cart cart: cartList){
            cartSum+=cart.getQuantity();
        }
        notificationBadge.setNumber(cartSum);
    }

    private void countCartItem(){
        List<Cart> carts = new ArrayList<>();

        FirebaseDatabase.getInstance(INSTANCE_URL_DATABASE).getReference("Cart")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot cartSnapshot: snapshot.getChildren()){
                            Cart cart = cartSnapshot.getValue(Cart.class);
                            cart.setId(Integer.parseInt(cartSnapshot.getKey()));
                            carts.add(cart);
                        }
                        onItemLoadSuccess(carts);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showToast(error.getMessage());
                    }
                });
    }
}