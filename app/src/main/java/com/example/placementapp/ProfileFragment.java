package com.example.placementapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    
    private TextView fullnameText,  addressText ,emailText ,dateofbirthText,semesterText ,uploadImageBtn;
    private Button editButton;
    private ImageView imageView ,imageViewhome;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private Appdatabase appDatabase;
    private Uri imageUri;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        fullnameText = view.findViewById(R.id.disFullnameProfile);
        addressText = view.findViewById(R.id.disAddress);
        emailText = view.findViewById(R.id.disEmail);
        dateofbirthText = view.findViewById(R.id.disDate);
        semesterText = view.findViewById(R.id.disSem);

        uploadImageBtn = view.findViewById(R.id.uploadBtn);
        imageView = view.findViewById(R.id.userImage);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        appDatabase = Appdatabase.getInstance(getContext());


        loadProfile();

        uploadImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

//        editButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.frameLayout ,new AdminFormFgm())
//                        .addToBackStack(null)
//                        .commit();
//            }
//        });

        return view;
    }
// Taking images from resources
    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            uploadImageToFirebaseStorage();
        }
    }

    private void uploadImageToFirebaseStorage(){
        if (imageUri != null && currentUser != null){
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("profileImages").child(currentUser.getUid()+".jpg");

            storageReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    // changed userDetails --> userdetail
                                    firestore.collection("userDetail").document(currentUser.getUid())
                                            .update("profileImageUrl",imageUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        loadProfileImage(imageUrl);
                                                        Toast.makeText(getContext(), "Profile image updated", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void loadProfileImage(String imageUrl){
        Glide.with(this).load(imageUrl).into(imageView);
    }

    private void loadProfile() {
            if (currentUser != null) {
                String uid = currentUser.getUid();

                firestore.collection("users").document(uid).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();

                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String email = documentSnapshot.getString("email");
                            fullnameText.setText(name);
                            emailText.setText(email);
                        } else {
                            fullnameText.setText("User not found");
                        }
                    } else {
                        fullnameText.setText("failed to load");
                    }
                });
            }

//  Seperation


            new Thread(() -> {
                UserProfile userProfile = appDatabase.userProfileDao().getUserProfile(currentUser.getUid());

                if (userProfile != null) {
                    getActivity().runOnUiThread(() -> {
                        addressText.setText(userProfile.getAddress());
                        dateofbirthText.setText(userProfile.getDob());
                        semesterText.setText(userProfile.getSemester());
                        loadProfileImage(userProfile.getProfileImageUrl());
                    });
                } else {
                    // changed userDetails --> userdetail
                    firestore.collection("userDetail").document(currentUser.getUid()).get()
                            .addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful() && task2.getResult() != null) {
                                    DocumentSnapshot documentSnapshot2 = task2.getResult();
                                    String fullName = documentSnapshot2.getString("fullName");
                                    String address = documentSnapshot2.getString("address");
                                    String detailemail = documentSnapshot2.getString("email");
                                    String semester = documentSnapshot2.getString("semester");
                                    String dob = documentSnapshot2.getString("dateOfbirth");
                                    String profileImageUrl = documentSnapshot2.getString("profileImageUrl");

                                    UserProfile userProfile2 = new UserProfile();

                                    userProfile2.setUid(currentUser.getUid());
                                    userProfile2.setFullname(fullName);
                                    userProfile2.setAddress(address);
                                    userProfile2.setEmail(detailemail);
                                    userProfile2.setDob(dob);
                                    userProfile2.setSemester(semester);
                                    userProfile2.setProfileImageUrl(profileImageUrl);

                                    new Thread(() -> appDatabase.userProfileDao().insert(userProfile2)).start();

                                    getActivity().runOnUiThread(() -> {
                                        addressText.setText(address);
                                        dateofbirthText.setText(dob);
                                        semesterText.setText(semester);
                                        loadProfileImage(profileImageUrl);
                                    });
                                }

                            });

                }
            }).start();

    }
    }

//emailText.setText(email);
//fullnameText.setText(name);




// fullnameText.setText(userProfile.getFullname());
//emailText.setText(userProfile.getEmail());














//                   firestore.collection("userDetails").document(currentUser.getUid())
//                           .get()
//                           .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                               @Override
//                               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                   if (task.isSuccessful() && task.getResult() != null){
//                                       DocumentSnapshot documentSnapshot = task.getResult();
//
//                                       String fullName = documentSnapshot.getString("fullName");
//                                       String address = documentSnapshot.getString("address");
//                                       String email = documentSnapshot2.getString("email");
//                                       String dob = documentSnapshot.getString("dateOfbirth");
//                                       String semester = documentSnapshot.getString("semester");
//                                       String profileImageUrl = documentSnapshot.getString("profileImageUrl");
//
//
//                                       UserProfile userProfile1 = new UserProfile();
//                                       userProfile1.setUid(currentUser.getUid());
//                                       userProfile1.setFullname(fullName);
//                                       userProfile1.setAddress(address);
//                                       userProfile1.setEmail(email);
//                                       userProfile1.setDob(dob);
//                                       userProfile1.setSemester(semester);
//
//
//
//                                       getActivity().runOnUiThread(()->{
//                                           fullnameText.setText(fullName);
//                                           addressText.setText(address);
//                                           emailText.setText(email);
//                                           dateofbirthText.setText(dob);
//                                           semesterText.setText(semester);
//                                           loadProfileImage(profileImageUrl);
//                                       });
//                                   }
//
//                               }
//
//                           });

//private void saveUserData() {
//        String fullName1 = fullname.getText().toString().trim();
//        String address1 = address.getText().toString().trim();
//        String email1 = email.getText().toString();
//        String semester1 = spin.getSelectedItem().toString();
//
//        if (TextUtils.isEmpty(fullName1) || TextUtils.isEmpty(address1) || "Select Semester".equals(semester1) || TextUtils.isEmpty(email1)) {
//            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//        if (currentUser != null) {
//            String emailKey = currentUser.getEmail().replace(".", ",")
//                    .replace("#", "%23")
//                    .replace("$", "%24")
//                    .replace("[", "%5B")
//                    .replace("]", "%5D");
//
//            reference = FirebaseDatabase.getInstance().getReference("usersDetail").child(emailKey);
//
//            // hashmaps
//            Map<String, Object> updates = new HashMap<>();
//            updates.put("fullName", fullName1);
//            updates.put("address", address1);
//            updates.put("email",email1);
//            updates.put("semester", semester1);
//
//            reference.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(getActivity(), "User data updated successfully", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getActivity(), "Failed to update user data", Toast.LENGTH_SHORT).show();
//                    }
//
//
//                }
//            });
//
//
//
//
//        }




//ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sem);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spin.setAdapter(adapter);
//
//        firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
//        if (currentUser != null) {
//            String emailKey = currentUser.getEmail().replace(".", ",")
//                    .replace("#", "%23")
//                    .replace("$", "%24")
//                    .replace("[", "%5B")
//                    .replace("]", "%5D");
//            reference = FirebaseDatabase.getInstance().getReference("usersDetail").child(emailKey);
//        }
//
//        // set the button
//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                {
//                    saveUserData();
//                }
//
//            }
//        });
