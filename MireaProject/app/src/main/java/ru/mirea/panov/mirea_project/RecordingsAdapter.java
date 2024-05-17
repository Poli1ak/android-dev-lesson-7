package ru.mirea.panov.mirea_project;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;

public class RecordingsAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> recordings;

    public RecordingsAdapter(@NonNull Context context, ArrayList<String> recordings) {
        super(context, 0, recordings);
        this.context = context;
        this.recordings = recordings;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.recording_item, parent, false);
        }

        String recording = recordings.get(position);

        TextView textViewRecording = convertView.findViewById(R.id.textViewRecording);
        Button buttonPlayRecording = convertView.findViewById(R.id.buttonPlayRecording);

        textViewRecording.setText(recording);
        buttonPlayRecording.setOnClickListener(v -> playRecording(recording));

        return convertView;
    }

    private void playRecording(String filePath) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(context, "Playing recording", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnCompletionListener(mp -> {
            mp.release();
        });
    }
}
