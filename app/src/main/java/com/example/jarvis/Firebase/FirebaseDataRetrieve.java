package com.example.jarvis.Firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.jarvis.Reminder.ReminderDetails;
import com.example.jarvis.Todo.Task;
import com.example.jarvis.UserHandling.User;
import com.example.jarvis.Wallet.Record;
import com.google.android.gms.tasks.OnCompleteListener;
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
    private String userID;
    private FirebaseFirestore db;


    public FirebaseDataRetrieve(FirebaseFirestore db, String userID){
        this.userID = userID;
        this.db = db;
    }

    public String retrieveDeviceID(){
        String[] x = {""};
        db.collection("user").document(userID).collection("RootUser").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            User u = (User) task.getResult().toObjects(User.class);
                            x[0] = u.getDevice();
                        }
                    }
                });
        return x[0];

    }

    public ArrayList<Task> retriveTodoFromFirebase(){
        ArrayList<Task> list = new ArrayList<>();
         db.collection("user").document(userID).collection("todo").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            Task todo = documentSnapshot.toObject(Task.class);
                            list.add(todo);

                        }
                        Log.d("Todo Data retrieve", "Success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    Log.d("Exception", e.getMessage());
            }
        });

        return list;



    }


    public ArrayList<Record> retriveWalletFromFirebase() {
        ArrayList<Record> list = new ArrayList<>();
        db.collection("user")
                .document(userID).collection("wallet").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Record wallet = documentSnapshot.toObject(Record.class);
                            list.add(wallet);

                        }
                        Log.d("Wallet Data retrieve", "Success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Exception", e.getMessage());
            }
        });
        return list;
    }


}
