package com.example.ciy;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.InputType;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nex3z.notificationbadge.NotificationBadge;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * A fragment representing the home screen, where the user can search for the ingredients he has at
 * home and add them to the fridge
 */
public class HomeFragment extends Fragment implements View.OnDragListener, View.OnLongClickListener {
    /* array of all basic ingredients names*/
    private static final String[] BASIC_INGREDIENTS = {"salt", "pepper", "milk", "eggs", "onions",
            "tomato", "potato", "carrots"};
    /* the firestore database instance */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    /* reference to the firestore global ingredients collection */
    private CollectionReference ingredientsRef = db.collection(SharedData.Ingredients);
    /* the search option adapter */
    private ArrayAdapter<String> searchOptionsAdapter;
    /* the search options ingredients list */
    private List<String> ingredientOptions;
    /* the autoComplete object for the possible ingredients */
    private AutoCompleteTextView userInput;
    /* the fridge notification badge indicating how mush ingredients were entered to the fridge*/
    private NotificationBadge badge;
    /* a hashmap of all basic ingredients we offer the user to choose from, and their appropriate image */
    private HashMap<String, ImageView> basicIngredients;
    /* the layout containing the shelf of basic ingredients we offer the user to choose from */
    private LinearLayout basicIngToChoose;
    /* the layout containing  the shelf were the user needs to drag to (or from) the basic ingredients he has */
    private LinearLayout basicIngShelf;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        badge = view.findViewById(R.id.badge);
        setUpSearchAdapter();
        ImageButton fridge = view.findViewById(R.id.fridge_button);
        fridge.setOnClickListener(v -> openFridge());
        basicIngredients = new HashMap<>();
        //Find all views and set Tag to all draggable views
        setViewsTags(view);
        //Set Drag Event Listeners for defined layouts
        setLayoutListeners();
    }

    /**
     * This method updates event listeners to the shelf were we offer basic ingredients to choose
     * from and the basic ingredients shelf that the user can put his basics in
     */
    private void setLayoutListeners() {
        basicIngToChoose = getView().findViewById(R.id.layout1);
        basicIngShelf = getView().findViewById(R.id.layout2);
        basicIngToChoose.setOnDragListener(this);
        basicIngShelf.setOnDragListener(this);

    }

    /**
     * sets the tag of all image views (of all basic ingredients)
     *
     * @param view the current view
     */
    private void setViewsTags(@NonNull View view) {
        ArrayList<ImageView> ingredientsImages = getImageView(view);
        for (int i = 0; i < BASIC_INGREDIENTS.length; i++) {
            setViewTag(ingredientsImages.get(i), BASIC_INGREDIENTS[i]);
        }
    }

    /**
     * sets the tag of each image view (of each basic ingredient)
     *
     * @param ingredientImage specific basic ingredient image view
     * @param ingredientName  specific basic ingredient name
     */
    private void setViewTag(ImageView ingredientImage, String ingredientName) {
        ingredientImage.setTag(ingredientName);
        ingredientImage.setOnLongClickListener(this);
        basicIngredients.put(ingredientName, ingredientImage);
    }


    /**
     * @param view the current view
     * @return an array list containing the image view of all basic ingredients
     */
    private ArrayList<ImageView> getImageView(@NonNull View view) {
        ArrayList<ImageView> ingredientsImages = new ArrayList<>();
        ingredientsImages.add(view.findViewById(R.id.salt));
        ingredientsImages.add(view.findViewById(R.id.pepper));
        ingredientsImages.add(view.findViewById(R.id.milk));
        ingredientsImages.add(view.findViewById(R.id.eggs));
        ingredientsImages.add(view.findViewById(R.id.onions));
        ingredientsImages.add(view.findViewById(R.id.tomato));
        ingredientsImages.add(view.findViewById(R.id.potato));
        ingredientsImages.add(view.findViewById(R.id.carrots));
        ingredientsImages.add(view.findViewById(R.id.salt));
        return ingredientsImages;
    }


    /**
     * This method controls what happens when user presses long click on one of the basic ingredients
     *
     * @param view the current view
     * @return true
     */
    @Override
    public boolean onLongClick(View view) {
        // Create a new ClipData.Item from the ImageView object's tag
        ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
        // Create a new ClipData using the tag as a label, the plain text MIME type, and
        // the already-created item. This will create a new ClipDescription object within the
        // ClipData, and set its MIME type entry to "text/plain"
        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
        ClipData data = new ClipData(view.getTag().toString(), mimeTypes, item);
        // Instantiates the drag shadow builder.
        View.DragShadowBuilder dragShadow = new View.DragShadowBuilder(view);
        // Starts the drag
        view.startDragAndDrop(data        // data to be dragged
                , dragShadow   // drag shadow builder
                , view           // local data about the drag and drop operation
                , 0          // flags (not currently used, set to 0)
        );
        return true;
    }

    /**
     * This is the method that the system calls when it dispatches a drag event to the listener.
     *
     * @param view  the current view
     * @param event the drag/drop event
     * @return true if the drag event succeeds, false otherwise
     */
    @Override
    public boolean onDrag(View view, DragEvent event) {
        // Defines a variable to store the action type for the incoming event
        int action = event.getAction();
        // Handles each of the expected events
        switch (action) {
            // Determines if this View can accept the dragged data.
            case DragEvent.ACTION_DRAG_STARTED:
                return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
            // Applies a GRAY or any color tint to the View. Return true; the return value is ignored.
            case DragEvent.ACTION_DRAG_ENTERED:
                view.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                view.invalidate();
                return true;
            // Ignore the event
            case DragEvent.ACTION_DRAG_LOCATION:
                return true;
            case DragEvent.ACTION_DRAG_EXITED:
                return invalidateView(view);
            case DragEvent.ACTION_DROP:
                draggedIngHandler(view, event);
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                return invalidateView(view);
            // An unknown action type was received.
            default:
                break;
        }
        return false;
    }

    /**
     * handles the ACTION_DROP event
     *
     * @param view  the current view
     * @param event the drag/drop event
     */
    private void draggedIngHandler(View view, DragEvent event) {
        // Gets the item containing the dragged data
        ClipData.Item item = event.getClipData().getItemAt(0);
        // Gets the text data from the item.
        String draggedIngredient = item.getText().toString();
        addBasicToFridge(view, draggedIngredient);
        invalidateView(view);
        View vw = (View) event.getLocalState();
        ViewGroup owner = (ViewGroup) vw.getParent();
        owner.removeView(vw); //remove the dragged view
        //caste the view into LinearLayout as our drag acceptable layout is LinearLayout
        LinearLayout container = (LinearLayout) view;
        removeBasicFromFridge(draggedIngredient, container);
        container.addView(vw);//Add the dragged view
        vw.setVisibility(View.VISIBLE);//finally set Visibility to VISIBLE
        // Returns true. DragEvent.getResult() will return true.
    }

    /**
     * handles the case were the user drags a basic ingredient from the basic ingredients shelf
     * back to the original shelf
     *
     * @param draggedIngredient the dragged ingredient to be removed from fridge
     * @param container         the layout were the event occurs
     */
    private void removeBasicFromFridge(String draggedIngredient, LinearLayout container) {
        if (container == getView().findViewById(R.id.layout1)) {
            SharedData.ingredients.remove(draggedIngredient);
            updateBadge();
        }
    }

    /**
     * handles the case were the user drags a basic ingredient from the original shelf to the
     * basic ingredients shelf (i.e adds it to the fridge)
     *
     * @param view              the current view
     * @param draggedIngredient the dragged ingredient
     */
    private void addBasicToFridge(View view, String draggedIngredient) {
        if (view == getView().findViewById(R.id.layout2)) {
            //here we add the dragged ingredient to the fridge
            SharedData.ingredients.add(draggedIngredient);
            updateBadge();
        }
    }

    /**
     * Invalidate the view to force a redraw in the new tint
     *
     * @param view the current view
     * @return true
     */
    private boolean invalidateView(View view) {
        //It will clear a color filter .
        view.getBackground().clearColorFilter();
        // Invalidate the view to force a redraw in the new tint
        view.invalidate();
        return true;
    }

    /**
     * opens the fridge fragment
     */
    private void openFridge() {
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity())
                .getSupportFragmentManager();
        FridgeFragment fridgeFragment = ((BottomNavigationBar) getActivity()).fridgeFragment;
        fridgeFragment.show(fragmentManager, "FridgeFromHome");
    }

    //TODO lior
    private void setUpSearchAdapter() {
        final Context context = getActivity();
        final View view = getView();
        ingredientOptions = new ArrayList<>();
        if (SharedData.allIngredients.isEmpty()) {
            ingredientsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
                //we add all ingredients from our data base to 'ingredientOptions' list
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String option = documentSnapshot.get("ingredient").toString();
                    SharedData.allIngredients.add(option);
                }
                ingredientOptions.addAll(SharedData.allIngredients);
                searchOptionsAdapter = new ArrayAdapter<>(Objects.requireNonNull(context),
                        android.R.layout.simple_list_item_1, ingredientOptions);
                getUserInput(view);
            });
        } else {
            if (ingredientOptions.isEmpty()) {
                ingredientOptions.addAll(SharedData.allIngredients);
            }
            searchOptionsAdapter = new ArrayAdapter<>(Objects.requireNonNull(context),
                    android.R.layout.simple_list_item_1, SharedData.allIngredients);
            getUserInput(view);
        }
    }

    /**
     * this method gets and process the user input on the search bar
     *
     * @param view the current view
     */
    private void getUserInput(View view) {
        setsUserInput(view);
        userInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private String input;

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //get the input like for a normal EditText
                input = userInput.getText().toString();
                hideKeyboard(getActivity());
                if (!SharedData.ingredients.contains(input)) {
                    addIngredient();
                } else {
                    Toast.makeText(getContext(), input + " is already in your fridge",
                            Toast.LENGTH_SHORT).show();
                }
                userInput.setText("");
            }

            /**
             * adds the specified ingredient to the user's ingredients list (add it to his fridge)
             */
            private void addIngredient() {
                //update user entered ingredient in data and ingredientName his choice on screen
                SharedData.ingredients.add(input);
                //clears search tab for next search
                updateBadge();
                //if user search one of the basic ingredients we move it to the shelf
                if (basicIngredients.containsKey(input)) {
                    basicIngToChoose.removeView(basicIngredients.get(input));  //remove the deleted view
                    basicIngShelf.addView(basicIngredients.get(input)); //Add the deleted view
                }
            }
        });
    }

    /**
     * sets the search bar adapter and user input type
     *
     * @param view the current view
     */
    private void setsUserInput(View view) {
        userInput = view.findViewById(R.id.enterIngredients);
        userInput.setAdapter(searchOptionsAdapter);
        userInput.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);
        userInput.setTextColor(Color.DKGRAY);
    }

    /**
     * hides the keyboard after each search the user does
     *
     * @param activity the parent activity of the fragment
     */
    private static void hideKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * updates the notification badge that indicates how many ingredients the user put in the fridge
     */
    void updateBadge() {
        badge.setNumber(SharedData.ingredients.size());
    }

    /**
     * This method updates the basic ingredients shelf according to what the user removes from the fridge
     *
     * @param removed an array list containing all the basic ingredient names that were removed from
     *                the fridge
     */
    void updateBasicIngredients(ArrayList<String> removed) {
        for (String item : removed) {
            //then we need to move it back to its original location
            if (basicIngredients.containsKey(item)) {
                basicIngShelf.removeView(basicIngredients.get(item)); //remove the deleted view
                basicIngToChoose.addView(basicIngredients.get(item)); //Add the deleted view
            }
        }
    }
}
