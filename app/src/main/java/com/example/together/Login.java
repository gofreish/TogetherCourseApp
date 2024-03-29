package com.example.together;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.together.service.LoginService;
import com.example.together.shared.executors.Executor;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private AppCompatButton loginButton;
    private AppCompatButton goToRegisterButton;
    private ProgressBar loginProgressBar;

    private EditText emailEditText;
    private EditText passwordEditText;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loginProgressBar = findViewById(R.id.loginProgressBar);
        emailEditText = findViewById(R.id.edit_account_email);
        passwordEditText = findViewById(R.id.edit_account_password);
        loginButton = findViewById(R.id.connexionBtn);
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tryLogin();
            }
        });

        goToRegisterButton = findViewById(R.id.loggin_register_button);
        goToRegisterButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent goToRegisterActivity = new Intent(Login.this, Register.class);
                startActivity(goToRegisterActivity);
            }
        });
    }

    private boolean isFormValid(String email, String password){
        if(email.isEmpty()){
            Toast.makeText(Login.this, "Entrez un email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.isEmpty()){
            Toast.makeText(Login.this, "Entrez un mot de passe", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void tryLogin(){
        loginProgressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if(!isFormValid(email, password)) return;

        Executor onLoginSuccess = new Executor() {
            @Override
            public void execute() {
                Intent goToMain = new Intent(Login.this, MainActivity.class);
                startActivity(goToMain);
            }
        };

        Executor onLoginFail = new Executor() {
            @Override
            public void execute() {
                loginProgressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);
                Toast.makeText(Login.this, "Email ou mot de passe incorrecte.",
                        Toast.LENGTH_SHORT).show();
            }
        };

        LoginService.getInstance().firebaseLogin(
                email,
                password,
                Login.this,
                onLoginSuccess,
                onLoginFail
        );
    }
}