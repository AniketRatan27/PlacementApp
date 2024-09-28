package com.example.placementapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LogIn extends AppCompatActivity {
    EditText loginEmail, loginPassword;
    Button loginBtn;
    TextView errortext;
    TextView signupRedirectText ,forgotLink;
    FirebaseAuth firebaseAuth;
    AlertDialog loadingDialog ,successDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            Intent intent = new Intent(LogIn.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }


        loginEmail = (EditText) findViewById(R.id.login_email);
        loginPassword = (EditText) findViewById(R.id.login_password);
        signupRedirectText = (TextView) findViewById(R.id.signup_redirectText);
        loginBtn = (Button) findViewById(R.id.logbtn);
        errortext = (TextView)findViewById(R.id.errorText);
        forgotLink = (TextView) findViewById(R.id.forgot_passwordLink);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!validateEmail() | !validatePassword()){

                } else {
                    checkUser();
                }


            }
        });
        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogIn.this,SignUp.class);
                startActivity(intent);
            }
        });

        forgotLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogIn.this,ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

    }

    public Boolean validateEmail() {
        String val = loginEmail.getText().toString();
        if (val.isEmpty()) {
            loginEmail.setError("Email cannot be empty");
            return false;
        } else {
            loginEmail.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            loginPassword.setError("Password cannot be empty");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    public void checkUser() {

        String userEmail = loginEmail.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        showLoadingDialog();
        firebaseAuth.signInWithEmailAndPassword(userEmail,userPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            if (user != null && user.isEmailVerified()){
                                DocumentReference  reference = FirebaseFirestore.getInstance().collection("users").document(user.getUid());

                                reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()){
                                            DocumentSnapshot documentSnapshot = task.getResult();

                                            if (documentSnapshot.exists()){
                                                String nameFromDb = documentSnapshot.getString("name");
                                                String usernameFromDb = documentSnapshot.getString("username");
                                                String emailFromDb = documentSnapshot.getString("email");

                                                hideLoadingDialog();
                                                Intent intent = new Intent(LogIn.this ,MainActivity.class);
                                                intent.putExtra("name", nameFromDb);
                                                intent.putExtra("email", emailFromDb);
                                                intent.putExtra("username", usernameFromDb);
                                                startActivity(intent);
                                                finish();
                                            }else {
                                                loginEmail.setError("Email is not registered");
                                                loginEmail.requestFocus();
                                            }
                                        }else {
                                            showToast("Database error "+task.getException().getMessage());
                                        }
                                    }
                                });
                            }else if (user != null && !user.isEmailVerified()) {
                                // User's email is not verified
                                firebaseAuth.signOut();
//                                errortext.setText("");
                                showToast("Email not verified. Please check your email for verification.");
                            }
                        }else {
//                            errortext.setText("");
                            showToast("Invalid Credential. Please Sign up to login.");
                        }
                    }
                });

    }
    private void showLoadingDialog(){
        if (loadingDialog != null && loadingDialog.isShowing()) {
            return; // Prevent multiple dialogs from being shown
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog_loading,null);//if any error occurs change remove "findViewById(R.id.loadingDialog)" and put null
        builder.setView(dialogView);
        builder.setCancelable(true);
        loadingDialog = builder.create();
        loadingDialog.show();

        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setLayout(960, 920); // Width and height in pixels
        }
    }
    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
    }

    private void showToast(String message){
        Toast toast = new Toast(getApplicationContext());
        View view = getLayoutInflater().inflate(R.layout.custom_toast,(ViewGroup) findViewById(R.id.viewContainer));
        toast.setView(view);

        TextView errorTextToast = view.findViewById(R.id.errorTextToast);
        errorTextToast.setText(message);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER ,0,0);
        toast.show();
    }


    @Override
    protected void onDestroy() {
        hideLoadingDialog();
        if (successDialog != null && successDialog.isShowing()) {
            successDialog.dismiss();
            successDialog = null; // Ensure dialog is reset
        }
        super.onDestroy();
    }
}

