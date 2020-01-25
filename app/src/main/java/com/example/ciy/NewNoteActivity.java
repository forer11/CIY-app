package com.example.ciy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jackandphantom.blurimage.BlurImage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


import android.os.Environment;

import android.widget.ImageView;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;


public class NewNoteActivity extends AppCompatActivity {
    private EditText editTextTitle;
    private EditText editTextDescription;
    private EditText editTextPrepInstructions;
    private EditText editTextPrepTime;
    private EditText editTextIngredients;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private FirebaseUser user = firebaseAuth.getCurrentUser();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookRef = db.collection(SharedData.RECIPES);
    private CollectionReference usersRef = db.collection(SharedData.USERS);

    public static final String PRESS_TO_START_OVER_MSG = "Press again to return to the Start Page";
    /* Represents the media file type(image/video) that were gonna share using the instegram intent */
    String typeOfMedia = "image/*";
    /* The image view which contains the photo that the user took in this screen */
    private ImageView userPicture;
    private android.net.Uri file;
    /* The file which contains the photo the user took*/
    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), "CameraDemo");
    /* indicates weather the user has pressed 2 times on the default "return" button in his phone*/
    ImageButton cameraButton;
    /* mediaPath is the path of the file which contains the photo the user took, and imName is the
     *  unique name we give to each image the user take */
    String mediaPath, imName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        initiallizeUi();
        //Check if there's a permission to access camera and external storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            cameraButton.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        setCameraButton();
        setBlurredView();
    }

    private void setBlurredView() {
        float radius = 20f;

        View decorView = getWindow().getDecorView();
        //ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        //Set drawable to draw in the beginning of each blurred frame (Optional).
        //Can be used in case your layout has a lot of transparent space and your content
        //gets kinda lost after after blur is applied.
        Drawable windowBackground = decorView.getBackground();
        BlurView blurView = decorView.findViewById(R.id.blurView);
        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(false);

        ImageView background = findViewById(R.id.background);
        BlurImage.with(getApplicationContext()).load(R.id.newNoteLayout).intensity(5).Async(true).into(background);
    }

    private void setCameraButton() {
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //generates a unique name and path for the each file which gonna contain the image
                // the user took
                @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat(
                        "yyyyMMdd_HHmmss").format(new Date());
                imName = "IMG_" + timeStamp + ".jpg";
                String filename = "/" + imName;
                mediaPath = mediaStorageDir.getPath() + File.separator + filename;
                cameraButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        file = Uri.fromFile(getOutputMediaFile());
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
                        startActivityForResult(intent, 100);
                    }
                });
            }
        });
    }

    private void initiallizeUi() {
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add Recipe");
        editTextTitle = findViewById(R.id.Title);
        editTextDescription = findViewById(R.id.Description);
        editTextPrepTime = findViewById(R.id.PreparationTime);
        editTextPrepInstructions = findViewById(R.id.PreparationInstructions);
        editTextIngredients = findViewById(R.id.ingredients);
        //Build upon an existing VmPolicy
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        userPicture = findViewById(R.id.userPicture);
        cameraButton = findViewById(R.id.cameraButton);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
//                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);


        }
    }

//    private void saveNote() {
//        String title = editTextTitle.getText().toString();
//        String description = editTextDescription.getText().toString();
//        String imageUrl = "https://i.ytimg.com/vi/MPV2METPeJU/maxresdefault.jpg";
//
//        if (title.trim().isEmpty() || description.trim().isEmpty()) {
//            Toast.makeText(this, "please enter t and d", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        notebookRef.add(new Recipe(title, description, 0, Arrays.asList("yay", "carrot"), imageUrl));
//        Toast.makeText(this, "Recipe added", Toast.LENGTH_SHORT).show();
//
//        usersRef.document(user.getUid()).collection("Recipes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                    usersRef.document(user.getUid()).collection("Recipes").document(documentSnapshot.getId()).delete();
//                }
//                notebookRef.orderBy("views", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                            Recipe recipe = documentSnapshot.toObject(Recipe.class);
//                            usersRef.document(user.getUid()).collection("Recipes").add(recipe);
//                        }
//                    }
//                });
//            }
//        });
//        finish();
//    }

    /**
     * Callback for the result from requesting permissions.
     *
     * @param requestCode  The request code passed in requestPermissions
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions which is either
     *                     PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
//                cameraButton.setEnabled(true); //tODO
            }
        }
    }


    /**
     * This function is called When the user is done with the subsequent activity and returns. if
     * everything went well we would like to set the taken image on screen and show the publish to
     * instegram button
     *
     * @param requestCode identifies from which Intent we came back.
     * @param resultCode  indicates weather the request was successful.
     * @param data        An Intent that carries the result data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                userPicture.getLayoutParams().height=700;
                userPicture.getLayoutParams().width=700;
                userPicture.requestLayout();
                userPicture.setImageURI(file);
            }
        }
    }

    /**
     * This function creates a new file with given path (if the mediaStorageDir exists)
     *
     * @return the new file which is the photo the user took
     */
    private File getOutputMediaFile() {
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        return new File(mediaStorageDir.getPath() + File.separator +
                imName);
    }
}
