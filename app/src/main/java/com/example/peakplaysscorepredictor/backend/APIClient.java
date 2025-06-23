package com.example.peakplaysscorepredictor.backend;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static final String BASE_URL = "https://v3.football.api-sports.io/";

    // For now, hardcoded API key - you can secure this later
    // Key - ca4a7aa31abf3acae6136d24af8a2e65
    private static final String API_KEY = "ca4a7aa31abf3acae6136d24af8a2e65";

    private static Retrofit retrofit;
    private static FootballAPIService apiService;

    public  static FootballAPIService getAPIService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(FootballAPIService.class);
        }
        return apiService;
    }

    public static String getApiKey() {
        return API_KEY;
    }
}