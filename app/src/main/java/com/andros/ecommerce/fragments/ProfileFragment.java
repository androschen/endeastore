package com.andros.ecommerce.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andros.ecommerce.R;
import com.andros.ecommerce.UpdateCartEvent;
import com.andros.ecommerce.models.Cart;
import com.andros.ecommerce.models.User;
import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.constant.ImageProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;


public class ProfileFragment extends Fragment {
    ImageView profileImage;
    ActivityResultLauncher<Intent> launcher;
    TextView profileName;

    String INSTANCE_URL_DATABASE = "https://ecommerce-dee3d-default-rtdb.asia-southeast1.firebasedatabase.app";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = v.findViewById(R.id.profile_image);
        profileName = v.findViewById(R.id.profile_name);

        loadProfile();
        launcher=
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),(ActivityResult result)->{
                    if(result.getResultCode()== Activity.RESULT_OK){
                        Uri uri=result.getData().getData();
                        setImageFirebase(uri);
                    }else if(result.getResultCode()== ImagePicker.RESULT_ERROR){
                        ImagePicker.Companion.getError(result.getData());
                    }
                });

        profileImage.setOnClickListener(view->{
            ImagePicker.Companion.with(getActivity())
                    .crop()
                    .cropOval()
                    .maxResultSize(512,512,true)
                    .provider(ImageProvider.BOTH) //Or bothCameraGallery()
                    .createIntentFromDialog((Function1)(new Function1(){
                        public Object invoke(Object var1){
                            this.invoke((Intent)var1);
                            return Unit.INSTANCE;
                        }

                        public final void invoke(@NotNull Intent it){
                            Intrinsics.checkNotNullParameter(it,"it");
                            launcher.launch(it);
                        }
                    }));
        });


        return v;
    }


    private void loadProfile(){
        FirebaseDatabase.getInstance(INSTANCE_URL_DATABASE)
                .getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            String name = capsText(user.getName());
                            profileName.setText(name);
                            if(user.getImages()!=null){
                                profileImage.setImageURI(Uri.parse(user.getImages()));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("TAG","FAIL");
                    }
                });
    }



    private void setImageFirebase(Uri uri){
        FirebaseDatabase.getInstance(INSTANCE_URL_DATABASE)
                .getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            User user = snapshot.getValue(User.class);
                            user.setImages(String.valueOf(uri));
                            updateFirebase(user);
                            profileImage.setImageURI(uri);
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

    private String capsText(String s){
        return s.substring(0,1).toUpperCase()+s.substring(1).toLowerCase();
    }

}