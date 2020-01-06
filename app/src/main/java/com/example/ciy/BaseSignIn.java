package com.example.ciy;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import androidx.annotation.VisibleForTesting;

import java.util.regex.Pattern;

public class BaseSignIn extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("");

    @VisibleForTesting
    public ProgressBar mProgressBar;

    public void setProgressBar(int resId) {
        mProgressBar = findViewById(resId);
    }

    public void showProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void hideKeyboard(View view) {
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressBar();
    }

    public boolean validateForm(EditText mailInput, EditText passwordInput) {
        boolean valid = true;

        String email = mailInput.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mailInput.setError("Required.");
            valid = false;
        } else {
            mailInput.setError(null);
        }

        String password = passwordInput.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Required.");
            valid = false;
        } else {
            passwordInput.setError(null);
        }

        return valid;
    }

    public boolean isEmailValid(EditText textInputMail) {
        String emailAdd = textInputMail.getText().toString().trim();
        if (emailAdd.isEmpty()) {
            textInputMail.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailAdd).matches()) {
            textInputMail.setError("Please enter a valid email address");
            return false;
        } else {
            textInputMail.setError(null);
            return true;
        }
    }

    public boolean isPasswordValid(EditText textInputPassword) {
        String password = textInputPassword.getText().toString().trim();
        if (password.isEmpty()) {
            textInputPassword.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) { // TODO edit the password regex
            textInputPassword.setError("Password too weak");    // TODO tell the user which inputs
            return false;
        } else {
            textInputPassword.setError(null);
            return true;
        }

    }

}
