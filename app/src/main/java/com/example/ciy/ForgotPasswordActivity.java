package com.example.ciy;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends BaseSignIn {

    private static final String TAG = "Forgot Password";
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setProgressBar(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
    }



    public void sendToMail(View view){
        String mail;
        EditText mailInput;
        mailInput = (EditText) findViewById(R.id.resetPassword);
        mail = mailInput.getText().toString();
        showProgressBar();
        mAuth.sendPasswordResetEmail(mail).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Toast.makeText(ForgotPasswordActivity.this, "Email sent",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Email not registered",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
