package com.ecotracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout fragmentContainer;
    private static boolean isLoggedIn = false;
    private static String currentUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fragmentContainer = findViewById(R.id.fragment_container);

        checkLoginStatus();
    }

    private void checkLoginStatus() {
        SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
        isLoggedIn = prefs.getBoolean("is_logged_in", false);
        currentUser = prefs.getString("current_user", "");

        if (isLoggedIn && !currentUser.isEmpty()) {
            EcoApp.getInstance().setCurrentUser(currentUser);
            showMainContent();
        } else {
            showLoginScreen();
        }
    }

    private void showLoginScreen() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();
        bottomNavigationView.setVisibility(View.GONE);
    }

    public void showMainContent() {
        bottomNavigationView.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new HomeFragment())
                            .commit();
                    return true;
                } else if (itemId == R.id.nav_actions) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ActionsFragment())
                            .commit();
                    return true;
                } else if (itemId == R.id.nav_history) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new HistoryFragment())
                            .commit();
                    return true;
                } else if (itemId == R.id.nav_challenges) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ChallengesFragment())
                            .commit();
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ProfileFragment())
                            .commit();
                    return true;
                }
                return false;
            }
        });
    }

    public static void logout() {
        isLoggedIn = false;
        currentUser = "";
    }

    public static void setCurrentUser(String user) {
        currentUser = user;
    }

    public static String getCurrentUser() {
        return currentUser;
    }
}