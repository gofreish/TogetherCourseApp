package com.example.together.service;

import com.example.together.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class StorageService {
    public FirebaseAuth firebaseAuth;
    public FirebaseStorage firebaseStorage;
    public FirebaseDatabase firebaseDatabase;
    private static StorageService storage = null;
    public User currentUser;
    private StorageService(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        currentUser = new User();
    }

    public static StorageService getInstance() {
        if( StorageService.storage == null )
            StorageService.storage = new StorageService();
        return StorageService.storage;
    }
}
