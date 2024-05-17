package ru.mirea.panov.timeservice;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.mirea.panov.timeservice.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final String host = "time.nist.gov"; // или time-a.nist.gov
    private final int port = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetTimeTask timeTask = new GetTimeTask();
                timeTask.execute();
            }
        });
    }

    private class GetTimeTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String timeResult = "";
            try {
                Socket socket = new Socket(host, port);
                BufferedReader reader = SocketUtils.getReader(socket);
                reader.readLine();
                timeResult = reader.readLine();
                Log.d("Socket", timeResult);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return timeResult;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String formattedDateTime = parseAndFormatTime(result);
            binding.textView.setText(formattedDateTime);
        }

        private String parseAndFormatTime(String timeString) {

            try {
                String[] parts = timeString.split(" ");
                if (parts.length < 3) {
                    return "Invalid time format";
                }
                String datePart = parts[1];
                String timePart = parts[2];

                SimpleDateFormat inputFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss", Locale.US);
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                Date date = inputFormat.parse(datePart + " " + timePart);
                return outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
                return "Error parsing time";
            }
        }
    }
}
