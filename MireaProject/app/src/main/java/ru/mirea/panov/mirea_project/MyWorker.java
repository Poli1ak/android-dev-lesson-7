package ru.mirea.panov.mirea_project;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyWorker extends Worker {

    private final Handler handler;

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        handler = new Handler(Looper.getMainLooper());
    }

    @NonNull
    @Override
    public Result doWork() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());
        final String currentDate = sdf.format(new Date());

        handler.post(new Runnable() {
            @Override
            public void run() {
                showToast(currentDate);
            }
        });

        return Result.success();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
