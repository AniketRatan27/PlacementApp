package com.example.placementapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText forgotEmail ,forgotPassword;
    Button resetPassd;
    TextView errortext;
    FirebaseAuth firebaseAuth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        forgotEmail = (EditText) findViewById(R.id.forgot_email);
        forgotPassword = (EditText) findViewById(R.id.forgot_password);
        resetPassd = (Button) findViewById(R.id.resetbtn);
        errortext = (TextView) findViewById(R.id.errorText);

        firebaseAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("users");

        resetPassd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = forgotEmail.getText().toString().trim();
                String newPassword = forgotPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(newPassword)){
                    errortext.setText("email and new Password are required.");
                    return;
                }

                resetPassword(email,newPassword);
            }
        });
    }
    private void resetPassword(String email ,String newpassword){
        String emailKey = email.replace(".", ",")
                .replace("#", "%23")
                .replace("$", "%24")
                .replace("[", "%5B")
                .replace("]", "%5D");
        reference.child(emailKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    firebaseAuth.signInWithEmailAndPassword(email,snapshot.child("password").getValue(String.class))
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUser user = firebaseAuth.getCurrentUser();

                                        if (user != null){
                                            user.updatePassword(newpassword)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                reference.child(emailKey).child("password").setValue(newpassword);

                                                                Toast.makeText(ForgotPasswordActivity.this, "Password reset successfully", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(ForgotPasswordActivity.this, LogIn.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }else {
                                                                showError("Failed to reset Password");
                                                            }
                                                        }
                                                    });
                                        }else {
                                            showError("Failed to reset password user not logged in.");
                                        }
                                    }else {
                                        Toast.makeText(ForgotPasswordActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    showError("Email not registered");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showError("Database error: " + error.getMessage());
            }
        });
   }
    private void showError(String error) {
        errortext.setText(error);
    }
}