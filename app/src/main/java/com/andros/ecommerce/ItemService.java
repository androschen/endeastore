package com.andros.ecommerce;

import com.andros.ecommerce.models.Item;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ItemService{

    @GET("products")
    Call<List<Item>> getItem();
}