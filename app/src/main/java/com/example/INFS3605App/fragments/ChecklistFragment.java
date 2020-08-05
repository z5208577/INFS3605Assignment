package com.example.INFS3605App.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.INFS3605App.R;
import com.example.INFS3605App.adapters.ChecklistAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChecklistFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChecklistFragment extends Fragment {
    public static final String website = "https://www.nsw.gov.au";
    public Spinner industrySpinner;
    public TextView safetyPlan,noResults;
    public String industryString;
    public RecyclerView checklistRecycler;
    public ChecklistAdapter mChecklistAdapter;
    public ArrayList<String> industries = new ArrayList<String>();
    public ArrayList<String> industriesURL = new ArrayList<String>();
    public ArrayList<String> checklistItems = new ArrayList<String>();


    public ChecklistFragment() {
        // Required empty public constructor
    }
    public static ChecklistFragment newInstance() {
        ChecklistFragment fragment = new ChecklistFragment();
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
        final View view = inflater.inflate(R.layout.fragment_checklist, container, false);
        industrySpinner = view.findViewById(R.id.industrySpinner);
        industrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                industryString = (String) parent.getSelectedItem();
                int industryHref = industries.indexOf(industryString);
                new ScanActions(industriesURL.get(industryHref)).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        new ScanIndustries().execute();
        checklistRecycler = (RecyclerView) view.findViewById(R.id.checklistRecycler);
        checklistRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        checklistRecycler.setHasFixedSize(true);
        safetyPlan = view.findViewById(R.id.safetyPlan);
        SpannableString content = new SpannableString("More Information and Official Safety Plan found here.");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        safetyPlan.setText(content);
        noResults = view.findViewById(R.id.noResults);
        noResults.setVisibility(View.GONE);
        return view;
    }


    private class ScanIndustries extends AsyncTask<Void,Void, ArrayList<String>> {
        @Override
        protected void onPostExecute(ArrayList<String> industries) {
            super.onPostExecute(industries);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext()
                    ,android.R.layout.simple_spinner_dropdown_item
                    ,industries);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            industrySpinner.setAdapter(adapter);
        }

        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            Document document = null;
            try{
                document = Jsoup.connect(website + "/covid-19/covid-safe-businesses#industries").get();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Could not get website", Toast.LENGTH_SHORT).show();
            }

            Elements elements = document.getElementsByClass("nsw-card__link");
            for (Element element: elements){
                industriesURL.add(element.attributes().get("href"));
                industries.add(element.text());
            }
            return industries;
        }
    }

    //class="nsw-wysiwyg-content"
    private class ScanActions extends AsyncTask<Void,Void,Elements>{
        public String link;

        ScanActions(String link){
            this.link = link;
        }
        @Override
        protected void onPostExecute(Elements elements) {
            super.onPostExecute(elements);
            mChecklistAdapter = new ChecklistAdapter(checklistItems,getContext());
            if(checklistItems.size()==0){
                noResults.setVisibility(View.VISIBLE);
            } else {
                noResults.setVisibility(View.GONE);
            }
            checklistRecycler.setAdapter(mChecklistAdapter);
            mChecklistAdapter.notifyDataSetChanged();
            safetyPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse(website+link));
                    startActivity(browserIntent);
                }
            });
        }

        @Override
        protected Elements doInBackground(Void... voids) {
            Document document = null;
            try{
                document = Jsoup.connect(website+ link).get();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Could not get website", Toast.LENGTH_SHORT).show();
            }

            Elements elements = document.getElementsByClass("nsw-accordion__content");
            checklistItems.clear();
            for (Element element: elements){
                String link = element.attributes().get("href");
                BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
                String source = element.text();
                iterator.setText(source);
                int start = iterator.first();
                for (int end = iterator.next();
                     end != BreakIterator.DONE;
                     start = end, end = iterator.next()) {
                    checklistItems.add(source.substring(start, end));
                }
            }
            return elements;
        }
    }

}
