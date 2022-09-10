package com.andros.ecommerce.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andros.ecommerce.R;
import com.andros.ecommerce.UpdateCartEvent;
import com.andros.ecommerce.models.Cart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    List<Cart> cartList;
    Context context;
    OnClickCartListener onClickCartListener;

    String INSTANCE_URL_DATABASE = "https://ecommerce-dee3d-default-rtdb.asia-southeast1.firebasedatabase.app";

    public CartAdapter(List<Cart> cartList, Context context) {
        this.cartList = cartList;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item,parent,false);

        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {
        Cart res = cartList.get(position);

        Picasso.get().load(res.getImage()).into(holder.img);
        holder.title.setText(res.getTitle());
        holder.price.setText("$"+res.getPrice().toString());
        holder.quantity.setText(new StringBuilder().append(res.getQuantity()));

        holder.plusBtn.setOnClickListener(v->{
            plusCartItem(holder,cartList.get(position));
        });

        holder.minBtn.setOnClickListener(v->{
            minusCartItem(holder,cartList.get(position));
        });

    }

    private void plusCartItem(CartViewHolder holder, Cart cart){
        if(cart.getQuantity()<100){
            cart.setQuantity(cart.getQuantity()+1);
            cart.setTotalPrice(cart.getQuantity()*cart.getPrice());

            holder.quantity.setText(new StringBuilder().append(cart.getQuantity()));
            updateFirebase(cart);
        }
    }

    private void minusCartItem(CartViewHolder holder, Cart cart){
        if(cart.getQuantity()>1){
            cart.setQuantity(cart.getQuantity()-1);
            cart.setTotalPrice(cart.getQuantity()*cart.getPrice());

            holder.quantity.setText(new StringBuilder().append(cart.getQuantity()));
            updateFirebase(cart);
        }else{
            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setTitle("Delete item")
                    .setMessage("Are you sure to delete this item?")
                    .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        deleteFromFirebase(cart);

                        dialogInterface.dismiss();
                    }).create();

            dialog.show();
        }
    }

    private void updateFirebase(Cart cart){
        FirebaseDatabase.getInstance(INSTANCE_URL_DATABASE)
                .getReference("Cart")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(String.valueOf(cart.getId()))
                .setValue(cart)
                .addOnSuccessListener(v->{
                   Log.d("Cart","UPDATE");
                   EventBus.getDefault().postSticky(new UpdateCartEvent());
                });
    }

    private void deleteFromFirebase(Cart cart){
        FirebaseDatabase.getInstance(INSTANCE_URL_DATABASE)
                .getReference("Cart")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(String.valueOf(cart.getId()))
                .removeValue()
                .addOnSuccessListener(v->{
                    Log.d("Cart","Delete");
                    EventBus.getDefault().postSticky(new UpdateCartEvent());
                });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView title,price,quantity;
        ImageView minBtn, plusBtn;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            img= itemView.findViewById(R.id.item_img);
            title =itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            quantity = itemView.findViewById(R.id.text_quantity);
            minBtn = itemView.findViewById(R.id.minus_btn);
            plusBtn = itemView.findViewById(R.id.plus_btn);
        }
    }

    public interface OnClickCartListener{
        void onClickCartListener(Cart cart);
    }

}
