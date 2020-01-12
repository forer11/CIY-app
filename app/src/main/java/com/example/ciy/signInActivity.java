package com.example.ciy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signInActivity extends BaseSignIn implements View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private EditText mailInput;
    private EditText passwordInput;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.signIn).setOnClickListener(this);
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            Intent intent = new Intent(getBaseContext(), BottomNavigationBar.class);
            startActivity(intent);
        }
    }


    public void signIn(View view) {
        String mail, password;
        mailInput = (EditText) findViewById(R.id.mailSignIn);
        passwordInput = (EditText) findViewById(R.id.passwordSignIn);
        mail = mailInput.getText().toString();
        password = passwordInput.getText().toString();

        if (!isEmailValid(mailInput)) {
            mailInput.setError("Field can't be empty");
//            Toast.makeText(signInActivity.this, "not valid mail format", Toast.LENGTH_SHORT).show();
//            signIn(view);
        }
        createAccount();
//        Toast.makeText(signInActivity.this,mail,Toast.LENGTH_SHORT).show();
//        Toast.makeText(signInActivity.this,password,Toast.LENGTH_SHORT).show();

    }

    private void createAccount() {
        mailInput = findViewById(R.id.mailSignIn);
        passwordInput = findViewById(R.id.passwordSignIn);
        String email = mailInput.getText().toString();
        String password = passwordInput.getText().toString();

        Log.d(TAG, "createAccount:" + email);
        if (!validateForm(mailInput, passwordInput)) {
            return;
        }

        showProgressBar();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            updateUI(user);
                            Intent intent = new Intent(getBaseContext(), BottomNavigationBar.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(signInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signIn) {
            createAccount();
        }
    }
}
