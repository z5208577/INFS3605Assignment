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
import com.example.INFS3605App.fragments.SettingsFragment;
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
            transaction.addToBackStack(null);
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
                    navigationView.getMenu().setGroupCheckable(0, false, true);
                    break;
                case R.id.bottomForum:
                    fragment = new ForumFragment();
                    navigationView.getMenu().setGroupCheckable(0, false, true);
                    break;
                case R.id.bottomCrisisRestrictions:
                    fragment = new RestrictionsFragment();
                    navigationView.getMenu().setGroupCheckable(0, false, true);
                    break;
                case R.id.bottomWorldCrisisNews:
                    fragment = new WorldCrisisNews();
                    navigationView.getMenu().setGroupCheckable(0, false, true);
                    break;

                case R.id.bottomCrisisMap:

                    fragment = new MapFragment();
                    navigationView.getMenu().setGroupCheckable(0, false, true);


                    Toast.makeText(getApplicationContext(), "This feature will be comming soon", Toast.LENGTH_SHORT).show();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment,
                    fragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.drawer_home:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFragment, new HomeFragment())
                        .addToBackStack(null)
                        .commit();
                navigationView.getMenu().findItem(R.id.drawer_home).setChecked(true);
                bottomNavigationView.setSelectedItemId(R.id.bottomHome);
                break;
            case R.id.drawer_settings:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFragment, new SettingsFragment())
                        .addToBackStack(null)
                        .commit();
                navigationView.getMenu().findItem(R.id.drawer_settings).setChecked(true);
                bottomNavigationView.getMenu().setGroupCheckable(0, false, true);
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
            //Checking for fragment count on backstack
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else if (!doubleBackToExitPressedOnce) {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this,"Please click BACK again to exit.", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce= false;
                    }}, 2000);
            } else {
                super.onBackPressed();
                mFirebaseAuth.signOut();
                return;
            }
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
        } else {
            Glide.with(this).load("https://firebasestorage.googleapis.com/v0/b/infs3605-32bdc.appspot.com/o/userDps%2FdefaultUser.jpg?alt=media&token=d0ae4498-18f3-4195-a07f-e9ee351273e2 " )
                    .apply(RequestOptions.circleCropTransform()).into(drawerUserDp);
        }

        drawerUserEmail = navigationView.getHeaderView(0).findViewById(R.id.drawerUserEmail);
        drawerUserEmail.setText(currentUser.getEmail());

        drawerUserName = navigationView.getHeaderView(0).findViewById(R.id.drawerUserName);
        drawerUserName.setText(currentUser.getDisplayName());


    }



}
