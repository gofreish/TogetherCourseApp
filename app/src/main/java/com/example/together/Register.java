package com.example.together;

import static android.content.ContentValues.TAG;


import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.together.service.RegisterService;
import com.example.together.service.UserService;
import com.example.together.shared.executors.Executor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class Register extends AppCompatActivity {

    private EditText emailEditText, usernameEditText, passwordEditText, confirmPasswordEditText;
    private AppCompatButton registerBtn, chooseImgBtn;
    private FirebaseAuth firebaseAuth;
    private ProgressBar registerProgressBar;
    private ImageView imageView;
    private Uri profileImgUri;

    //Définition d'une activité de sélection d'image
    ActivityResultLauncher<PickVisualMediaRequest> chooseImage =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    Log.d("PhotoPicker", "Image choisie: " + uri);
                    try {
                        profileImgUri = uri;
                        imageView.setImageURI(uri);
                        /*
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                getContentResolver(),
                                uri
                        );
                        imageView.setImageBitmap(bitmap);
                        */
                    }catch(Exception e){
                        Toast.makeText(Register.this, "Impossible de charger l'image", Toast.LENGTH_SHORT).show();
                        Log.e("Image View", "IO exception while loading image");
                    }
                } else {
                    Log.d("PhotoPicker", "Pas d'image choisie");
                    Toast.makeText(Register.this, "Aucune image choisie", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firebaseAuth = FirebaseAuth.getInstance();
        usernameEditText = findViewById(R.id.register_username);
        emailEditText = findViewById(R.id.register_email);
        passwordEditText = findViewById(R.id.register_password);
        confirmPasswordEditText = findViewById(R.id.register_password_confirm);
        registerBtn = findViewById(R.id.registerBtn);
        registerProgressBar = findViewById(R.id.registerProgressBar);
        imageView = findViewById(R.id.register_image_view);
        chooseImgBtn = findViewById(R.id.btn_register_profile);
        registerBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                register();
            }
        });
        chooseImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()
                );
            }
        });
    }

    private void uploadImage()
    {
        if (profileImgUri != null) {
            //On désactive les boutons et on met le loader
            registerProgressBar.setVisibility(View.VISIBLE);
            registerBtn.setEnabled(false);
            chooseImgBtn.setEnabled(false);

            //Définition des listeners
            OnSuccessListener<UploadTask.TaskSnapshot> successListener
                    = new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(
                        UploadTask.TaskSnapshot taskSnapshot
                ) {
                    registerProgressBar.setVisibility(View.GONE);
                    registerBtn.setEnabled(true);
                    chooseImgBtn.setEnabled(true);

                    Toast.makeText(Register.this,
                            "Image uploader",
                            Toast.LENGTH_SHORT).show();
                }
            };

            OnFailureListener failureListener = new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    registerProgressBar.setVisibility(View.GONE);
                    registerBtn.setEnabled(true);
                    chooseImgBtn.setEnabled(true);

                    Toast.makeText(Register.this,
                            "Une erreur est survenue",
                            Toast.LENGTH_SHORT).show();
                }
            };

            RegisterService.getInstance().saveUserProfileFile(profileImgUri, successListener, failureListener);
        }
    }

    private boolean isFormValid(
            String username,
            String email,
            String password,
            String confirmPass
    ){
        if(profileImgUri == null){
            Toast.makeText(Register.this, "Choisissez une photo de profile", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(username.isEmpty()){
            Toast.makeText(Register.this, "Entrez un Nom d'ultilisateur", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(email.isEmpty()){
            Toast.makeText(Register.this, "Entrez un email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.isEmpty()){
            Toast.makeText(Register.this, "Entrez un mot de passe", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            if(password.length()<6){
                Toast.makeText(Register.this, "Le mot de passe doit avoir au moins 6 caractères ", Toast.LENGTH_LONG).show();
                return false;
            }else
            if(!password.equals(confirmPass)){
                Toast.makeText(Register.this, "Confirmation du mot de passe différente", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void register(){
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPass = confirmPasswordEditText.getText().toString();

        if( !isFormValid(username, email, password, confirmPass) ){
            return;
        }

        registerProgressBar.setVisibility(View.VISIBLE);
        registerBtn.setEnabled(false);

        Executor onUserSaveComplete = new Executor() {
            @Override
            public void execute() {
                Toast.makeText(Register.this, "User enregistrer", Toast.LENGTH_LONG).show();
            }
        };
        Executor onUserSaveFail = new Executor() {
            @Override
            public void execute() {
                Toast.makeText(Register.this, "User Non enregistrer", Toast.LENGTH_LONG).show();
            }
        };

        OnCompleteListener<AuthResult> createUserCompleteListener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    UserService.getInstance().newUser(
                            profileImgUri,
                            getFileExtension(profileImgUri),
                            firebaseUser.getUid(),
                            username,
                            email,
                            onUserSaveComplete,
                            onUserSaveFail
                    );
                    uploadImage();
                    Intent goToMain = new Intent(Register.this, MainActivity.class);
                    startActivity(goToMain);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(Register.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    //updateUI(null);
                }
                registerProgressBar.setVisibility(View.GONE);
                registerBtn.setEnabled(true);
            }
        };

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(Register.this, createUserCompleteListener);
    }

    private String getFileExtension(@NonNull Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}