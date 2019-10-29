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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FirebaseDataAdd {
    private String userID;
    private FirebaseFirestore db;


    public FirebaseDataAdd(FirebaseFirestore db, String userID){
        this.userID = userID;
        this.db = db;
    }

    public void addUserInFireBase(User user){
        CollectionReference refUser = db.collection("user").document(userID).collection("userDetails");
        refUser.document("RootUser").set(user)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Log.d("addUserDetails", "Successful");
                    }
                    else{
                        Log.d("addUserDetails", "Failed");
                    }
                });
    }
    public void addTodoInFireBase(ArrayList <Task> tasks){

        CollectionReference refTodo = db.collection("user").document(userID).collection("todo");
        Iterator<Task> i = tasks.iterator();
        while(i.hasNext()){

            refTodo.document()
                    .set(i.next()).addOnSuccessListener(aVoid -> Log.d("addTodoFireBase", "Successfull"))
                    .addOnFailureListener(e -> Log.d("addTodoFireBase", "Failed"));
        }
    }

    public void addReminderInFireBase(ArrayList<ReminderDetails> reminderDetails){
        CollectionReference refReminder = db.collection("user").document(userID).collection("reminder");
        Iterator<ReminderDetails> iterator = reminderDetails.iterator();
        while(iterator.hasNext()){
            refReminder.document()
                    .set(iterator.next())
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Log.d("addReminderDetails", "Successful");
                        }
                        else{
                            Log.d("addReminderDetails", "Failed");
                        }
                    });
        }
    }

    public void addWalletInFireBase(ArrayList<Record> wallets){
        CollectionReference  refWallet = db.collection("user").document(userID).collection("wallet");
        Iterator<Record> iterator = wallets.iterator();
        while(iterator.hasNext()){
            refWallet.document()
                    .set(iterator.next())
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Log.d("addWalletDetails", "Success");
                        }
                        else{
                            Log.d("addWalletDetails", "Failed");
                        }
                    });
        }

    }


}
