package com.example.INFS3605App.fragments;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.INFS3605App.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlanFragment extends Fragment {
    private Map<String, String> actions;
    public static final String website = "https://www.nsw.gov.au";
    public TextView test;
    public Button covid, fire, terror, financial;
    public PlanFragment() {
        // Required empty public constructor
    }

    public static PlanFragment newInstance(String param1, String param2) {
        PlanFragment fragment = new PlanFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actions = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_crisis_plans, container, false);
        covid = view.findViewById(R.id.covid);
        covid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                PlanResourcesFragment planResourcesFragment= new PlanResourcesFragment();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainFragment, planResourcesFragment, "planResourcesGragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        fire = view.findViewById(R.id.fire);
        fire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commingSoon();
            }
        });
        terror = view.findViewById(R.id.terror);
        terror.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commingSoon();
            }
        });
        financial = view.findViewById(R.id.financial);
        financial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commingSoon();
            }
        });
        return view;
    }

    public void commingSoon(){
        Toast.makeText(getContext(), "This feature will be comming soon", Toast.LENGTH_SHORT).show();
    }

}
