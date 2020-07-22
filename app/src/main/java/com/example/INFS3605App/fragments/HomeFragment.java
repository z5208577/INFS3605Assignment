package com.example.INFS3605App.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.INFS3605App.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class HomeFragment extends Fragment {
    public ImageView alerts, discussion, worldnews, plans, restrictions, map, businessConnect;
    public BottomNavigationView mBottomNavigationView;
    public NavigationView navigationView;



    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        final AppCompatActivity activity = (AppCompatActivity) view.getContext();
        mBottomNavigationView= activity.findViewById(R.id.bottomNavigationView);
        enableCheckedItems(mBottomNavigationView);
        navigationView = activity.findViewById(R.id.nav_view);
        navigationView.getMenu().clear();
        navigationView.getMenu().findItem(R.id.drawer_home).setChecked(true);
        mBottomNavigationView.getMenu().findItem(R.id.bottomHome).setChecked(true);
        alerts = view.findViewById(R.id.alerts);
        alerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertsFragment alertsFragment= new AlertsFragment();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainFragment, alertsFragment, "alertsFragment")
                        .addToBackStack(null)
                        .commit();
                clearCheckedItems(mBottomNavigationView);
            }
        });
        discussion = view.findViewById(R.id.discusson);
        discussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                ForumFragment forumFragment= new ForumFragment();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainFragment, forumFragment, "forumFragment")
                        .addToBackStack(null)
                        .commit();
                mBottomNavigationView.getMenu().findItem(R.id.bottomForum).setChecked(true);
            }
        });
        worldnews = view.findViewById(R.id.worldnews);
        worldnews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                WorldCrisisNews worldCrisisNews = new WorldCrisisNews();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainFragment, worldCrisisNews, "worldnewsFragment")
                        .addToBackStack(null)
                        .commit();
                mBottomNavigationView.getMenu().findItem(R.id.bottomWorldCrisisNews).setChecked(true);
            }
        });
        plans = view.findViewById(R.id.plans);
        plans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                PlanFragment planFragment = new PlanFragment();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainFragment, planFragment, "postDetailFragment")
                        .addToBackStack(null)
                        .commit();
                clearCheckedItems(mBottomNavigationView);
            }
        });

        restrictions = view.findViewById(R.id.restrictions);
        restrictions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                PlanFragment planFragment= new PlanFragment();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainFragment, planFragment, "postDetailFragment")
                        .addToBackStack(null)
                        .commit();
                mBottomNavigationView.getMenu().findItem(R.id.bottomCrisisRestrictions).setChecked(true);
            }
        });
        map = view.findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                MapFragment mapFragment = new MapFragment();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainFragment, mapFragment, "postDetailFragment")
                        .addToBackStack(null)
                        .commit();
                mBottomNavigationView.getMenu().findItem(R.id.bottomCrisisMap).setChecked(true);
            }
        });

        businessConnect = view.findViewById(R.id.businessConnect);
        businessConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.business.nsw.gov.au/support-for-business/businessconnect"));
                startActivity(browserIntent);
            }
        });

        return view;
    }

    public static void clearCheckedItems(BottomNavigationView view) {
        int size = view.getMenu().size();
        for (int i = 0; i < size; i++) {
            view.getMenu().getItem(i).setChecked(false);
            view.getMenu().getItem(i).setCheckable(false);
        }
    }
    public static void enableCheckedItems(BottomNavigationView view) {
        int size = view.getMenu().size();
        for (int i = 0; i < size; i++) {
            view.getMenu().getItem(i).setChecked(false);
            view.getMenu().getItem(i).setCheckable(true);
        }
    }

}
