package com.example.ciy;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

public class SignInActivity extends BaseSignIn implements View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private EditText mailInput;
    private EditText passwordInput;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        setProgressBar(R.id.progressBar);
        findViewById(R.id.signIn).setOnClickListener(this);
        showProgressBar();
    }

    public void signIn(View view) {
        String mail, password;
        mailInput = (EditText) findViewById(R.id.mailSignIn);
        passwordInput = (EditText) findViewById(R.id.passwordSignIn);
        mail = mailInput.getText().toString();
        password = passwordInput.getText().toString();

        if (!isEmailValid(mailInput)) {
            mailInput.setError("Field can't be empty");
//            Toast.makeText(SignInActivity.this, "not valid mail format", Toast.LENGTH_SHORT).show();
//            signIn(view);
        }
        createAccount();
//        Toast.makeText(SignInActivity.this,mail,Toast.LENGTH_SHORT).show();
//        Toast.makeText(SignInActivity.this,password,Toast.LENGTH_SHORT).show();

    }

    private void createAccount() {
        mailInput = findViewById(R.id.mailSignIn);
        passwordInput = findViewById(R.id.passwordSignIn);
        String email = mailInput.getText().toString();
        String password = passwordInput.getText().toString();

        showProgressBar();

        Log.d(TAG, "createAccount:" + email);
        if (!validateForm(mailInput, passwordInput)) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            Intent intent = new Intent(getBaseContext(), BottomNavigationBar.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
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

    private void updateUI(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection(SharedData.USERS);

        String userId = user.getUid();

        final DocumentReference userRef = usersRef.document(userId);
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot == null || !documentSnapshot.exists()) {
                    userRef.set(new HashMap<String, Object>(), SetOptions.merge());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }
}
