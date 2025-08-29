package com.krizer.motionsensor;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private TextView detectionStatusTextView;
    private MotionSensor mMotionSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        detectionStatusTextView = findViewById(R.id.detectionStatusTextView);
        detectionStatusTextView.setText("센서 초기화 중...");

        String deviceModel = Build.MODEL != null ? Build.MODEL : "";
        Log.d(TAG, "Device MODEL = " + deviceModel);

        if (deviceModel.startsWith("Infos_Duple")) {
            onGpioRK3288();
            Log.i(TAG, "rk3288 on");
        } else if (deviceModel.startsWith("rk3399")) {
            onGpioRK3399();
            Log.i(TAG, "rk3399 on");
        } else {
        }
        mMotionSensor = new MotionSensor(this);
        mMotionSensor.setMotionDetectionListener(new MotionSensor.MotionDetectionListener() {
            @Override
            public void onMotionDetected(boolean detected) {
                runOnUiThread(() -> {
                    if (detected) {
                        detectionStatusTextView.setText("움직임 감지");
                        detectionStatusTextView.setBackgroundColor(getColor(android.R.color.holo_purple));
                    } else {
                        detectionStatusTextView.setText("움직임 감지 대기");
                        detectionStatusTextView.setBackgroundColor(getColor(android.R.color.holo_orange_light));
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    detectionStatusTextView.setText("오류: " + message);
                    detectionStatusTextView.setBackgroundColor(getColor(android.R.color.holo_red_light));
                });
                Log.e(TAG, "MotionSensor Error: " + message);
            }
        });
        mMotionSensor.init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMotionSensor != null) {
            mMotionSensor.release();
        }
    }

    private void onGpioRK3399() {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.item.gpio.a2.direction");
            intent.putExtra("value", "20");
            sendBroadcast(intent);
            Log.d(TAG, "onGpioRK3399: broadcast sent (a2.direction=20)");
        } catch (Exception e) {
            Log.e(TAG, "onGpioRK3399 failed: " + e.getMessage(), e);
        }
    }

    private void onGpioRK3288() {
        try {
            Intent intent2 = new Intent();
            intent2.setAction("android.intent.item.gpio.a0.direction");
            intent2.putExtra("value", "00");
            sendBroadcast(intent2);
            Log.d(TAG, "onGpioRK3288: broadcast sent (a0.direction=00)");
        } catch (Exception e) {
            Log.e(TAG, "onGpioRK3288 failed: " + e.getMessage(), e);
        }
    }
}
