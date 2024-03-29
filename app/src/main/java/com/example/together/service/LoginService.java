package com.example.together.service;

import static android.content.ContentValues.TAG;

import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.together.shared.executors.Executor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

public class LoginService {
    private static LoginService loginService;
    private StorageService storageService;
    private LoginService(){
        storageService = StorageService.getInstance();
    }
    public static LoginService getInstance() {
        if( LoginService.loginService == null )
            LoginService.loginService = new LoginService();
        return LoginService.loginService;
    }
    public void firebaseLogin(
            @NonNull String email,
            @NonNull String password,
            @NonNull android.app.Activity activity,
            @NonNull Executor onCompleteListener,
            @NonNull Executor onFailListener
    )throws Resources.NotFoundException {
        //le listener de la terminaison
        OnCompleteListener<AuthResult> onLoginComplete = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = storageService.firebaseAuth.getCurrentUser();
                    if(user == null) throw new Resources.NotFoundException();
                    UserService.getInstance().getCurrentUser(user.getUid());
                    onCompleteListener.execute();
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    onFailListener.execute();
                }
            }
        };
        storageService.firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, onLoginComplete);
    }
}
