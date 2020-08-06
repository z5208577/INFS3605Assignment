package com.example.INFS3605App.fragments;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.INFS3605App.R;
import com.example.INFS3605App.adapters.ApiAdapter;
import com.example.INFS3605App.adapters.ChecklistAdapter;
import com.example.INFS3605App.api.ApiClient;
import com.example.INFS3605App.api.ApiInterface;
import com.example.INFS3605App.ui.MainActivity;
import com.example.INFS3605App.utils.Article;
import com.example.INFS3605App.utils.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class AlertsFragment extends Fragment {
    private final String CHANNEL_ID = "personal_notifications";
    private final int NOTIFICATION_ID = 001;
    public static final String API_KEY = "ae25e23ee3d044f9a96b9911516abfaf";
    private Button updateAlerts;
    private TextView alertMoreInfo;
    private List<Article> articles = new ArrayList<>();
    public AlertsFragment() {
        // Required empty public constructor
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

        View view = inflater.inflate(R.layout.fragment_alerts, container, false);
        updateAlerts = view.findViewById(R.id.updateAlerts);
        updateAlerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (articles.size()>0){
                    displayNotification(articles.get(0));
                } else {
                    Toast.makeText(getContext(), "No new alerts. Please check the above link", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertMoreInfo = view.findViewById(R.id.alert_more_info);
        SpannableString content = new SpannableString("Check here for most current information.");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        alertMoreInfo.setText(content);
        alertMoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.nsw.gov.au/covid-19/find-facts-about-covid-19"));
                startActivity(browserIntent);
            }
        });
        LoadJson("New Cases NSW" );
        for (Article article:articles){
            if (!article.getPublishedAt().contains("days")||!article.getPublishedAt().contains("hours")){
                articles.remove(article);
            }
        }
        return view;
    }

    public void LoadJson(String keyword) {
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<News> call;
        call = apiInterface.getNews(keyword, API_KEY,"publishedAt","en");

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                System.out.println(response);
                if (response.isSuccessful() && response.body().getArticle() != null) {
                    if (!articles.isEmpty()) {
                        articles.clear();

                    }
                    articles = response.body().getArticle();


                } else {
                    Toast.makeText(getContext(), "Error retrieving articles", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Toast.makeText(getContext(), "Error retrieving articles", Toast.LENGTH_SHORT).show();
                System.out.println(t);
            }
        });
    }

    public void displayNotification(Article article) {
        createNotificationChannel();

        // When you click on the sent Notification, it redirects to the "DUMMY Activity" - Link it to something useful in our APP like Home?
        Intent landingInetent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(article.getUrl()));
        landingInetent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent landingPendingIntent = PendingIntent.getActivity(getContext(), 0,landingInetent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(),CHANNEL_ID);
        // Alan : Need Channel after Android 8.0
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle("Crisis Alert");
        builder.setContentText(article.getTitle());
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setContentIntent(landingPendingIntent);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.virus);
        // Icon
        builder.setLargeIcon(bitmap);
        //Large Picture
        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap));

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getContext());
        notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());

    }
    // Alan: Creating notification channel
    private void createNotificationChannel(){
        //Alan: Check Android Ver
        // This allows it work for Ver above Android8.0 or Api26 or higher

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            CharSequence name = "Personal Notification";
            String description = "COVID-19 NEWS ALERT";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,name,importance);

            notificationChannel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

        }

    }

}
