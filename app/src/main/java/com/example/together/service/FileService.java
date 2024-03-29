package com.example.together.service;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.together.shared.Constantes;
import com.example.together.shared.executors.ImageUriExecutor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class FileService {
    private static FileService fileService;
    private StorageService storageService;
    private StorageReference imageStorageReference;
    private FileService(){
        storageService = StorageService.getInstance();
        imageStorageReference = storageService.firebaseStorage.getReference(Constantes.imageEndPoint);
    }

    public static FileService getInstance(){
        if(FileService.fileService==null)
            FileService.fileService = new FileService();
        return FileService.fileService;
    }

    public void uploadImage(
            @NonNull Uri imageUri,
            @NonNull String fileExtension,
            //@NonNull com.google.android.gms.tasks.OnCompleteListener<Uri> onCompleteListener,
            @NonNull ImageUriExecutor imageUriSuccessExecutor,
            @NonNull ImageUriExecutor imageUriFaillureExecutor

    ) {
        //String extension = MimeTypeMap.getFileExtensionFromUrl(imageUri.getPath());
        String imageRef = Constantes.imageEndPoint+"/"+ UUID.randomUUID().toString()+"."+fileExtension;
        StorageReference imageReference =  imageStorageReference.child(imageRef); //reference de l'image
        imageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        imageUriSuccessExecutor.executor(uri);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageUriFaillureExecutor.executor(null);
            }
        });
       /*
        UploadTask uploadTask = imageReference.putFile(imageUri);//tache de upload
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        // recupération de l'url de telechargement
                        return imageReference.getDownloadUrl();
                    }
                }).addOnCompleteListener(onCompleteListener);
        return imageRef;*/
    }
}
