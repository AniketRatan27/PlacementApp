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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

public class SignUp extends AppCompatActivity {

    EditText signupName , signupEmail ,signupUsername,signupPassword ;
    TextView loginRedirectText;
    Button signupBtn;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    AlertDialog loadingDialog ,successDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        signupName = (EditText)findViewById(R.id.signup_name);
        signupBtn=(Button)findViewById(R.id.signbtn);
        signupEmail = (EditText)findViewById(R.id.signup_email);
        signupUsername = (EditText)findViewById(R.id.signup_username);
        signupPassword = (EditText)findViewById(R.id.signup_password);
        loginRedirectText = (TextView) findViewById(R.id.login_redirectText);


        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateName() | !validateEmail() | !validateUserName() | !validatePassword()){

                }else {
                    SignUpUser();
                }
            }
        });


        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUp.this,LogIn.class);
                startActivity(intent);
            }
        });
    }

    public Boolean validateName(){
        String val = signupName.getText().toString().trim();
        if (val.isEmpty()){
            signupName.setError("name field cannot be empty.");
            return false;
        }else{
            signupName.setError(null);
            return true;
        }
    }

    public Boolean validateEmail(){
        String val = signupEmail.getText().toString().trim();
        if (val.isEmpty()){
            signupEmail.setError("email field cannot be empty.");
            return false;
        }else{
            signupEmail.setError(null);
            return true;
        }
    }

    public Boolean validateUserName(){
        String val = signupUsername.getText().toString().trim();
        if (val.isEmpty()){
            signupUsername.setError("Username field cannot be empty.");
            return false;
        }else{
            signupUsername.setError(null);
            return true;
        }
    }

    public Boolean validatePassword(){
        String val = signupPassword.getText().toString().trim();
        if (val.isEmpty()){

            signupPassword.setError("Password field cannot be empty.");
            return false;
        }
        else if (val.length()<6)
        {
            showToast("Password must be 6 character");
            return false;
        } else{
            signupPassword.setError(null);
            return true;
        }
    }


    public void SignUpUser(){

        String name = signupName.getText().toString();
        String email = signupEmail.getText().toString();
        String username = signupUsername.getText().toString();
        String password = signupPassword.getText().toString();




        showLoadingDialog();
        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user!=null){
                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                hideLoadingDialog();
                                                if (task.isSuccessful()){
                                                    HelperClass helperClass = new HelperClass(name,email,username,password);

                                                    firestore.collection("users")
                                                            .document(user.getUid())
                                                            .set(helperClass)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        showSuccessToast("user registered please verify your email");
                                                                        firebaseAuth.signOut();
                                                                        Intent intent = new Intent(SignUp.this,LogIn.class);
                                                                        startActivity(intent);
                                                                        finish();
                                                                    }else {
                                                                        showToast("Error saving user data: ");
                                                                    }
                                                                }
                                                            });
                                                }else {
                                                    showToast("Error sending in emial "+task.getException().getMessage());
                                                }
                                            }
                                        });
                            }
                        }else {
                            // If registration fails
                            showToast("Registration Failed");
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

    private void showSuccessToast(String message){
        Toast toast = new Toast(getApplicationContext());
        View view = getLayoutInflater().inflate(R.layout.custom_success_dialog,(ViewGroup) findViewById(R.id.successView));
        toast.setView(view);

        TextView errorTextToast = view.findViewById(R.id.success_toast);
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

