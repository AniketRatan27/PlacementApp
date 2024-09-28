package com.example.placementapp;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<HelperClass> userLiveData = new MutableLiveData<>();

    public LiveData<HelperClass> getUserLiveData(){
        return userLiveData;
    }

    public void fetchUserData(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String emailKey = currentUser.getEmail().replace(".", ",")
                    .replace("#", "%23")
                    .replace("$", "%24")
                    .replace("[", "%5B")
                    .replace("]", "%5D");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("").child(emailKey);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String name = snapshot.child("name").getValue(String.class);
                        String username = snapshot.child("username").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);
                        String password = snapshot.child("password").getValue(String.class);

                        HelperClass helperClass = new HelperClass(name,username,email,password);
                        userLiveData.setValue(helperClass);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

    }
}
