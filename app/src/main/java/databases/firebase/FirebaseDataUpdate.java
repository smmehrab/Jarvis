package databases.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class FirebaseDataUpdate {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    CollectionReference refTodo, refWallet,  refJournal, refReminder;
    DocumentReference refUser;

    public FirebaseDataUpdate(FirebaseAuth mAuth){
        this.mAuth = mAuth.getInstance();
        setup();
        setupCacheSize();
        initializeRef();
    }

    public void setup() {
        // [START get_firestore_instance]
        db = FirebaseFirestore.getInstance();
        // [END get_firestore_instance]

        // [START set_firestore_settings]
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        // [END set_firestore_settings]
    }

    public void setupCacheSize() {
        // [START fs_setup_cache]
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        db.setFirestoreSettings(settings);
        // [END fs_setup_cache]
    }
    private void initializeRef(){
        //refTodo = db.collection("user").document(mAuth.getUid().toString()).collection("todo");
      //  refReminder = db.collection("user").document(mAuth.getUid().toString()).collection("reminder");
        refJournal = db.collection("user").document(mAuth.getUid().toString()).collection("journal");
        refWallet = db.collection("user").document(mAuth.getUid().toString()).collection("wallet");
        refUser = db.collection("user").document(mAuth.getUid().toString());
    }

}
