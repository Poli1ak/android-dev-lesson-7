package ru.mirea.panov.mirea_project;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class VoiceFragment extends Fragment {
    private Button buttonRecord, buttonStop, buttonPlay;
    private ListView listViewRecordings;
    private MediaRecorder mediaRecorder;
    private String currentFilePath;
    private boolean isRecording = false;
    private ArrayList<String> recordings = new ArrayList<>();
    private RecordingsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voice, container, false);
        buttonRecord = view.findViewById(R.id.buttonRecord);
        buttonStop = view.findViewById(R.id.buttonStop);
        buttonPlay = view.findViewById(R.id.buttonPlay);
        listViewRecordings = view.findViewById(R.id.listViewRecordings);

        adapter = new RecordingsAdapter(requireContext(), recordings);
        listViewRecordings.setAdapter(adapter);

        buttonRecord.setOnClickListener(v -> startRecording());
        buttonStop.setOnClickListener(v -> stopRecording());
        buttonPlay.setOnClickListener(v -> playLastRecording());

        buttonStop.setEnabled(false);
        buttonPlay.setEnabled(false);

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
        }

        return view;
    }

    private void startRecording() {
        if (isRecording) {
            Toast.makeText(getContext(), "Recording is already in progress", Toast.LENGTH_SHORT).show();
            return;
        }

        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File audioFile = new File(storageDir, "AUDIO_" + timeStamp + ".3gp");

        currentFilePath = audioFile.getAbsolutePath();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(currentFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
            buttonRecord.setEnabled(false);
            buttonStop.setEnabled(true);
            Toast.makeText(getContext(), "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (!isRecording) {
            Toast.makeText(getContext(), "No recording in progress", Toast.LENGTH_SHORT).show();
            return;
        }

        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;
        isRecording = false;

        recordings.add(currentFilePath);
        adapter.notifyDataSetChanged();

        buttonRecord.setEnabled(true);
        buttonStop.setEnabled(false);
        buttonPlay.setEnabled(true);
        Toast.makeText(getContext(), "Recording stopped", Toast.LENGTH_SHORT).show();
    }

    private void playLastRecording() {
        if (recordings.isEmpty()) {
            Toast.makeText(getContext(), "No recordings available to play", Toast.LENGTH_SHORT).show();
            return;
        }

        String lastRecording = recordings.get(recordings.size() - 1);
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(lastRecording);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(getContext(), "Playing last recording", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(mp -> {
            mp.release();
        });
    }
}
