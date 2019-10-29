package com.example.jarvis.Firebase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.jarvis.SQLite.SQLiteDatabaseHelper;
import com.example.jarvis.Todo.Task;
import com.example.jarvis.UserHandling.User;
import com.example.jarvis.Wallet.Record;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
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

    public void retriveTodoFromFirebase(Context context){
        ArrayList<Task> tasks = new ArrayList<>();

        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();

         db.collection("user").document(userID).collection("todo").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            Task todo = documentSnapshot.toObject(Task.class);
                            tasks.add(todo);

                        }
                        sqLiteDatabaseHelper.insertAllTodos(tasks);
                        Log.d("Todo Data retrieve", "Success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                    Log.d("Exception", e.getMessage());
            }
        });
    }


    public void retriveWalletFromFirebase(Context context) {
        ArrayList<Record> records = new ArrayList<>();

        SQLiteDatabaseHelper sqLiteDatabaseHelper = new SQLiteDatabaseHelper(context);
        SQLiteDatabase sqLiteDatabase = sqLiteDatabaseHelper.getWritableDatabase();

        db.collection("user")
                .document(userID).collection("wallet").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Record wallet = documentSnapshot.toObject(Record.class);
                            records.add(wallet);

                        }

                        sqLiteDatabaseHelper.insertAllRecords(records);
                        Log.d("Wallet Data retrieve", "Success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Exception", e.getMessage());
            }
        });
    }
}
