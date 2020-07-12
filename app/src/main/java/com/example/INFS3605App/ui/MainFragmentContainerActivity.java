package com.example.INFS3605App.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.INFS3605App.R;
import com.example.INFS3605App.fragments.ForumFragment;
import com.example.INFS3605App.fragments.MapFragment;
import com.example.INFS3605App.fragments.RestrictionsFragment;
import com.example.INFS3605App.fragments.HomeFragment;
import com.example.INFS3605App.fragments.WorldCrisisNews;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainFragmentContainerActivity extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment_container);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationListener);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = new HomeFragment();
        Bundle arguments = new Bundle();
        fragment.setArguments(arguments);
        transaction.replace(R.id.mainFragment,fragment);
        transaction.commit();

    }

    public BottomNavigationView.OnNavigationItemSelectedListener navigationListener = new BottomNavigationView.OnNavigationItemSelectedListener(){
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
            Fragment fragment = null;
            switch(menuItem.getItemId()){
                case R.id.home:
                    fragment = new HomeFragment();
                    break;
                case R.id.companyDiscussionBoard:
                    fragment = new ForumFragment();
                    break;
                case R.id.crisisRestrictions:
                    fragment = new RestrictionsFragment();
                    break;

                case R.id.worldCrisisNews:
                    fragment = new WorldCrisisNews();
                    break;

                case R.id.crisisMap:
                    fragment = new MapFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFragment,
                    fragment).commit();
            return true;
        }
    };

}
