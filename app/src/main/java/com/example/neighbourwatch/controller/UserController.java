package com.example.neighbourwatch.controller;

import android.content.Context;

import com.example.neighbourwatch.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserController {
    private FirebaseFirestore db;
    private Context context;
    private User user;
    public UserController(Context context){
        this.context = context;
    }
}
