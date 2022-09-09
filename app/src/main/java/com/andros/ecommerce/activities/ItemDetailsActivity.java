package com.andros.ecommerce.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.andros.ecommerce.ICartLoadListener;
import com.andros.ecommerce.models.Cart;
import com.andros.ecommerce.models.Item;
import com.andros.ecommerce.R;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ItemDetailsActivity extends AppCompatActivity {
    Item item;
    int id;
    ImageView imageView;
    TextView title, category, price, description;
    ElegantNumberButton elegantNumberButton;
    Button addCartBtn;
    ICartLoadListener iCartLoadListener;

    String INSTANCE_URL_DATABASE = "https://ecommerce-dee3d-default-rtdb.asia-southeast1.firebasedatabase.app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        imageView = findViewById(R.id.image);
        title = findViewById(R.id.title);
        category = findViewById(R.id.category);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);
        elegantNumberButton = findViewById(R.id.number_btn);
        addCartBtn = findViewById(R.id.add_cart_btn);

        getItemDetails();

        addCartBtn.setOnClickListener(view->{
            addToCart(item);
        });
    }

    private void getItemDetails(){
        Bundle extras = getIntent().getExtras();

        if(extras!=null){
            item = (Item) getIntent().getSerializableExtra("data");

            String titleContent = item.getTitle();
            String categoryContent = item.getCategory();
            Double priceContent = item.getPrice();
            String descContent= item.getDescription();
            String imageContent = item.getImage();

            Picasso.get().load(imageContent).into(imageView);
            title.setText(titleContent);
            category.setText(categoryContent);
            price.setText("$"+ priceContent.toString());
            description.setText(descContent);

        }
    }

    private void addToCart(Item item) {
        DatabaseReference userCart = FirebaseDatabase
                .getInstance(INSTANCE_URL_DATABASE)
                .getReference("Cart")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        userCart.child(String.valueOf(item.getId()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int quantity = Integer.parseInt(elegantNumberButton.getNumber());
                        if(snapshot.exists()){
                            Cart cart = snapshot.getValue(Cart.class);
                            HashMap<String,Object> updateCartMap = new HashMap<>();
                            cart.setQuantity(cart.getQuantity()+quantity);
                            cart.setTotalPrice(cart.getTotalPrice()+(cart.getPrice()*quantity));
                            updateCartMap.put("quantity", cart.getQuantity());
                            updateCartMap.put("totalPrice", cart.getTotalPrice());

                            userCart.child(String.valueOf(item.getId()))
                                    .updateChildren(updateCartMap)
                                    .addOnSuccessListener(v->{
//                                        iCartLoadListener.onItemLoadFailed("Add To Cart");
                                        showToast("Update Cart");
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
//                                            iCartLoadListener.onItemLoadFailed(e.getMessage());
                                        }
                                    });

                        }else{
                            Cart cart = new Cart();
                            cart.setTitle(item.getTitle());
                            cart.setId(item.getId());
                            cart.setImage(item.getImage());
                            cart.setPrice(item.getPrice());
                            cart.setTotalPrice(item.getPrice()*quantity);
                            cart.setQuantity(quantity);

                            userCart.child(String.valueOf(item.getId()))
                                    .setValue(cart)
                                    .addOnSuccessListener(v->{
//                                        iCartLoadListener.onItemLoadFailed("Add To Cart");
                                        showToast("Add To Cart");
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
//                                            iCartLoadListener.onItemLoadFailed(e.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(ItemDetailsActivity.this, message, Toast.LENGTH_LONG).show();
    }
}
