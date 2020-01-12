package com.example.ciy;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;


public class HomeFragment extends Fragment {
    private static final String NOTEBOOK_COLLECTION = "Notebook";
    private static final String USERS = "Users";


    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference notebookRef = db.collection(NOTEBOOK_COLLECTION);

    private CollectionReference usersRef = db.collection(USERS);

    private NoteAdapter adapter;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private FirebaseUser user = firebaseAuth.getCurrentUser();

    private boolean canIclick = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        canIclick = false;
        //TODO fo this in login/signin
        final DocumentReference userRef = usersRef.document(user.getUid());
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot == null || !documentSnapshot.exists()) {
                    userRef.set(new HashMap<String, Object>(), SetOptions.merge());
                }
                setUpData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                setUpData();
            }
        });

        setUpRecyclerView();
        adapter.startListening();
    }

    private void setUpData() {
        usersRef.document(user.getUid()).collection("Recipes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    usersRef.document(user.getUid()).collection("Recipes").document(documentSnapshot.getId()).delete();
                }
                final ArrayList<Recipe> recipes = new ArrayList<>();
                notebookRef.orderBy("views", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Recipe recipe = documentSnapshot.toObject(Recipe.class);
                            recipe.setId(documentSnapshot.getId());
                            recipes.add(recipe);
                            usersRef.document(user.getUid()).collection("Recipes").add(recipe);
                        }
                        View b = getView().findViewById(R.id.test);
                        b.setVisibility(View.GONE);
                        canIclick = true;
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });

        FloatingActionButton addNoteButton = Objects.requireNonNull(getView()).findViewById(R.id.addButton);
        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NewNoteActivity.class));
            }
        });
    }


    private void setUpRecyclerView() {

        Query query = usersRef.document(user.getUid()).collection("Recipes").orderBy("views", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Recipe> options = new FirestoreRecyclerOptions.Builder<Recipe>()
                .setQuery(query, Recipe.class)
                .build();

        adapter = new NoteAdapter(options);
        RecyclerView recyclerView = Objects.requireNonNull(getView()).findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);

        final String[] urls = new String[]{"https://boygeniusreport.files.wordpress.com/2016/11/puppy-dog.jpg?quality=98&strip=all&w=782",
                "https://images2.minutemediacdn.com/image/upload/c_crop,h_1350,w_2400,x_0,y_136/f_auto,q_auto,w_1100/v1576859350/shape/mentalfloss/610651-gettyimages-901452436.jpg",
                "https://cdn.psychologytoday.com/sites/default/files/styles/article-inline-half/public/field_blog_entry_images/2018-02/vicious_dog_0.png?itok=nsghKOHs",
                "https://scx2.b-cdn.net/gfx/news/hires/2019/wolfdog.jpg",
                "https://img.thedailybeast.com/image/upload/c_crop,d_placeholder_euli9k,h_1687,w_3000,x_0,y_0/dpr_1.5/c_limit,w_1044/fl_lossy,q_auto/v1575669519/191206-weill-dogs-in-politics-tease_ko5qke",
                "https://d.newsweek.com/en/full/1517827/coconut-rice-bear.jpg?w=1600&h=1600&q=88&f=8b37e38c82ec050dda787e009f0ef2ef",
                "https://compote.slate.com/images/8aedcaf8-0474-4644-b1b9-6a00220dc2dd.jpeg?width=780&height=520&rect=1560x1040&offset=0x0"};


        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(DocumentSnapshot documentSnapshot, int position) {
                if (canIclick) {
                    Recipe recipe = documentSnapshot.toObject(Recipe.class);
                    Random random = new Random();
                    final int index = random.nextInt(urls.length);
                    usersRef.document(user.getUid()).collection("Recipes")
                            .document(documentSnapshot.getId()).update("imageUrl", urls[index]);
                    executeTransaction(Objects.requireNonNull(recipe).getId(), notebookRef);
                    executeTransaction(documentSnapshot.getId(), usersRef.document(user.getUid()).collection("Recipes"));
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.stopListening();
    }

    /**
     * incrementing a parameter in fireStore with synchronization
     *
     * @param id
     */
    private void executeTransaction(final String id, final CollectionReference dataCollection) {
        db.runTransaction(new Transaction.Function<Long>() {
            @Override
            public Long apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentReference noteRef = dataCollection.document(id);
                DocumentSnapshot noteSnapShot = transaction.get(noteRef);
                long newViews = noteSnapShot.getLong("views") + 1;
                transaction.update(noteRef, "views", newViews);
                return newViews;

            }
        }).addOnSuccessListener(new OnSuccessListener<Long>() {
            @Override
            public void onSuccess(Long result) {
                Toast.makeText(getActivity(), "Views updated to: " + result, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
