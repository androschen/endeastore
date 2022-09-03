package com.andros.ecommerce.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.andros.ecommerce.models.Item;
import com.andros.ecommerce.R;
import com.squareup.picasso.Picasso;

public class ItemDetailsActivity extends AppCompatActivity {
    Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        getItemDetails();
    }

    private void getItemDetails(){
        ImageView imageView = findViewById(R.id.image);
        TextView title = findViewById(R.id.title);
        TextView category = findViewById(R.id.category);
        TextView price = findViewById(R.id.price);
        TextView description = findViewById(R.id.description);

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
}
