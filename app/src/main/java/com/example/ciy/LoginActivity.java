package com.example.ciy;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This activity represents the LoginActivity of the app. It builds 4 sign in options and deals
 * with the result according to the user choice
 */
public class LoginActivity extends AppCompatActivity {

    private static final String LOGIN_ACTIVITY_FLAG_VALUE = "LoginActivity";
    private static final String LOGIN_ACTIVITY_FLAG_KEY = "I_CAME_FROM";
    private static final int MY_REQUEST_CODE = 7117;    // can choose every number
    List<AuthUI.IdpConfig> providers;   // List of all the sign in providers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // init providers
        buildProviders();

        showSignInOptions();
    }

    /**
     * build all the Array of all sign in options
     */
    private void buildProviders() {
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.AnonymousBuilder().build()
        );
    }


    /**
     * starts the intent of the app's home screen, and send a flag to indicate that
     * the user arrived from Login Activity
     */
    private void navToHome() {
        Intent intent = new Intent(getBaseContext(), BottomNavigationBarActivity.class);
        intent.putExtra(LOGIN_ACTIVITY_FLAG_KEY, LOGIN_ACTIVITY_FLAG_VALUE);
        startActivity(intent);
        finish();
    }


    /**
     * creates a custom layout to be used for the Login Activity
     */
    private AuthMethodPickerLayout createCustomLayout() {
        // A custom layout to attach to the LoginActivity screen
        return new AuthMethodPickerLayout
                .Builder(R.layout.activity_signin)
                .setGoogleButtonId(R.id.google_signIn)
                .setEmailButtonId(R.id.email_signIn)
                .setAnonymousButtonId(R.id.anonymous_signIn)
                .setPhoneButtonId(R.id.phone_signIn)
                .build();
    }


    /**
     * This function builds all the sign in options
     */
    private void showSignInOptions() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .setTheme(R.style.BaseAppTheme)
                        .setAuthMethodPickerLayout(createCustomLayout())
                        .build(), MY_REQUEST_CODE
        );
    }

    /**
     * this method update the id of the user which was created
     *
     * @param user the firebase user
     */
    private void updateUI(FirebaseUser user) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection(SharedData.USERS);

        String userId = user.getUid();

        final DocumentReference userRef = usersRef.document(userId);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot == null || !documentSnapshot.exists()) {
                userRef.set(new HashMap<String, Object>(), SetOptions.merge());
            }
        }).addOnFailureListener(e -> {
        });
    }


    /**
     * this function deals with the results of the user's sign in, and calls to the
     * updatesUI function to update app's UI with the new user
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    updateUI(user);
                }
                navToHome();
            } else {
                if (response != null && response.getError() != null) {
                    Toast.makeText(this, "" + response.getError().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
