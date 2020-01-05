package com.example.ciy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class loginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view){
        String mail,password;

        EditText mailInput;
        EditText passwordInput;
        Button submit;

        mailInput = (EditText) findViewById(R.id.mailInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);

        mail = mailInput.getText().toString();
        password = passwordInput.getText().toString();

        Toast.makeText(loginActivity.this,mail,Toast.LENGTH_SHORT).show();
        Toast.makeText(loginActivity.this,password,Toast.LENGTH_SHORT).show();

    }

    public void goToSignIn(View view){
        startActivity(new Intent(this, signInActivity.class));
    }

    public void goToForgetPassword(View view){
        startActivity(new Intent(this, forgotPasswordActivity.class));

    }
}
