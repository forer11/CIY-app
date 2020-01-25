package com.example.ciy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jackandphantom.blurimage.BlurImage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


import android.os.Environment;

import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;


public class NewNoteActivity extends AppCompatActivity {
    public static final String INSTRUCTIONS_NEW_LINE = "\uD83D\uDCCC";
    public static final String INGREDIENT_NEW_LINE = "\uD83D\uDCCD";
    public static final String PREPERATION_TIME_NEW_LINE = "⏰";
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
        initializeUi();
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
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);
        Drawable windowBackground = decorView.getBackground();
        BlurView blurView = decorView.findViewById(R.id.blurView);
        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(new RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(false);
        ImageView background = findViewById(R.id.background);
        BlurImage.with(getApplicationContext()).load(R.id.newNoteLayout).intensity(5).Async(true).
                into(background);
        ImageButton button = findViewById(R.id.takePicButton);
        BlurImage.with(getApplicationContext()).load(R.id.imageButton).intensity(25).Async(false).into(button);
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
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                file = Uri.fromFile(getOutputMediaFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
                startActivityForResult(intent, 100);
            }
        });
    }

    private void initializeUi() {
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add Recipe");
        editTextTitle = findViewById(R.id.Title);
        editTextDescription = findViewById(R.id.Description);
        handlePreparationTime();
        handlePreparationInstruction();
        handleIngredientsInput();
        //Build upon an existing VmPolicy
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        userPicture = findViewById(R.id.userPicture);
        cameraButton = findViewById(R.id.takePicButton);
    }

    private void handlePreparationInstruction() {
        editTextPrepInstructions = findViewById(R.id.PreparationInstructions);
        editTextPrepInstructions.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable e) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
                if (lengthAfter > lengthBefore) {
                    if (text.toString().length() == 1) {
                        text = INSTRUCTIONS_NEW_LINE + " " + text;
                        editTextPrepInstructions.setText(text);
                        editTextPrepInstructions.setSelection(editTextPrepInstructions.getText()
                                .length());
                    }
                    if (text.toString().endsWith("\n")) {
                        text = text.toString().replace("\n", "\n" +
                                INSTRUCTIONS_NEW_LINE + " ");
                        text = text.toString().replace(INSTRUCTIONS_NEW_LINE + " " +
                                INSTRUCTIONS_NEW_LINE, INSTRUCTIONS_NEW_LINE);
                        editTextPrepInstructions.setText(text);
                        editTextPrepInstructions.setSelection(editTextPrepInstructions.getText()
                                .length());
                    }
                }
            }
        });
    }

    private void handlePreparationTime() {
        editTextPrepTime = findViewById(R.id.PreparationTime);
        editTextPrepTime.setFocusable(false);
        editTextPrepTime.setClickable(true);
        editTextPrepTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar currentTime = Calendar.getInstance();
                final int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                final int min = currentTime.get(Calendar.MINUTE);
                TimePickerDialog TimePicker;
                TimePicker = new TimePickerDialog(view.getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String time = hourOfDay + ":" + minute + " " + PREPERATION_TIME_NEW_LINE;
                                editTextPrepTime.setText(time);
                            }
                        }, hour, min, true);
                TimePicker.setTitle("Select Time");
                TimePicker.show();
            }
        });
    }

    private void handleIngredientsInput() {
        editTextIngredients = findViewById(R.id.ingredients);
        editTextIngredients.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable e) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
                if (lengthAfter > lengthBefore) {
                    if (text.toString().length() == 1) {
                        text = INGREDIENT_NEW_LINE + " " + text;
                        editTextIngredients.setText(text);
                        editTextIngredients.setSelection(editTextIngredients.getText().length());
                    }
                    if (text.toString().endsWith("\n")) {
                        text = text.toString().replace("\n", "\n" +
                                INGREDIENT_NEW_LINE + " ");
                        text = text.toString().replace(INGREDIENT_NEW_LINE + " " +
                                INGREDIENT_NEW_LINE, INGREDIENT_NEW_LINE);
                        editTextIngredients.setText(text);
                        editTextIngredients.setSelection(editTextIngredients.getText().length());
                    }
                }
            }
        });
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
                try {
                    saveNote();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);


        }
    }

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
                userPicture.getLayoutParams().height = 700;
                userPicture.getLayoutParams().width = 700;
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

    private void saveNote() throws MalformedURLException {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        String preparationTime = editTextPrepTime.getText().toString();
        String preparationInstruction = editTextPrepInstructions.getText().toString();
        String ingredients = editTextIngredients.getText().toString();
        if (title.trim().isEmpty() || description.trim().isEmpty() || preparationTime.trim().isEmpty()
                || preparationInstruction.trim().isEmpty() || ingredients.trim().isEmpty() || file.getPath().equals("")) {
            Toast.makeText(this, "Please fill in all the fields above", Toast.LENGTH_SHORT).show();
            return;
        }
        preparationTime = preparationTime.substring(0, preparationTime.length() - 2);
        List<String> ingredientsList = new LinkedList<>(Arrays.asList(ingredients.split(INGREDIENT_NEW_LINE)));
        ingredientsList.removeAll(Arrays.asList(" ","","\n"));
        List<String> instructionsList = new LinkedList<>(Arrays.asList(ingredients.split(INSTRUCTIONS_NEW_LINE)));
        instructionsList.removeAll(Collections.singleton(","));
        preparationInstruction = instructionsList.toString();
        Recipe recipe = new Recipe(title, description, preparationTime,
                preparationInstruction, ingredientsList, new URL(file.toString()).toString());
        finish();
    }


}
