package com.andros.ecommerce;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andros.ecommerce.models.Item;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.myviewholder>{

    List<Item> itemDataHolder;
    OnClickShowListener mOnClickShowListener;

    public ItemAdapter(List<Item> itemDataHolder, OnClickShowListener onClickShowListener) {
        this.itemDataHolder = itemDataHolder;
        this.mOnClickShowListener = onClickShowListener;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row,parent, false);
        return new myviewholder(view, mOnClickShowListener);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        Item res = itemDataHolder.get(position);

        Picasso.get().load(res.getImage()).into(holder.img);
        holder.title.setText(res.getTitle());
        holder.category.setText(res.getCategory());
        holder.price.setText("$"+res.getPrice().toString());

        holder.itemView.setOnClickListener(view -> {
            mOnClickShowListener.onClickShowListener(res);
        });


    }

    @Override
    public int getItemCount() {
        return itemDataHolder.size();
    }

    class myviewholder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView title,price,category;
        OnClickShowListener onClickShowListener;
        public myviewholder(@NonNull View itemView, OnClickShowListener onClickShowListener){
            super(itemView);
            img= itemView.findViewById(R.id.item_img);
            title =itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.price);
            category=itemView.findViewById(R.id.category);
        }

    }

    public interface OnClickShowListener{
        void onClickShowListener(Item item);
    }

    public void setData(List<Item> itemDataHolder){
        this.itemDataHolder = itemDataHolder;
        notifyDataSetChanged();
    }

}

