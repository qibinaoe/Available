package com.scuavailable.available;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Camera;
import android.os.Bundle;
import android.widget.TextView;

import com.scuavailable.available.util.CameraHelper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivityTestHere";
    private OpencvJni openCvJni;
    private CameraHelper cameraHelper;
    int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openCvJni = new OpencvJni();

    }



}
