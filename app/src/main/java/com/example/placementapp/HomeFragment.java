package com.example.placementapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

    private CardView profileCard, studentDetailsCard, notificationCard;
    private CardView topinterview, javascript, leetcode, mysql;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            ((MainActivity) context).setToolbarVisibility(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setToolbarVisibility(false);
        }
    }

    private TextView displayname;
    private Button btn;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        displayname = view.findViewById(R.id.displayName);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        profileCard = view.findViewById(R.id.profilecard);
        studentDetailsCard = view.findViewById(R.id.studentdetails);
        notificationCard = view.findViewById(R.id.notification);

        topinterview=view.findViewById(R.id.topinterview);
        javascript=view.findViewById(R.id.javascript);
        leetcode=view.findViewById(R.id.leetcode);
        mysql=view.findViewById(R.id.mysql);

        if (currentUser != null){
            String uid = currentUser.getUid();

            firestore.collection("users").document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if (documentSnapshot.exists()){
                        String name = documentSnapshot.getString("name");
                        displayname.setText(name);
                    }else {
                        displayname.setText("User not found");
                    }
                }else {
                    displayname.setText("failed to load");
                }
            });
        }else {
            displayname.setText("User not logged in");
        }

//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                firebaseAuth.signOut();
//                Intent intent = new Intent(getActivity(),LogIn.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(intent);
//            }
//        });


        // external links

        topinterview.setOnClickListener(view1 -> openURL("https://prepinsta.com/interview-preparation/"));
        javascript.setOnClickListener(view1 -> openURL("https://developer.mozilla.org/en-US/docs/Web/JavaScript"));
        leetcode.setOnClickListener(view1 -> openURL("https://leetcode.com/"));
        mysql.setOnClickListener(view1 -> openURL("https://dev.mysql.com/doc/"));
        // fragment transfer
        profileCard.setOnClickListener(view1 -> loadfragment(new ProfileFragment()));
        studentDetailsCard.setOnClickListener(view1 -> loadfragment(new AdminFormFgm()));
        notificationCard.setOnClickListener(view1 -> loadfragment(new ChatFragment()));

            return view;
    }

    private void openURL(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void loadfragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}



















//
//if(currentUser != null) {
//String emailKey = currentUser.getEmail().replace(".", ",")
//        .replace("#", "%23")
//        .replace("$", "%24")
//        .replace("[", "%5B")
//        .replace("]", "%5D");
//
//reference = FirebaseDatabase.getInstance().getReference("users").child(emailKey);
//
//            reference.addListenerForSingleValueEvent(new ValueEventListener() {
//    @Override
//    public void onDataChange(@NonNull DataSnapshot snapshot) {
//        if (snapshot.exists()) {
//            String name = snapshot.child("name").getValue(String.class);
//
//            displayname.setText(name);
//        }else {
//            displayname.setText("user not found");
//        }
//
//    }
//
//    @Override
//    public void onCancelled(@NonNull DatabaseError error) {
//        displayname.setText("failed to load");
//    }
//});
//
//        }else{
//        displayname.setText("user not logged in");
//        }
//                btn.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View view) {
//        firebaseAuth.signOut();
//        Intent intent = new Intent(getActivity(),LogIn.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//
//    }
//});
