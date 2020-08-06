package com.example.INFS3605App.api;

import com.example.INFS3605App.utils.News;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("everything")
    Call<News> getNews(
            @Query("q") String keyword,
            @Query("apiKey") String apiKey


    );



}
