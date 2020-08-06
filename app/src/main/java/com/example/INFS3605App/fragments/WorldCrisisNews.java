package com.example.INFS3605App.fragments;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.example.INFS3605App.R;
import com.example.INFS3605App.adapters.ApiAdapter;
import com.example.INFS3605App.api.ApiClient;
import com.example.INFS3605App.api.ApiInterface;
import com.example.INFS3605App.api.Utils;
import com.example.INFS3605App.ui.MainActivity;
import com.example.INFS3605App.utils.Article;
import com.example.INFS3605App.utils.News;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorldCrisisNews extends Fragment {

    public static final String API_KEY = "ae25e23ee3d044f9a96b9911516abfaf";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> articles = new ArrayList<>();
    private ApiAdapter adapter;
    private String TAG = MainActivity.class.getSimpleName();



    public WorldCrisisNews() {

        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void LoadJson() {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        String country = Utils.getCountry();
        Call<News> call;
        call = apiInterface.getNews("covid-19", API_KEY);

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                System.out.println(response);
                if (response.isSuccessful() && response.body().getArticle() != null) {
                    if (!articles.isEmpty()) {
                        articles.clear();

                    }
                    articles = response.body().getArticle();
                    adapter = new ApiAdapter(articles, getActivity());
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                } else {
                    Toast.makeText(getContext(), "Alan", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Toast.makeText(getContext(), "Alan", Toast.LENGTH_SHORT).show();
                System.out.println(t);
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_world_crisis_news, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        LoadJson();
        recyclerView.setAdapter(adapter);
        return view;

    }


}

