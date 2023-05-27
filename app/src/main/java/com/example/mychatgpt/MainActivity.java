package com.example.mychatgpt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.widget.ToggleButton;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    VoiceFragment voiceFragment = new VoiceFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    private static final String SAVED_FRAGMENT_TAG = "SAVED_FRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.voice);
        ToggleButton toggleButton = findViewById(R.id.themeToggleButton);

        toggleButton.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);

        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        String currentFragmentTag;
        if (savedInstanceState != null) {
            currentFragmentTag = savedInstanceState.getString(SAVED_FRAGMENT_TAG);
        } else {
            currentFragmentTag = "home";
        }

        switch (currentFragmentTag) {
            case "home":
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, homeFragment, "home").commit();
                break;
            case "voice":
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, voiceFragment, "voice").commit();
                break;
            case "settings":
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, settingsFragment, "settings").commit();
                break;
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, homeFragment, "home").commit();
                    return true;
                case R.id.voice:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, voiceFragment, "voice").commit();
                    return true;
                case R.id.settings:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, settingsFragment, "settings").commit();
                    return true;
            }
            return false;
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        String currentFragmentTag = getSupportFragmentManager().findFragmentById(R.id.frame_container).getTag();
        outState.putString(SAVED_FRAGMENT_TAG, currentFragmentTag);
    }
}
