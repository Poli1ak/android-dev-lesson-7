package ru.mirea.panov.mirea_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class BackgroundTaskFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_background_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scheduleBackgroundTask();
    }

    private void scheduleBackgroundTask() {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(MyWorker.class)
                .setInitialDelay(1, TimeUnit.SECONDS) // Задержка перед выполнением работы
                .build();
        WorkManager.getInstance(requireContext()).enqueue(workRequest);
    }

}
