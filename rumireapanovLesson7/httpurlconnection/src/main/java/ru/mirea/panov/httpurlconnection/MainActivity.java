package ru.mirea.panov.httpurlconnection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.mirea.panov.httpurlconnection.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = null;
                if (connectivityManager != null) {
                    networkInfo = connectivityManager.getActiveNetworkInfo();
                }

                if (networkInfo != null && networkInfo.isConnected()) {
                    new DownloadPageTask().execute("https://ipinfo.io/json");
                } else {
                    Toast.makeText(MainActivity.this, "Нет интернета", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class DownloadPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.textViewLocate.setText("Загрузка...");
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                return downloadIpInfo(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject responseJSON = new JSONObject(result);

                String city = responseJSON.getString("city");
                String region = responseJSON.getString("region");
                String country = responseJSON.getString("country");
                String loc = responseJSON.getString("loc");
                String[] coords = loc.split(",");

                String locate = "Город: " + city +
                        "\nРегион: " + region +
                        "\nСтрана: " + country;

                double latitude = Double.parseDouble(coords[0]);
                double longitude = Double.parseDouble(coords[1]);

                new DownloadWeatherTask().execute("https://api.open-meteo.com/v1/forecast?latitude=" +
                        latitude +
                        "&longitude=" +
                        longitude +
                        "&current_weather=true");
                binding.textViewLocate.setText(locate);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }

        private String downloadIpInfo(String address) throws IOException {
            InputStream inputStream = null;
            String data = "";
            try {
                URL url = new URL(address);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("GET");
                connection.setInstanceFollowRedirects(true);
                connection.setUseCaches(false);
                connection.setDoInput(true);
                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    inputStream = connection.getInputStream();
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int read = 0;
                    while ((read = inputStream.read()) != -1) {
                        bos.write(read);
                    }
                    bos.close();
                    data = bos.toString();
                } else {
                    data = connection.getResponseMessage() + " Error Code " + responseCode;
                }
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return data;
        }
    }

    public class DownloadWeatherTask extends DownloadPageTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            binding.textViewWeather.setText("Загрузка...");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject responseJSON = new JSONObject(result);
                JSONObject currentWeather = responseJSON.getJSONObject("current_weather");
                String weather = "Температура: " + currentWeather.getString("temperature") + "°C" +
                        "\nСкорость ветра: " + currentWeather.getString("windspeed") + " km/h" +
                        "\nНаправление ветра: " + currentWeather.getString("winddirection") + "°";
                binding.textViewWeather.setText(weather);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }
}
