package com.example.INFS3605App.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.INFS3605App.R;

public class PlanResourcesFragment extends Fragment {
    public Button plan,supp;
    public static final String website = "https://www.nsw.gov.au";

    public PlanResourcesFragment() {
        // Required empty public constructor
    }


    public static PlanResourcesFragment newInstance(String param1, String param2) {
        PlanResourcesFragment fragment = new PlanResourcesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_crisis_plans_resources, container, false);

        plan = view.findViewById(R.id.safetyPlans);
        plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                ChecklistFragment checklistFragment= new ChecklistFragment();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainFragment, checklistFragment, "checklistFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        supp = view.findViewById(R.id.finansupp);
        supp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "This feature will be comming soon", Toast.LENGTH_SHORT).show();
            }
        });
         return view;
    }
}
