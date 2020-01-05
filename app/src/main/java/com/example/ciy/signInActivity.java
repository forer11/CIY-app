package com.example.ciy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class signInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
    }
    public void signIn(View view){
        String mail,password;

        EditText mailInput;
        EditText passwordInput;

        mailInput = (EditText) findViewById(R.id.mailSignIn);
        passwordInput = (EditText) findViewById(R.id.passwordSignIn);

        mail = mailInput.getText().toString();
        password = passwordInput.getText().toString();

        Toast.makeText(signInActivity.this,mail,Toast.LENGTH_SHORT).show();
        Toast.makeText(signInActivity.this,password,Toast.LENGTH_SHORT).show();

    }
}
