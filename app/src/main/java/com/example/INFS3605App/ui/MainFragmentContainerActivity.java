package com.example.INFS3605App.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.INFS3605App.R;
import com.example.INFS3605App.fragments.ForumFragment;
import com.example.INFS3605App.fragments.MapFragment;
import com.example.INFS3605App.fragments.RestrictionsFragment;
import com.example.INFS3605App.fragments.HomeFragment;
import com.example.INFS3605App.fragments.WorldCrisisNews;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.view.Gravity.START;

public class MainFragmentContainerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public BottomNavigationView bottomNavigationView;
    public NavigationView navigationView;
    public DrawerLayout drawer;
    public ActionBarDrawerToggle toggle;
    public TextView drawerUserName, drawerUserEmail;
    public ImageView drawerUserDp;
    public FirebaseUser currentUser;
    public FirebaseAuth mFirebaseAuth;
    public boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment_container);
        //populate UI elements
        Toolbar toolbar = findViewById(R.id.topToolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this,drawer,toolbar
            ,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationListener);

        if(savedInstanceState == null){
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            Fragment fragment = new HomeFragment();
            Bundle arguments = new Bundle();
            fragment.setArguments(arguments);
            transaction.replace(R.id.mainFragment,fragment);
            transaction.commit();
            navigationView.setCheckedItem(R.id.drawer_home);
        }
        // load user detail
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUser = mFirebaseAuth.getCurrentUser();
        updateNavDrawer();

    }

    // populating bottom navigation view
    public BottomNavigationView.OnNavigationItemSelectedListener navigationListener = new BottomNavigationView.OnNavigationItemSelectedListener(){
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
            Fragment fragment = null;
            switch(menuItem.getItemId()){
                case R.id.bottomHome:
                    fragment = new HomeFragment();
                    uncheckAllMenuItems(navigationView);
                    break;
                case R.id.companyDiscussionBoard:
                    fragment = new ForumFragment();
                    uncheckAllMenuItems(navigationView);
                    break;
                case R.id.crisisRestrictions:
                    fragment = new RestrictionsFragment();
                    uncheckAllMenuItems(navigationView);
                    break;

                case R.id.worldCrisisNews:
                    fragment = new WorldCrisisNews();
                    uncheckAllMenuItems(navigationView);
                    break;

                case R.id.crisisMap:
                    fragment = new MapFragment();
                    uncheckAllMenuItems(navigationView);
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment,
                    fragment).commit();
            return true;
        }
    };

    //method to uncheck menu items
    public void uncheckAllMenuItems(NavigationView navigationView) {
        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.drawer_home:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFragment, new HomeFragment())
                        .commit();
                bottomNavigationView.setSelectedItemId(R.id.bottomHome);
                break;
            case R.id.logout:
                Intent intent = new Intent(MainFragmentContainerActivity.this, MainActivity.class);
                MainFragmentContainerActivity.this.startActivity(intent);
                break;
        }
        drawer.closeDrawer(navigationView);
        return true;
    }

    @Override
    public void onBackPressed(){
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }  else {
            //reference: https://stackoverflow.com/questions/8430805/clicking-the-back-button-twice-to-exit-an-activity
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                mFirebaseAuth.signOut();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        if (toggle != null) {
            toggle.syncState();
        }
    }

    public void updateNavDrawer(){
        drawerUserDp = navigationView.getHeaderView(0).findViewById(R.id.drawerUserDp);
        if (currentUser.getPhotoUrl()!=null){
            Glide.with(this).load(currentUser.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(drawerUserDp);
        }

        drawerUserEmail = navigationView.getHeaderView(0).findViewById(R.id.drawerUserEmail);
        drawerUserEmail.setText(currentUser.getEmail());

        drawerUserName = navigationView.getHeaderView(0).findViewById(R.id.drawerUserName);
        drawerUserName.setText(currentUser.getDisplayName());


    }

}
