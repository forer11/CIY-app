package com.example.ciy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jackandphantom.blurimage.BlurImage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import android.os.Environment;

import android.widget.ImageView;
import android.widget.Toast;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;


public class NewRecipeActivity extends AppCompatActivity {
    public static final String INSTRUCTIONS_NEW_LINE = "\uD83D\uDCCC";
    public static final String INGREDIENT_NEW_LINE = "\uD83D\uDCCD";
    public static final String PREPERATION_TIME_NEW_LINE = "⏰";

    private TextInputEditText titleText;
    private TextInputEditText descriptionText;
    private TextInputEditText instructionsText;
    private TextInputEditText prepTimeText;
    private TextInputEditText ingredientsText;

    private TextInputLayout titleLayout;
    private TextInputLayout descriptionLayout;
    private TextInputLayout instructionsLayout;
    private TextInputLayout prepTimeLayout;
    private TextInputLayout ingredientsLayout;

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
    private String finalInstructions;
    private List<String> finalIngredientsList;
    private String finalPrepTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent();
        setResult(2, intent);
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
        titleText = findViewById(R.id.titleText);
        titleLayout = findViewById(R.id.title);
        descriptionText = findViewById(R.id.descriptionText);
        prepTimeLayout = findViewById(R.id.description);
        ingredientsText = findViewById(R.id.ingredientsText);
        ingredientsText.addTextChangedListener(new ValidationTextWatcher(ingredientsText));
        ingredientsLayout = findViewById(R.id.ingredients);
        prepTimeText = findViewById(R.id.preparationTimeText);
        prepTimeText.addTextChangedListener(new ValidationTextWatcher(prepTimeText));
        prepTimeLayout = findViewById(R.id.prepTime);
        instructionsText = findViewById(R.id.preparationInstructionsText);
        instructionsText.addTextChangedListener(new ValidationTextWatcher(instructionsText));
        instructionsLayout = findViewById(R.id.preparationInstructions);
        userPicture = findViewById(R.id.userPicture);
        cameraButton = findViewById(R.id.takePicButton);
        //Build upon an existing VmPolicy
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }


    private boolean validatePreparationTime() {
        String time = prepTimeText.getText().toString();
        if (time.trim().isEmpty()) {
            prepTimeLayout.setErrorEnabled(false);
            return false;
        } else if (Integer.parseInt(Character.toString(time.charAt(0))) <= 0) {
            prepTimeLayout.setError("Invalid Time, please set legal time");
            prepTimeText.requestFocus();
            return false;
        } else {
            finalPrepTime = time;
            prepTimeLayout.setErrorEnabled(false);
            return true;
        }
    }


    private boolean validateIngredientsInput() {
        if (ingredientsText.getText().toString().trim().isEmpty()) {
            ingredientsLayout.setErrorEnabled(false);
            return false;
        } else {
            String[] ingredients = ingredientsText.getText().toString().split("\n");
            finalIngredientsList = new ArrayList<>();
            for (String line : ingredients) {
                if (!TextUtils.isEmpty(line.trim())) {
                    finalIngredientsList.add(line);
                }
                ingredientsLayout.setErrorEnabled(false);
            }
        }
        return true;
    }

    private boolean validateInstructionsInput() {
        if (instructionsText.getText().toString().trim().isEmpty()) {
            instructionsLayout.setErrorEnabled(false);
            return false;
        } else {
            String[] instructions = instructionsText.getText().toString().split("\n");
            finalInstructions = "";
            for (String line : instructions) {
                if (!TextUtils.isEmpty(line.trim())) {
                    finalInstructions += line + "\n";
                }
                instructionsLayout.setErrorEnabled(false);
            }
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class ValidationTextWatcher implements TextWatcher {

        private View view;

        private ValidationTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.preparationTimeText:
                    validatePreparationTime();
                    break;
                case R.id.ingredientsText:
                    validateIngredientsInput();
                    break;
                case R.id.preparationInstructionsText:
                    validateInstructionsInput();
                    break;
            }
        }
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
        String title = titleText.toString();
        String description = descriptionText.getText().toString();
        if (!validateIngredientsInput() || !validatePreparationTime() || !validateInstructionsInput()) {
            Toast.makeText(this, "Please fill legal values in all the fields above", Toast.LENGTH_SHORT).show();
            return;
        } else if (title.trim().isEmpty() || description.trim().isEmpty() || finalPrepTime.trim().isEmpty()
                || finalInstructions.trim().isEmpty() || finalIngredientsList.isEmpty() || file.getPath().equals("")) {
            Toast.makeText(this, "Please fill in all the fields above", Toast.LENGTH_SHORT).show();
            return;
        }
        if (finalPrepTime.contains(":")) {
            String[] temp = finalPrepTime.split(":");
            int timeInMin = ((Integer.parseInt(temp[0]) * 60) + (Integer.parseInt(temp[1])));
            finalPrepTime = Integer.toString(timeInMin);
        }
        Recipe recipe = new Recipe(title, description, finalPrepTime,
                finalInstructions, finalIngredientsList, new URL(file.toString()).toString());
        finish();
    }


}
