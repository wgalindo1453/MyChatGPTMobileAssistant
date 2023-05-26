package com.example.mychatgpt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    VoiceFragment voiceFragment = new VoiceFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.voice);
        //badgeDrawable.setVisible(true);
        //badgeDrawable.setNumber(1);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, homeFragment).commit();

        //set the selected item listener
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, homeFragment).commit();
                    return true;
                case R.id.voice:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, voiceFragment).commit();
                    return true;
                case R.id.settings:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, settingsFragment).commit();
                    return true;
            }
            return false;
        });



    }
}