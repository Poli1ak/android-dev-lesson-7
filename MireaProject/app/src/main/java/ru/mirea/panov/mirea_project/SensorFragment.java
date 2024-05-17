package ru.mirea.panov.mirea_project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.TextView;
import android.content.Context;

public class SensorFragment extends Fragment implements SensorEventListener {
    private SensorManager sensorManager;
    private TextView pressureTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensor, container, false);
        pressureTextView = view.findViewById(R.id.pressureTextView);
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            float pressure = event.values[0];
            float altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);
            pressureTextView.setText("Давление: " + pressure + " hPa\nВысота над уровнем моря: " + altitude + " м");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }
}
