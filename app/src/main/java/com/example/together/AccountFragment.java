package com.example.together;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.together.model.User;
import com.example.together.service.StorageService;
import com.example.together.service.UserService;
import com.example.together.shared.executors.Executor;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AccountFragment extends Fragment {

    private EditText emailEditText, usernameEditText, passwordEditText, newPasswordEditText;
    private AppCompatButton editUserBtn, chooseImgBtn;
    private FirebaseAuth firebaseAuth;
    private ProgressBar editProfileProgressBar;
    private ImageView imageView;
    private Uri profileImgUri;


    ActivityResultLauncher<PickVisualMediaRequest> chooseImage =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    Log.d("PhotoPicker", "Image choisie: " + uri);
                    try {
                        profileImgUri = uri;
                        Glide.with(AccountFragment.this).clear(imageView);
                        imageView.setImageURI(uri);
                        /*Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                requireActivity().getContentResolver(),
                                uri
                        );
                        imageView.setImageBitmap(bitmap);*/
                    }catch(Exception e){
                        Toast.makeText(requireActivity(), "Impossible de charger l'image", Toast.LENGTH_SHORT).show();
                        Log.e("Image View", "IO exception while loading image");
                    }
                } else {
                    Log.d("PhotoPicker", "Pas d'image choisie");
                    Toast.makeText(requireActivity(), "Aucune image choisie", Toast.LENGTH_SHORT).show();
                }
            });

    public AccountFragment() {
        // Required empty public constructor
    }
    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        User user = StorageService.getInstance().currentUser;

        imageView = view.findViewById(R.id.edit_account_image_view);
        Glide.with(this).load(user.getProfile_img_url()).into(imageView);
        usernameEditText = view.findViewById(R.id.edit_account_username);
        usernameEditText.setText(user.getUsername());
        emailEditText = view.findViewById(R.id.edit_account_email);
        emailEditText.setText(user.getEmail());
        passwordEditText = view.findViewById(R.id.edit_account_password);
        newPasswordEditText = view.findViewById(R.id.edit_account_new_password);
        editUserBtn = view.findViewById(R.id.editAccountBtn);
        editUserBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                editUser();
            }
        });
        editProfileProgressBar = view.findViewById(R.id.editAccountProgressBar);
        imageView = view.findViewById(R.id.register_image_view);
        chooseImgBtn = view.findViewById(R.id.btn_edit_profile);
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

    private void editUser(){
        String username = usernameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String newPass = newPasswordEditText.getText().toString();

        if( !isFormValid(username, email, password, newPass) ){
            return;
        }
        editUserBtn.setEnabled(false);
        editProfileProgressBar.setVisibility(View.VISIBLE);
        User currentUser = StorageService.getInstance().currentUser;
        User user = new User(
                currentUser.getUser_id(),
                username,
                currentUser.getEmail(),
                ""
                );

        Executor onUserSaveComplete = new Executor() {
            @Override
            public void execute() {
                Toast.makeText(requireActivity(), "Profile modifier", Toast.LENGTH_LONG).show();
                editUserBtn.setEnabled(true);
                editProfileProgressBar.setVisibility(View.GONE);
            }
        };
        Executor onUserSaveFail = new Executor() {
            @Override
            public void execute() {
                Toast.makeText(requireActivity(), "Erreur de modification", Toast.LENGTH_LONG).show();
                editUserBtn.setEnabled(true);
                editProfileProgressBar.setVisibility(View.GONE);
            }
        };

        UserService.getInstance().editUser(
                profileImgUri,
                getFileExtension(profileImgUri),
                user,
                onUserSaveComplete,
                onUserSaveFail
        );

    }
    private boolean isFormValid(
            String username,
            String email,
            String password,
            String newPass
    ){
        if(profileImgUri == null){
            Toast.makeText(requireActivity(), "Choisissez une photo de profile", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(username.isEmpty()){
            Toast.makeText(requireActivity(), "Entrez un Nom d'ultilisateur", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.isEmpty()){
            Toast.makeText(requireActivity(), "Entrez l'ancien mot de passe", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(newPass.isEmpty() || newPass.length()<6){
            Toast.makeText(requireActivity(), "Entrez un nouveau mot de passe de plus de 7 caractères", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String getFileExtension(@NonNull Uri uri){
        ContentResolver contentResolver = requireActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}