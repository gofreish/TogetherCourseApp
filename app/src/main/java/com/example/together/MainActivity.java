package com.example.together;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.example.together.service.StorageService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;
    NavigationView navigationView;
    View headerView;
    TextView headerTextView;
    ImageView imageProfileView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance(); //TODO controller si le user est auth
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser==null){
            Toast.makeText(MainActivity.this, "Veuillez vous connecter.", Toast.LENGTH_LONG).show();
            Intent goToLogin = new Intent(MainActivity.this, Login.class);
            startActivity(goToLogin);
            finish();
        }
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        headerTextView = headerView.findViewById(R.id.headerUsername);
        headerTextView.setText(StorageService.getInstance().currentUser.getUsername());

        imageProfileView = headerView.findViewById(R.id.imageProfileView);
        Glide.with(this)
                .load(StorageService.getInstance().currentUser.getProfile_img_url())
                .into(imageProfileView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open_nav,
                R.string.close_nav
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.nav_logout){
            Log.i("Nav menu", "Loggin out");
            FirebaseAuth.getInstance().signOut();
            Intent goToLogin = new Intent(MainActivity.this, Login.class);
            startActivity(goToLogin);
            finish();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        if(menuItem.getItemId() == R.id.nav_desactivate){
            Log.i("Nav menu", "Delete account");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User account deleted.");
                    }
                }
            });
            Intent goToLogin = new Intent(MainActivity.this, Login.class);
            startActivity(goToLogin);
            finish();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        if(menuItem.getItemId() == R.id.nav_home){
            Log.i("Nav menu", "Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new HomeFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        if(menuItem.getItemId() == R.id.nav_user){
            Log.i("Nav menu", "Edit user");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new AccountFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        if(menuItem.getItemId() == R.id.nav_chat){
            Log.i("Nav menu", "Chat");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new AllConversationsFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        if(menuItem.getItemId() == R.id.nav_setting){
            Log.i("Nav menu", "Setting");
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new SettingFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        /*switch (menuItem.getItemId()) {
            case 1000025://R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new HomeFragment()).commit();
                break;
            case 1000015://R.id.nav_setting:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new SettingFragment()).commit();
                break;
            case 1000019://R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent goToLogin = new Intent(MainActivity.this, Login.class);
                startActivity(goToLogin);
                finish();
                break;
            default:
                break;
        }*/
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }
}