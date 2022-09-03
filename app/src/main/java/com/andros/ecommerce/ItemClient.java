package com.andros.ecommerce;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ItemClient {
    private static final String BASE_URL = "https://fakestoreapi.com/";

    public ItemService getApi(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(ItemService.class);
    }

}
