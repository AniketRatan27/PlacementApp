package com.example.placementapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AdminFormFgm extends Fragment {

    private static final int PICK_RESUME_REQUEST = 1;

    private EditText editfullName, editAddress, editEmail, editDob, eduucms, phone;
    private Spinner editSpinner;
    private CardView cardView , resumeUpload;
    private Button saveProfileButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private Appdatabase appdatabase;
    private Uri resumeuri;
    ImageView resumeShow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_form_fgm, container, false);

        editfullName = view.findViewById(R.id.editfullname);
        editAddress = view.findViewById(R.id.etAddress);
        editEmail = view.findViewById(R.id.etEmail);
        editDob = view.findViewById(R.id.dateOfbirth);
        editSpinner = view.findViewById(R.id.spinnerSemester);
        saveProfileButton = view.findViewById(R.id.btnSave);
        eduucms = view.findViewById(R.id.etuucms);
        phone = view.findViewById(R.id.etphone);
        cardView = view.findViewById(R.id.resumeField);
       // resumeShow = view.findViewById(R.id.resumePic);
       // resumeUpload = view.findViewById(R.id.resumeField);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        appdatabase = Appdatabase.getInstance(getContext());


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.semesters, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editSpinner.setAdapter(adapter);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFilePicker();
            }
        });

        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfile();
            }
        });

        return view;
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Select Resume"), PICK_RESUME_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_RESUME_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            resumeuri = data.getData();
            Toast.makeText(getActivity(), "Resume Selected", Toast.LENGTH_LONG).show();
        }
    }


