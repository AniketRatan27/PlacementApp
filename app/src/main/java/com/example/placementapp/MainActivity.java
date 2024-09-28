package com.example.placementapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.EdgeToEdge;
import androidx.annotation.GravityInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.URI;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, LogIn.class);
            startActivity(intent);
            finish();
            return;
        }
//            Intent intent = new Intent(SignUp.this, MainActivity.class);
//            startActivity(intent);
//            finish();


        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        frameLayout = findViewById(R.id.frameLayout);
//        btn = findViewById(R.id.drawable_logout_btn1);

        //Step1


        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.OpenDrawer, R.string.CloseDrawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        loadFragment(new HomeFragment(), true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.navdrawer_resource) {
                  //  Toast.makeText(MainActivity.this, "resources Fragment", Toast.LENGTH_LONG).show();
                    loadFragment(new resourcefgm(),false);
//Because of the toast if you click the resource than it will not open as it will wait for the toast to close so  add fragments as soon as possible.
                } else if (id == R.id.navdrawer_website) {
                    //Toast.makeText(MainActivity.this, "College Website Fragment", Toast.LENGTH_LONG).show();
                    //same goes here
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.rlsbca.edu.in/"));
                    startActivity(intent);
                } else if (id == R.id.drawable_logout_btn1) {
                            firebaseAuth.signOut();
                            Intent intent = new Intent(MainActivity.this, LogIn.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Notes Fragment", Toast.LENGTH_LONG).show();
                    //same goes here
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();

                if (itemId == R.id.navChat) {
                    loadFragment(new ChatFragment(), false);
                } else if (itemId == R.id.navProfile) {
                    loadFragment(new AdminFormFgm(), false);
                } else {
                    loadFragment(new HomeFragment(), false);
                }
                return true;
            }
        });
    }

    //   we need to override onBackPressed() method

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            
        } else {
            super.onBackPressed();
        }
    }


//es method ko ham jaha bhi call karenge ,es me jo fragment pass karenge vo fragment show hoga!

    private void loadFragment(Fragment fragment, boolean isAppinitailized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppinitailized) {
            fragmentTransaction.add(R.id.frameLayout, fragment);
        } else {
            fragmentTransaction.replace(R.id.frameLayout, fragment);
            // here i changed 
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.commit();
    }


    public void setToolbarVisibility(boolean isVisible) {
        if (toolbar != null) {
            toolbar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }


    }
}