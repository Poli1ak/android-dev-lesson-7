package ru.mirea.panov.mirea_project;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ProfileFragment extends Fragment {

    private EditText editTextName, editTextSurname, editTextGroup, editTextListNumber, editTextWorkplace, editTextStudyPlace;
    private Button buttonSave;

    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "profile_pref";
    private static final String KEY_NAME = "name";
    private static final String KEY_SURNAME = "surname";
    private static final String KEY_GROUP = "group";
    private static final String KEY_LIST_NUMBER = "list_number";
    private static final String KEY_WORKPLACE = "workplace";
    private static final String KEY_STUDY_PLACE = "study_place";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        editTextName = view.findViewById(R.id.editTextName);
        editTextSurname = view.findViewById(R.id.editTextSurname);
        editTextGroup = view.findViewById(R.id.editTextGroup);
        editTextListNumber = view.findViewById(R.id.editTextListNumber);
        editTextWorkplace = view.findViewById(R.id.editTextWorkplace);
        editTextStudyPlace = view.findViewById(R.id.editTextStudyPlace);
        buttonSave = view.findViewById(R.id.buttonSave);

        sharedPreferences = getActivity().getSharedPreferences(PREF_NAME, getActivity().MODE_PRIVATE);

        editTextName.setText(sharedPreferences.getString(KEY_NAME, ""));
        editTextSurname.setText(sharedPreferences.getString(KEY_SURNAME, ""));
        editTextGroup.setText(sharedPreferences.getString(KEY_GROUP, ""));
        editTextListNumber.setText(sharedPreferences.getString(KEY_LIST_NUMBER, ""));
        editTextWorkplace.setText(sharedPreferences.getString(KEY_WORKPLACE, ""));
        editTextStudyPlace.setText(sharedPreferences.getString(KEY_STUDY_PLACE, ""));

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        return view;
    }

    private void saveData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NAME, editTextName.getText().toString());
        editor.putString(KEY_SURNAME, editTextSurname.getText().toString());
        editor.putString(KEY_GROUP, editTextGroup.getText().toString());
        editor.putString(KEY_LIST_NUMBER, editTextListNumber.getText().toString());
        editor.putString(KEY_WORKPLACE, editTextWorkplace.getText().toString());
        editor.putString(KEY_STUDY_PLACE, editTextStudyPlace.getText().toString());
        editor.apply();
    }
}
