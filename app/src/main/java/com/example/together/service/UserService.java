package com.example.together.service;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.together.model.User;
import com.example.together.shared.Constantes;
import com.example.together.shared.executors.Executor;
import com.example.together.shared.executors.ImageUriExecutor;
import com.example.together.shared.executors.UserExecutor;
import com.example.together.shared.executors.UserListExecutor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private static UserService userService = null;
    public static List<User> allUsers = null;
    private StorageService storageService;
    private DatabaseReference userStorageReference;

    private UserService() {
        this.storageService = StorageService.getInstance();
        this.userStorageReference = this.storageService.firebaseDatabase.getReference().child(Constantes.userEndPoint);
    }

    public void firstGetAll(
            @NonNull UserListExecutor userListSuccessExecutor,
            @NonNull UserListExecutor userListFaillureExecutor
    ){
        OnCompleteListener<DataSnapshot> allUserListener = new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot allUserSnapshot = task.getResult();
                    List<User> allUsers = new ArrayList<User>();
                    for(DataSnapshot userSnapshot: allUserSnapshot.getChildren()){
                        User user = userSnapshot.getValue(User.class);
                        if ( !user.getUser_id().equals(StorageService.getInstance().currentUser.getUser_id()) ){
                            allUsers.add(user);
                        }
                    }
                    userListSuccessExecutor.executor(allUsers);
                }else{
                    userListFaillureExecutor.executor(null);
                }
            }
        };
        userStorageReference.get().addOnCompleteListener(allUserListener);
    }

    public void allUsersEventListener(
            UserExecutor onNewUserExecutor,
            UserExecutor onChangeUserExecutor
    ){
        userStorageReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                onNewUserExecutor.executor(snapshot.getValue(User.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                onChangeUserExecutor.executor(snapshot.getValue(User.class));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static UserService getInstance() {
        if (UserService.userService == null) {
            UserService.userService = new UserService();
        }
        return UserService.userService;
    }

    public void getCurrentUser(@NonNull String uid){

        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                storageService.currentUser = user;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        };
        DatabaseReference currentUserStorageReference = userStorageReference.child(uid);
        currentUserStorageReference.addValueEventListener(userListener);
    }

    public void editUser(
            @NonNull Uri profileUri,
            @NonNull String fileExtension,
            @NonNull User user,
            @NonNull Executor userSaveTaskCompleteListener,
            @NonNull Executor userSaveTaskFail
    ){

        ImageUriExecutor imageUriSuccessExecutor = new ImageUriExecutor() {
            @Override
            public void executor(Uri uri) {
                user.setProfile_img_url(uri.toString());
                StorageService.getInstance().currentUser = user;
                //on enregistre le user
                updateUser(
                        user,
                        userSaveTaskCompleteListener
                );
            }
        };

        ImageUriExecutor imageUriFaillureExecutor = new ImageUriExecutor() {
            @Override
            public void executor(Uri uri) {
                userSaveTaskFail.execute();
            }
        };
        /*
        //Quand l'upload de la photo de profile se termine
        OnCompleteListener<Uri> imageUploadListener = new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    user.setProfile_img_url(downloadUri.getPath());
                    StorageService.getInstance().currentUser = user;
                    //on enregistre le user
                    updateUser(
                            user,
                            userSaveTaskCompleteListener
                    );
                } else {
                    //on exécute le code d'echec
                    userSaveTaskFail.execute();
                }
            }
        };
        */
        FileService.getInstance().uploadImage(
                profileUri,
                fileExtension,
                //imageUploadListener
                imageUriSuccessExecutor,
                imageUriFaillureExecutor
        );
    }

    public void newUser(
            @NonNull Uri profileUri,
            @NonNull String fileExtension,
            @NonNull String UID,
            @NonNull String username,
            @NonNull String email,
            @NonNull Executor userSaveTaskCompleteListener,
            @NonNull Executor userSaveTaskFail
            ) {
        StorageService storageService = StorageService.getInstance();
        storageService.currentUser.setUsername(username);
        storageService.currentUser.setEmail(email);

        ImageUriExecutor imageUriSuccessExecutor = new ImageUriExecutor() {
            @Override
            public void executor(Uri uri) {
                StorageService.getInstance().currentUser.setProfile_img_url(uri.toString());
                //on enregistre le user
                saveUser(
                        UID,
                        userSaveTaskCompleteListener
                );
            }
        };

        ImageUriExecutor imageUriFaillureExecutor = new ImageUriExecutor() {
            @Override
            public void executor(Uri uri) {
                userSaveTaskFail.execute();
            }
        };
    /*
        //Quand l'upload de la photo de profile se termine
        OnCompleteListener<Uri> imageUploadListener = new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    StorageService.getInstance().currentUser.setProfile_img_url(downloadUri.getPath());
                    //on enregistre le user
                    saveUser(
                            UID,
                            userSaveTaskCompleteListener
                    );
                } else {
                    //on exécute le code d'echec
                    userSaveTaskFail.execute();
                }
            }
        };*/
        FileService.getInstance().uploadImage(
                profileUri,
                fileExtension,
                //imageUploadListener
                imageUriSuccessExecutor,
                imageUriFaillureExecutor
        );
    }

    public void updateUser(
            @NonNull User user,
            @NonNull Executor onUserSaveCompleted
    ) throws NullPointerException {
        //String userId = userStorageReference.push().getKey();
        //if (userId == null) throw new NullPointerException("Aucune nouvelle Id obtenue");

        OnCompleteListener onCompleteTaskListener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    onUserSaveCompleted.execute();
                }
            }
        };
        userStorageReference
                .child(user.getUser_id())
                .setValue(user)
                .addOnCompleteListener(onCompleteTaskListener);
    }

    public void saveUser(
            @NonNull String UID,
            @NonNull Executor onUserSaveCompleted
    ) throws NullPointerException {
        //String userId = userStorageReference.push().getKey();
        //if (userId == null) throw new NullPointerException("Aucune nouvelle Id obtenue");
        StorageService.getInstance().currentUser.setUser_id(UID);

        OnCompleteListener onCompleteTaskListener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    onUserSaveCompleted.execute();
                }
            }
        };
        userStorageReference
                .child(UID)
                .setValue(StorageService.getInstance().currentUser)
                .addOnCompleteListener(onCompleteTaskListener);
    }
}
