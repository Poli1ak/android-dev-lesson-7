package ru.mirea.panov.mirea_project;

import retrofit2.Call;
import retrofit2.http.GET;

public interface QuoteApiService {
    @GET("random")
    Call<Quote> getRandomQuote();
}