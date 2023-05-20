package com.example.practice;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class profdrawable extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
        DrawerLayout drawerLayout;
    public void setContentView(View view) {
        drawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.profdrawable, null);
        FrameLayout container = drawerLayout.findViewById(R.id.activityContainer);
        container.addView(view);
        super.setContentView(drawerLayout);

        Toolbar toolbar = drawerLayout.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = drawerLayout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        switch (item.getItemId()){
            case R.id.home:
                startActivity(new Intent(this, ProfHome.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.qr_generator:
                startActivity(new Intent(this, qr_generator.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.tracker:
                startActivity(new Intent(this, proftracker.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.schedule:
                startActivity(new Intent(this, profsched.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.profile:
                startActivity(new Intent(this, profprofile.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.addClass:
                startActivity(new Intent(this, enrollStudent.class));
                overridePendingTransition(0, 0);
                break;
            case R.id.log_out:
                startActivity(new Intent(this, welcomeScreen.class));
                overridePendingTransition(0, 0);

        }

        return false;
    }

    protected void allocateActivityTitle(String titleString) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleString);
        }
    }
}





