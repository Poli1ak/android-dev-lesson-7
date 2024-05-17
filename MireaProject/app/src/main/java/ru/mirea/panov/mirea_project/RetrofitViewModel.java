package ru.mirea.panov.mirea_project;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitViewModel extends ViewModel {

    private final MutableLiveData<String> _quote = new MutableLiveData<>();
    public LiveData<String> quote = _quote;

    private final MutableLiveData<String> _author = new MutableLiveData<>();
    public LiveData<String> author = _author;

    private final QuoteApiService apiService;

    public RetrofitViewModel() {
        apiService = RetrofitClient.getInstance().create(QuoteApiService.class);
    }

    public void loadQuote() {
        apiService.getRandomQuote().enqueue(new Callback<Quote>() {
            @Override
            public void onResponse(Call<Quote> call, Response<Quote> response) {
                if (response.isSuccessful() && response.body() != null) {
                    _quote.setValue(response.body().getContent());
                    _author.setValue(response.body().getAuthor());
                } else {
                    Log.e("RetrofitViewModel", "Failed to load quote");
                }
            }

            @Override
            public void onFailure(Call<Quote> call, Throwable t) {
                Log.e("RetrofitViewModel", "Error loading quote", t);
            }
        });
    }
}
