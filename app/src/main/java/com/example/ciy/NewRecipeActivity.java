package com.example.ciy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.jackandphantom.blurimage.BlurImage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import android.widget.ImageView;
import android.widget.Toast;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;


public class NewRecipeActivity extends AppCompatActivity {

    private TextInputEditText titleText;
    private TextInputEditText descriptionText;
    private TextInputEditText instructionsText;
    private TextInputEditText prepTimeText;
    private TextInputEditText ingredientsText;

    private TextInputLayout instructionsLayout;
    private TextInputLayout prepTimeLayout;
    private TextInputLayout ingredientsLayout;

    /* the firestore database instance */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    /* reference to the firestore recipes collection that waits for approval */
    private CollectionReference tempDb = db.collection(SharedData.TEMP_RECIPE_DB);


    /* The image view which contains the photo that the user took in this screen */
    private ImageView userPicture;
    /* the instruction the user has entered after edit*/
    private String finalInstructions;
    /* the ingredients list the user has entered after edit*/
    private List<String> finalIngredientsList;
    /* the preparation time the user has entered*/
    private String prepTimeHours;
    /* the upload an image from your gallery button */
    private ImageButton uploadImageButton;
    private Uri selectedImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent();
        setResult(2, intent);
        setContentView(R.layout.activity_new_recipe);
        initializeUi();
        uploadImageButton.setOnClickListener(view -> {
            // upload a picture from gallery intent
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, 1);
        });
        setBlurredView();
    }

    /**
     * sets the blurred background of the activity, and the blurred effect on the -upload a picture
     * from gallery button
     */
    private void setBlurredView() {
        float radius = 20f;
        View decorView = getWindow().getDecorView();
        ViewGroup rootView = decorView.findViewById(android.R.id.content);
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
        ImageButton uploadPicButton = findViewById(R.id.uploadPicButton);
        BlurImage.with(getApplicationContext()).load(R.id.imageButton).intensity(25).
                Async(false).into(uploadPicButton);
    }

    /**
     * This method initializes all UI elements of the activity
     */
    private void initializeUi() {
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add Recipe");
        titleText = findViewById(R.id.titleText);
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
        uploadImageButton = findViewById(R.id.uploadPicButton);
        //Build upon an existing VmPolicy
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    /**
     * This method converts the given preparation time from hours to minutes in case the user has
     * chosen to fill it by hours
     *
     * @return false if the user haven't filled the preparation time field, and true otherwise
     */
    private boolean convertsPrepTimeToMin() {
        String time = prepTimeText.getText().toString();
        if (time.trim().isEmpty()) {
            prepTimeLayout.setErrorEnabled(false);
            return false;
        }
        //if the user entered ":" it means he chose to enter the preparation time in hours
        int hours = time.contains(":") ? Integer.parseInt(time.substring(0, time.indexOf(":"))) * 60 : 0;
        prepTimeHours = Integer.toString(hours);
        prepTimeLayout.setErrorEnabled(false);
        return true;
    }

    /**
     * This method validates the input ingredients that the user has filled
     *
     * @return true if the ingredients input is'nt empty, false otherwise
     */
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

    /**
     * This method validates the input instructions the user has filled
     *
     * @return true if the instructions input is'nt empty, false otherwise
     */
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

    /***
     * A validation text watcher class that we use to validate each input
     * (preparation time, ingredients and recipe instructions) we get from the user.
     */
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
                    convertsPrepTimeToMin();
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
        if (item.getItemId() == R.id.save_note) {
            try {
                saveNote();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * This function is called When the user is done with the subsequent activity and returns. if
     * everything went well we would like to set the chosen image from gallery on screen and show
     *
     * @param requestCode identifies from which Intent we came back.
     * @param resultCode  indicates weather the request was successful.
     * @param data        An Intent that carries the result data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                selectedImageUri = data.getData();
                userPicture.getLayoutParams().height = 700;
                userPicture.getLayoutParams().width = 700;
                userPicture.requestLayout();
                userPicture.setImageURI(selectedImageUri);
            }
        }
    }


    /**
     * upload the new recipe to a temporary database, for now it will not upload it to the main
     * recipe database as it needs approval.
     *
     * @throws MalformedURLException thrown if the photo url is invalid.
     */
    private void saveNote() throws MalformedURLException {
        String title = titleText.toString();
        String description = descriptionText.getText().toString();
        String prepTime = prepTimeText.getText().toString();
        if (validatesUserInput(title, description, prepTime)) {
            return;
        }
        //if the user has entered preparation time in hours we take the hours converted to minutes
        //(which we converted before in convertsPrepTimeToMin method) and add the minutes part
        //(after the ":" in the input)
        int prepTimeMinutes = prepTime.contains(":") ? Integer.parseInt(prepTimeHours) +
                Integer.parseInt(prepTime.substring(prepTime.indexOf(":") + 1)) :
                Integer.parseInt(prepTime);
        //we create the final recipe object from all the given input
        Recipe recipe = new Recipe(title, description, Integer.toString(prepTimeMinutes),
                finalInstructions, finalIngredientsList,
                new URL(selectedImageUri.toString()).toString());
        recipe.setId(recipe.getTitle());
        //we save it to a temporary database for approval
        tempDb.document(recipe.getId()).set(recipe, SetOptions.merge())
                .addOnSuccessListener(aVoid -> Toast
                        .makeText(NewRecipeActivity.this, "recipe.getTitle()" +
                                " Submission is up for approval", Toast.LENGTH_SHORT).show());
        finish();
    }

    /**
     * This method validates the user input
     *
     * @param title       the input title the user has entered
     * @param description the input description the user has entered
     * @param prepTime    the input preparation time the user has entered
     * @return true
     */
    private boolean validatesUserInput(String title, String description, String prepTime) {
        if (!validateIngredientsInput() || !convertsPrepTimeToMin() || !validateInstructionsInput()) {
            Toast.makeText(this, "Please fill legal values in all the fields above",
                    Toast.LENGTH_SHORT).show();
        } else if (title.trim().isEmpty() || description.trim().isEmpty() || prepTime.trim().isEmpty()
                || finalInstructions.trim().isEmpty() || finalIngredientsList.isEmpty() ||
                selectedImageUri.getPath().equals("")) {
            Toast.makeText(this, "Please fill in all the fields above",
                    Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
