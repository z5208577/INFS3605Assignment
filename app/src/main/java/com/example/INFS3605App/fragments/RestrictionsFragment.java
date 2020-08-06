package com.example.INFS3605App.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.INFS3605App.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RestrictionsFragment extends Fragment {
    public TextView restrictionsTextView,updateDate,furtherRestrictions;
    public String restrictionsList ="";
    public String update;

    public RestrictionsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_crisis_restrictions, container, false);
        restrictionsTextView = view.findViewById(R.id.restrictions);
        updateDate = view.findViewById(R.id.date);
        furtherRestrictions = view.findViewById(R.id.furtherRestrictions);
        SpannableString content = new SpannableString("Check here for full list of restrictions and guidelines.");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        furtherRestrictions.setText(content);
        furtherRestrictions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.nsw.gov.au/covid-19/what-you-can-and-cant-do-under-rules"));
                startActivity(browserIntent);
            }
        });
        new ScanRestrictions().execute();
        new ScanUpdateDate().execute();
        return view;
    }


    private class ScanRestrictions extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPostExecute(String restrictions) {
            super.onPostExecute(restrictions);
            restrictionsTextView.setText(restrictionsList);
        }

        @Override
        protected String doInBackground(Void... voids) {
            Document document = null;
            try {
                document = Jsoup.connect("https://www.nsw.gov.au/covid-19/what-you-can-and-cant-do-under-rules").get();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Could not get website", Toast.LENGTH_SHORT).show();
            }

            Element businessH2 = document.select("h2").get(16);
            Elements siblings = businessH2.siblingElements();
            List<Element> elementsBetween = new ArrayList<Element>();
            for (int i = 1; i < siblings.size(); i++) {
                Element sibling = siblings.get(i);
                if (i>37) {
                    restrictionsList = restrictionsList + sibling.text();
                } else {
                    processElementsBetween(elementsBetween);
                    elementsBetween.clear();
                }
            }
            if (!elementsBetween.isEmpty()) {
                processElementsBetween(elementsBetween);
            }
            return restrictionsList;
        }
    }

    private class ScanUpdateDate extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPostExecute(String restrictions) {
            super.onPostExecute(restrictions);
            updateDate.setText(update);
        }

        @Override
        protected String doInBackground(Void... voids) {
            Document document = null;
            try {
                document = Jsoup.connect("https://www.nsw.gov.au/covid-19/what-you-can-and-cant-do-under-rules").get();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Could not get website", Toast.LENGTH_SHORT).show();
            }
            Elements elements = document.getElementsByTag("small");
            for (Element element: elements){
              update = element.text();
            }
            return update;
        }
    }


    private static void processElementsBetween(List<Element> elementsBetween) {
        System.out.println("---");
        for (Element element : elementsBetween) {
            System.out.println(element.text());
        }
    }
}