//private void saveProfile() {
//    String fullName = editfullName.getText().toString().trim();
//    String address = editAddress.getText().toString().trim();
//    String email = editEmail.getText().toString().trim();
//    String dateOfbirth = editDob.getText().toString().trim();
//    String semester = editSpinner.getSelectedItem().toString();
//    String uucms = eduucms.getText().toString();
//    String phonenumber = phone.getText().toString();
//
//    if (fullName.isEmpty() || address.isEmpty() || email.isEmpty() || dateOfbirth.isEmpty() || semester.isEmpty()) {
//        Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_LONG).show();
//        return;
//    }
//
//    if (resumeuri != null) {
//
//        // removed to.String()
//        Log.d("ResumeURI", "URI: " + resumeuri);
//        uploadResumeToFirebaseStorage();
//    }
//
//    saveProfileToFirestore(fullName, address, email, dateOfbirth, semester, uucms, phonenumber, null);
//}
//
//
//
//    private void uploadResumeToFirebaseStorage(){
//        if (resumeuri != null && currentUser != null){
//            StorageReference storageReference = FirebaseStorage.getInstance().getReference("Reseume").child(currentUser.getUid()+".pdf");
//
//            storageReference.putFile(resumeuri)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                @Override
//                                public void onSuccess(Uri uri) {
//                                    String resumeUri = uri.toString();
//                                    // changed userDetails --> userdetail
//                                    firestore.collection("userdetail").document(currentUser.getUid())
//                                            .update("resumeUrl",resumeUri)
//                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    if (task.isSuccessful()){
//                                                        loadProfileImage(resumeUri);
//                                                        Log.d("ResumeURI", "URI: " + resumeUri.toString());
//                                                        Toast.makeText(getContext(), "Profile image updated", Toast.LENGTH_SHORT).show();
//                                                    }
//                                                }
//                                            });
//                                }
//                            });
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(getContext(), "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }
//    }
//
//    private void loadProfileImage(String imageUrl){
//        Glide.with(this).load(imageUrl).into(resumeShow);
//    }
//
//
//    private void updateResumeUrlInFirestore(final String resumeURL) {
//        if (currentUser != null) {
//            Map<String, Object> update = new HashMap<>();
//            update.put("ResumeURL", resumeURL);
//            firestore.collection("userdetail").document(currentUser.getUid()).update(update).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()) {
//                        Toast.makeText(getActivity(), "Resume uploaded successfully", Toast.LENGTH_LONG).show();
//                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                        fragmentManager.popBackStack();
//                    } else {
//                        Toast.makeText(getActivity(), "Failed to update resume URL: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//
//                    }
//                }
//            });
//        }
//
//    }
//
//
//    private void saveProfileToFirestore(final String fullName, final String address, final String email, final String dateOfbirth,
//                                        final String semester, final String uucms, final String phoneNumber, @Nullable String resumeURL) {
//        Map<String, Object> userdetails = new HashMap<>();
//        userdetails.put("fullName", fullName);
//        userdetails.put("address", address);
//        userdetails.put("email", email);
//        userdetails.put("dateOfbirth", dateOfbirth);
//        userdetails.put("semester", semester);
//        userdetails.put("uucms", uucms);
//        userdetails.put("phoneNumber", phoneNumber);
//
//        if (resumeURL != null) {
//            userdetails.put("ResumeURL", resumeURL);
//        }
//
//        if (currentUser != null) {
//            firestore.collection("userdetail").document(currentUser.getUid()).set(userdetails).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if (task.isSuccessful()) {
//                        UserProfile userProfile = new UserProfile();
//                        userProfile.setUid(currentUser.getUid());
//                        userProfile.setFullname(fullName);
//                        userProfile.setAddress(address);
//                        userProfile.setEmail(email);
//                        userProfile.setDob(dateOfbirth);
//                        userProfile.setSemester(semester);
//                        userProfile.setUucmsNo(uucms);
//                        userProfile.setPhoneNumber(phoneNumber);
//                        if (resumeURL != null) {
//                            userProfile.setResumeUrl(resumeURL);
//                        }
//
//                        new Thread(() -> appdatabase.userProfileDao().insert(userProfile)).start();
//                        Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_LONG).show();
//                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                        fragmentManager.popBackStack();
//                    } else {
//                        Toast.makeText(getActivity(), "Failed to update profile: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                }
//            });
//        }
//    }
//
//}

    private void saveProfile() {
        String fullName = editfullName.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String dateOfbirth = editDob.getText().toString().trim();
        String semester = editSpinner.getSelectedItem().toString();
        String uucms = eduucms.getText().toString().trim();
        String phonenumber = phone.getText().toString().trim();

        if (fullName.isEmpty() || address.isEmpty() || email.isEmpty() || dateOfbirth.isEmpty() || semester.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_LONG).show();
            return;
        }

        if (resumeuri != null) {
            uploadResumeToFirebaseStorage(fullName, address, email, dateOfbirth, semester, uucms, phonenumber);
        } else {
            saveProfileToFirestore(fullName, address, email, dateOfbirth, semester, uucms, phonenumber, null);
        }
    }

    private void uploadResumeToFirebaseStorage(final String fullName, final String address, final String email, final String dateOfbirth,
    final String semester, final String uucms, final String phoneNumber) {
        if (resumeuri != null && currentUser != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("resumes/" + currentUser.getUid() + "/resume.pdf");

            storageReference.putFile(resumeuri)
                    .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String resumeUri = uri.toString();
                        uploadResumeUrlToFirestore(resumeUri);
                        saveProfileToFirestore(fullName, address, email, dateOfbirth, semester, uucms, phoneNumber, resumeUri);
                    }).addOnFailureListener(e -> Log.e("Upload", "Failed to get download URL", e)))
                    .addOnFailureListener(e -> Log.e("Upload", "Resume upload failed", e));
        }
    }

    private void uploadResumeUrlToFirestore(String resumeUrl) {
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("userdetail").document(currentUser.getUid());

            userRef.update("resumeUrl", resumeUrl)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Resume URL uploaded successfully"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error uploading resume URL", e));
        }
    }

    private void saveProfileToFirestore(final String fullName, final String address, final String email, final String dateOfbirth,
    final String semester, final String uucms, final String phoneNumber, @Nullable String resumeURL) {
        Map<String, Object> userdetails = new HashMap<>();
        userdetails.put("fullName", fullName);
        userdetails.put("address", address);
        userdetails.put("email", email);
        userdetails.put("dateOfbirth", dateOfbirth);
        userdetails.put("semester", semester);
        userdetails.put("uucms", uucms);
        userdetails.put("phoneNumber", phoneNumber);

        if (resumeURL != null) {
            userdetails.put("resumeUrl", resumeURL);
        }

        if (currentUser != null) {
            firestore.collection("userdetail").document(currentUser.getUid()).set(userdetails)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            UserProfile userProfile = new UserProfile();
                            userProfile.setUid(currentUser.getUid());
                            userProfile.setFullname(fullName);
                            userProfile.setAddress(address);
                            userProfile.setEmail(email);
                            userProfile.setDob(dateOfbirth);
                            userProfile.setSemester(semester);
                            userProfile.setUucmsNo(uucms);
                            userProfile.setPhoneNumber(phoneNumber);
                            if (resumeURL != null) {
                                userProfile.setResumeUrl(resumeURL);
                            }

                            new Thread(() -> appdatabase.userProfileDao().insert(userProfile)).start();
                            Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_LONG).show();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.popBackStack();
                        } else {
                            Toast.makeText(getActivity(), "Failed to update profile: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}





