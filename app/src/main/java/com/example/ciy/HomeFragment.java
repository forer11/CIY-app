package com.example.ciy;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.FirebaseFirestore;

import com.nex3z.notificationbadge.NotificationBadge;


import java.util.Objects;


/**
 * fragment representing our user home screen where he can see the top viewed recipes
 */
public class HomeFragment extends Fragment implements View.OnDragListener, View.OnLongClickListener {
    private static final int FAST_SCROLL_POSITION = 10;
    /* the firestore database instance */
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    /* reference to the firestore recipes collection */
    private CollectionReference recipesRef = db.collection(SharedData.RECIPES);
    /* reference to the firestore users collection */
    private CollectionReference usersRef = db.collection(SharedData.USERS);
    /* reference to the firestore global ingredients collection */
    private CollectionReference ingredientsRef = db.collection(SharedData.Ingredients);

    /* adapter to the Firestore recipes recyclerView */
    private RecipeAdapter recipeAdapter;
    /* Firestore authentication reference */
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    /* the recycler view object */
    private RecyclerView recyclerView;

    private NotificationBadge badge;


    /* boolean for when we can click on the recyclerView items (when we load the data) */
    // TODO DECIDE IF NEEDED
    private FloatingActionButton addNoteButton;

    private FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        user = firebaseAuth.getCurrentUser();
        badge = view.findViewById(R.id.badge);
        ImageButton fridge = view.findViewById(R.id.fridge_button);
        fridge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFridge(v);
            }
        });
        //Find all views and set Tag to all draggable views
        setViewsTags(view);
        //Set Drag Event Listeners for defined layouts
        LinearLayout home_layout= view.findViewById(R.id.home_layout);
        home_layout.findViewById(R.id.layout1).setOnDragListener(this);
        home_layout.findViewById(R.id.layout2).setOnDragListener(this);
    }

    private void setViewsTags(@NonNull View view) {
        ImageView saltImage = (ImageView) view.findViewById(R.id.salt);
        saltImage.setTag("salt");
        saltImage.setOnLongClickListener(this);
        ImageView pepperImage = (ImageView) view.findViewById(R.id.pepper);
        pepperImage.setTag("pepper");
        pepperImage.setOnLongClickListener(this);
        ImageView milkImage = (ImageView) view.findViewById(R.id.milk);
        milkImage.setTag("milk");
        milkImage.setOnLongClickListener(this);
        ImageView eggsImage = (ImageView) view.findViewById(R.id.eggs);
        eggsImage.setTag("eggs");
        eggsImage.setOnLongClickListener(this);
        ImageView onionImage = (ImageView) view.findViewById(R.id.onion);
        onionImage.setTag("onion");
        onionImage.setOnLongClickListener(this);
        ImageView tomatoImage = (ImageView) view.findViewById(R.id.tomato);
        tomatoImage.setTag("tomato");
        tomatoImage.setOnLongClickListener(this);
        ImageView potatoImage = (ImageView) view.findViewById(R.id.potato);
        potatoImage.setTag("potato");
        potatoImage.setOnLongClickListener(this);
        ImageView carrotImage = (ImageView) view.findViewById(R.id.carrot);
        carrotImage.setTag("carrot");
        carrotImage.setOnLongClickListener(this);
    }

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
        View.DragShadowBuilder dragshadow = new View.DragShadowBuilder(view);
        // Starts the drag
        view.startDrag(data        // data to be dragged
                , dragshadow   // drag shadow builder
                , view           // local data about the drag and drop operation
                , 0          // flags (not currently used, set to 0)
        );
        return true;
    }

    // This is the method that the system calls when it dispatches a drag event to the listener.
    @Override
    public boolean onDrag(View view, DragEvent event) {
        // Defines a variable to store the action type for the incoming event
        int action = event.getAction();
        // Handles each of the expected events
        switch (action) {

            case DragEvent.ACTION_DRAG_STARTED:
                // Determines if this View can accept the dragged data
                if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
//                    // apply color when drag started to view in order to give any color tint to
//                    // the View to indicate that it can accept data.
//                     view.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
//                     //Invalidate the view to force a redraw in the new tint
//                      view.invalidate();
//                    // returns true to indicate that the View can accept the dragged data.
                    return true;
                }
                // Returns false. During the current drag and drop operation, this View will
                // not receive events again until ACTION_DRAG_ENDED is sent.
                return false;

            case DragEvent.ACTION_DRAG_ENTERED:
                // Applies a GRAY or any color tint to the View. Return true; the return value is ignored.
                view.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN); //TODO - we want to change color here?
                // Invalidate the view to force a redraw in the new tint
                view.invalidate();
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                // Ignore the event
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
//                // Re-sets the color tint. Returns true; the return value is ignored.
//                 view.getBackground().setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN);
                //It will clear a color filter .
                view.getBackground().clearColorFilter();
                // Invalidate the view to force a redraw in the new tint
                view.invalidate();
                return true;

            case DragEvent.ACTION_DROP:
                // Gets the item containing the dragged data
                ClipData.Item item = event.getClipData().getItemAt(0);
                // Gets the text data from the item.
                String draggedIngredient = item.getText().toString();
                //here we add the dragged ingredient to the fridge
                SharedData.ingredients.add(draggedIngredient);
                // Turns off any color tints
                view.getBackground().clearColorFilter();
                // Invalidates the view to force a redraw
                view.invalidate();
                View vw = (View) event.getLocalState();
                ViewGroup owner = (ViewGroup) vw.getParent();
                owner.removeView(vw); //remove the dragged view
                //caste the view into LinearLayout as our drag acceptable layout is LinearLayout
                LinearLayout container = (LinearLayout) view;
                container.addView(vw);//Add the dragged view
                vw.setVisibility(View.VISIBLE);//finally set Visibility to VISIBLE
                // Returns true. DragEvent.getResult() will return true.
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                // Turns off any color tinting
                view.getBackground().clearColorFilter();
                // Invalidates the view to force a redraw
                view.invalidate();
                // Does a getResult(), and displays what happened.
                if (event.getResult())
                {

                }
                else
                {

                }
                // returns true; the value is ignored.
                return true;
            // An unknown action type was received.
            default:
                break;
        }
        return false;
    }

    private void openFridge(View view) {
        FragmentManager fragmentManager = Objects.requireNonNull(getActivity())
                .getSupportFragmentManager();
        SearchFragment searchFragment = ((BottomNavigationBar) getActivity()).searchFragment;
        searchFragment.show(fragmentManager, "FridgeFromHome");
    }

    void updateBadge() {
        badge.setNumber(SharedData.ingredients.size());
    }
}
