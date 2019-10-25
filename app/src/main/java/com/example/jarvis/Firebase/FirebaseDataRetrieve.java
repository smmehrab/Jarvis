package com.example.jarvis.Firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.jarvis.Reminder.ReminderDetails;
import com.example.jarvis.Todo.Task;
import com.example.jarvis.Wallet.Record;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FirebaseDataRetrieve {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference refTodo, refWallet,  refJournal, refReminder;
    private DocumentReference refUser;

    public FirebaseDataRetrieve(FirebaseAuth mAuth){
        this.mAuth = mAuth;
        setup();
        setupCacheSize();
        initializeRef();
    }


    // Offline cache enable
    private void setup() {
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

    private void setupCacheSize() {
        // [START fs_setup_cache]
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        db.setFirestoreSettings(settings);
        // [END fs_setup_cache]
    }
    private void initializeRef(){
        refTodo = db.collection("user").document(mAuth.getUid().toString()).collection("todo");
        refReminder = db.collection("user").document(mAuth.getUid().toString()).collection("reminder");
        refJournal = db.collection("user").document(mAuth.getUid().toString()).collection("journal");
        refWallet = db.collection("user").document(mAuth.getUid().toString()).collection("wallet");
        refUser = db.collection("user").document(mAuth.getUid().toString());
    }

    public ArrayList<Task> retriveRecentTodo(){
        ArrayList<Task> list = new ArrayList<>();
        refTodo.document("recent").collection("todo").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            Task todo = documentSnapshot.toObject(Task.class);
                            list.add(todo);
                        }
                        Log.d("Orreh dekh dekh", "Success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    Log.d("Exception", e.getMessage());
            }
        });
        return list;



    }

    public ArrayList<Task> retriveOldTodo() {
        ArrayList<Task> list = new ArrayList<>();
        refTodo.document("old").collection("todo").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Task todo = documentSnapshot.toObject(Task.class);
                            list.add(todo);
                        }
                        Log.d("Orreh dekh dekh", "Success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Exception", e.getMessage());
            }
        });
        return list;
    }


    public ArrayList<Record> retriveCurrentWallet() {
        ArrayList<Record> list = new ArrayList<>();
        refTodo.document("current").collection("wallet").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Record wallet = documentSnapshot.toObject(Record.class);
                            list.add(wallet);
                        }
                        Log.d("Orreh dekh dekh", "Success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Exception", e.getMessage());
            }
        });
        return list;
    }


    public ArrayList<Record> retriveOldWallet() {
        ArrayList<Record> list = new ArrayList<>();
        refTodo.document("old").collection("wallet").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Record wallet = documentSnapshot.toObject(Record.class);
                            list.add(wallet);
                        }
                        Log.d("Orreh dekh dekh", "Success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Exception", e.getMessage());
            }
        });
        return list;
    }

    public ArrayList<ReminderDetails> retriveReminder(){
        ArrayList<ReminderDetails> list = new ArrayList<>();
        refReminder.get().addOnSuccessListener((new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    ReminderDetails reminder = documentSnapshot.toObject(ReminderDetails.class);
                    list.add(reminder);
                }
                Log.d("Orreh dekh dekh", "Success");
            }
        })).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Exception", e.getMessage());
            }
        });
        return list;
    }
}
