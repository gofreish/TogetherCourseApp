package com.example.together.shared;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.together.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Executor;

public class UserTask<User> extends Task<User> {

    private User user;
    public UserTask(User user){
        super();
        this.user = user;
    }

    @NonNull
    @Override
    public Task addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
        return null;
    }

    @NonNull
    @Override
    public Task addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
        return null;
    }

    @NonNull
    @Override
    public Task addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
        return null;
    }

    @NonNull
    @Override
    public Task addOnSuccessListener(@NonNull OnSuccessListener onSuccessListener) {
        onSuccessListener.onSuccess(this.user);
        return this;
    }

    @NonNull
    @Override
    public Task addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener onSuccessListener) {
        return null;
    }

    @NonNull
    @Override
    public Task addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener onSuccessListener) {
        return null;
    }

    @Nullable
    @Override
    public Exception getException() {
        return null;
    }

    @Override
    public User getResult() {
        return null;
    }

    @Override
    public boolean isCanceled() {
        return false;
    }

    @Override
    public boolean isComplete() {
        return false;
    }

    @Override
    public boolean isSuccessful() {
        return false;
    }

    @Override
    public Object getResult(@NonNull Class aClass) throws Throwable {
        return null;
    }
}
