package com.example.ciy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class forgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
    }

    public void sendToMail(View view){
        String mail;
        EditText mailInput;
        mailInput = (EditText) findViewById(R.id.resetPassword);
        mail = mailInput.getText().toString();
        Toast.makeText(forgotPasswordActivity.this,mail,Toast.LENGTH_SHORT).show();

    }
}
