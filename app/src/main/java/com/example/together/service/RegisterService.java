package com.example.together.service;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.together.shared.Constantes;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class RegisterService {

    private static RegisterService registerService;
    private StorageService storeageService;

    private RegisterService(){
        storeageService = StorageService.getInstance();
    }

    public static RegisterService getInstance() {
        if( RegisterService.registerService == null )
            RegisterService.registerService = new RegisterService();
        return RegisterService.registerService;
    }

    public void saveUserProfileFile(
            @NonNull Uri fileUri,
            @NonNull com.google.android.gms.tasks.OnSuccessListener<? super UploadTask.TaskSnapshot> successListener,
            @NonNull com.google.android.gms.tasks.OnFailureListener failureListener
    ){
        StorageReference ref = storeageService.firebaseStorage.getReference().child(
                Constantes.imageEndPoint + UUID.randomUUID().toString()
        );
        ref.putFile(fileUri)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener);
    }
}
