package ru.mirea.panov.mirea_project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManagementFragment extends DialogFragment {

    private List<String> fileNames = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_management, container, false);

        FloatingActionButton fabCreateFile = view.findViewById(R.id.fabCreateFile);
        fabCreateFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCreateFileDialog();
            }
        });

        return view;
    }

    private void openCreateFileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_create_file, null);

        final EditText editTextFileName = dialogView.findViewById(R.id.editTextFileName);
        final EditText editTextFileContent = dialogView.findViewById(R.id.editTextFileContent);

        builder.setView(dialogView)
                .setTitle("Create File")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String fileName = editTextFileName.getText().toString();
                        String fileContent = editTextFileContent.getText().toString();
                        createFile(fileName, fileContent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder.create().show();
    }

    private void createFile(String fileName, String fileContent) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, fileName + ".txt");

        try {
            if (file.createNewFile()) {
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(fileContent.getBytes());
                outputStream.close();
                Toast.makeText(requireContext(), "File created: " + fileName, Toast.LENGTH_SHORT).show();
                fileNames.add(fileName);
            } else {
                Toast.makeText(requireContext(), "File already exists", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error creating file", Toast.LENGTH_SHORT).show();
        }
    }
}
