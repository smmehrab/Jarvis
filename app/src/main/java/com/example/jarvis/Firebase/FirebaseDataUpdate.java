package com.example.jarvis.Firebase;

import android.util.Log;
import androidx.annotation.NonNull;

import com.example.jarvis.Reminder.ReminderDetails;
import com.example.jarvis.Todo.Task;
import com.example.jarvis.Wallet.Record;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FirebaseDataUpdate {

    private String userID;
    private FirebaseFirestore db;


    public FirebaseDataUpdate(FirebaseFirestore db, String userID){
        this.userID = userID;
        this.db = db;
    }


    public void setUserDeviceIDFromFirebase(String userID){
        db.collection("user").document(userID).
                collection("userDetails")
                .document("RootUser")
                .update(
                        "device",userID
                ).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d("user Device update is", "Successful");
                    }
                    else{
                        Log.d("USERID", "Failed");
                    }
                });
    }


    // If the isIgnored field is one this means that it has been changed;
    // so corresponding document should be deleted; As it is not signle so
    // We use a while loop;
    public void queryOnMultipleTodoInput(ArrayList <Task> dueUpdate){
        ArrayList<Task> newData = new ArrayList<>();
        Iterator<Task> iterator = dueUpdate.iterator();
        //CollectionReference refTode = db.collection("user").document(userID).collection("todo");
            Task h;
            while (iterator.hasNext()) {
                h = iterator.next();
                if (h.getIsIgnored() == 1) {
                    Query query = db.collection("user").document(userID).collection("todo")
                            .whereEqualTo("title", h.getTitle()).whereEqualTo("year", h.getYear()).whereEqualTo(
                            "month", h.getMonth()
                    ).whereEqualTo("day", h.getDay());  // I will need a custom, composite index here;
                    query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            WriteBatch batch = FirebaseFirestore.getInstance().batch();
                            List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot snapshot : snapshotList) {
                                batch.delete(snapshot.getReference());
                            }
                            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("File", "Successfully deleted from firestore");
                                    }
                                }
                            });
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                } else {
                    newData.add(h);
                }
            }
            FirebaseDataAdd firebaseDataAdd = new FirebaseDataAdd(db, userID);
            firebaseDataAdd.addTodoInFireBase(newData);
    }



    public void queryOnMultipleWalletInput(ArrayList<Record> dueWallet){
        ArrayList<Record> newData = new ArrayList<>();
        Iterator<Record> iterator = dueWallet.iterator();
        Record r;
    //    CollectionReference refWallet = db.collection("user").document(userID).collection("wallet");
        //if(refWallet != null) {
            while (iterator.hasNext()) {
                r = iterator.next();
                if (r.getIsIgnored() == 1) {
                    Query query = db.collection("user").document(userID).collection("wallet")
                            .whereEqualTo("title", r.getTitle()).whereEqualTo("year", r.getYear()).whereEqualTo(
                            "month", r.getMonth()
                    ).whereEqualTo("day", r.getDay()).whereEqualTo("type", r.getType());  // I will need a custom, composite index here;
                    query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            WriteBatch batch = FirebaseFirestore.getInstance().batch();
                            List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot snapshot : snapshotList) {
                                batch.delete(snapshot.getReference());
                            }
                            batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("File", "Successfully deleted from firestore");
                                    }
                                }
                            });
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                } else {
                    newData.add(r);
                }
            }
            FirebaseDataAdd firebaseDataAdd = new FirebaseDataAdd(db, userID);
            firebaseDataAdd.addWalletInFireBase(newData);
    }


                            //    private void setup() {
                            //        // [START get_firestore_instance]
                            //        db = FirebaseFirestore.getInstance();
                            //        // [END get_firestore_instance]
                            //
                            //        // [START set_firestore_settings]
                            //        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                            //                .setPersistenceEnabled(true)
                            //                .build();
                            //        db.setFirestoreSettings(settings);
                            //        // [END set_firestore_settings]
                            //    }
                            //
                            //    private void setupCacheSize() {
                            //        // [START fs_setup_cache]
                            //        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                            //                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                            //                .build();
                            //        db.setFirestoreSettings(settings);
                            //        // [END fs_setup_cache]
                            //    }
                            //    private void initializeRef(){
                            //        //refTodo = db.collection("user").document(mAuth.getUid().toString()).collection("todoh");
                            //      //  refReminder = db.collection("user").document(mAuth.getUid().toString()).collection("reminder");
                            //        refJournal = db.collection("user").document(mAuth.getUid().toString()).collection("journal");
                            //        refWallet = db.collection("user").document(mAuth.getUid().toString()).collection("wallet");
                            //        refUser = db.collection("user").document(mAuth.getUid().toString());
                            //    }

}
