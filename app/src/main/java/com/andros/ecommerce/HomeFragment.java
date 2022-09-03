package com.andros.ecommerce;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.andros.ecommerce.activities.ItemDetailsActivity;
import com.andros.ecommerce.activities.MainActivity;
import com.andros.ecommerce.models.Item;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements ItemAdapter.OnClickShowListener{
    RecyclerView recyclerView;
    List<Item> itemDataHolder;
    ItemAdapter itemAdapter;
    SearchView searchBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = v.findViewById(R.id.item_recycle_view);
        searchBar = v.findViewById(R.id.search_bar);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        itemAdapter = new ItemAdapter(itemDataHolder,this);
        getItem(v);
        searchBar.clearFocus();
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filter(s);
                return false;
            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchBar.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

    }

    private void getItem(View v){
        ItemClient call = new ItemClient();

        call.getApi().getItem().enqueue(new Callback<List<Item>>() {
            @Override
            public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                Log.d("TAG","RESPONSE");
                if (response.isSuccessful() && response.body() != null) {
                    List<Item> itemDataHolder = response.body();

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

    public void getFilteredItem(View v){
        searchBar.clearFocus();
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filter(s);
                return false;
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
        itemAdapter.setData(itemDataHolder);
        recyclerView.setAdapter(itemAdapter);
    }

    @Override
    public void onClickShowListener(Item item) {
        Intent intent = new Intent(getActivity(), ItemDetailsActivity.class).putExtra("data", item);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

}