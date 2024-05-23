package com.example.roommade;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationBarView;

public class Mainmenu extends AppCompatActivity {
    FragmentHome home;
    FragmentCommunity community;
    FragmentMypage mypage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        home = new FragmentHome();
        community = new FragmentCommunity();
        mypage = new FragmentMypage();

        //기본화면 설정
        getSupportFragmentManager().beginTransaction().replace(R.id.containers, home).commit();

        NavigationBarView navigationBarView = findViewById(R.id.bottom_navigationview);
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.containers, home).commit();
                    return true;
                } else if (itemId == R.id.community) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.containers, community).commit();
                    return true;
                } else if (itemId == R.id.mypage) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.containers, mypage).commit();
                    return true;
                }
                return false;
            }
        });
    }
}