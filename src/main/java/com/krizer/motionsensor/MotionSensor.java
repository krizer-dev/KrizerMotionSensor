package com.krizer.motionsensor;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class MotionSensor {
    private static final String TAG = "MotionSensor";
    private Context mContext;
    private boolean mIsInitialized = false;
    private boolean mIsRunning = false;
    private MotionDetectionListener mListener;
    private HandlerThread mPollingThread;
    private Handler mPollingHandler;
    private Runnable mPollingRunnable;
    private static final long POLLING_INTERVAL_MS = 200;
    private int mLastGpioValue = -1;

    public interface MotionDetectionListener {
        void onMotionDetected(boolean detected);
        void onError(String message);
    }
    public MotionSensor(Context context) {
        mContext = context;
    }
    public void setMotionDetectionListener(MotionDetectionListener listener) {
        mListener = listener;
    }

    public void init() {
        if (mIsInitialized) {
            Log.w(TAG, "MotionSensor already initialized.");
            if (mListener != null) mListener.onError("Motion sensor already initialized.");
            return;
        }

        mPollingThread = new HandlerThread("GpioPollingThread");
        mPollingThread.start();
        mPollingHandler = new Handler(mPollingThread.getLooper());

        mPollingRunnable = new Runnable() {
            @Override
            public void run() {
                if (!mIsRunning) return;
                String deviceModel = Build.MODEL;
                int currentGpioValue = -1;
                try {
                    if (deviceModel.startsWith("Infos_Duple")) {
                        currentGpioValue = Integer.parseInt(GpioApi.read_gpioA0());
                        Log.d(TAG, "RK3288 GPIO_A0. Value: " + currentGpioValue);
                    } else if (deviceModel.startsWith("rk3399")) {
                        currentGpioValue = Integer.parseInt(GpioApi.read_gpioA2());
                        Log.d(TAG, "RK3399 GPIO_A2. Value: " + currentGpioValue);
                    } else {
                        Log.w(TAG, "제품 확인 불가 ");
                    }

                    if (currentGpioValue != mLastGpioValue) {
                        mLastGpioValue = currentGpioValue;

                        boolean isMotionDetected = (currentGpioValue == 1);
                        if (mListener != null) {
                            mListener.onMotionDetected(isMotionDetected);
                        }
                    }

                } catch (NumberFormatException e) {
                    Log.e(TAG, "GPIO 값 파싱 오류: " + e.getMessage(), e);
                    if (mListener != null)
                        mListener.onError("GPIO 값 파싱 오류: " + e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, "GPIO 확인 필요 (GpioApi 호출 오류): " + e.getMessage(), e);
                    if (mListener != null)
                        mListener.onError("GpioApi 호출 오류: " + e.getMessage());
                } finally {
                    if (mIsRunning) {
                        mPollingHandler.postDelayed(this, POLLING_INTERVAL_MS);
                    }
                }
            }
        };

        mIsRunning = true;
        mPollingHandler.post(mPollingRunnable);

        mIsInitialized = true;
        Log.d(TAG, "Motion sensor initialized and polling started.");

        try {
            String deviceModel = Build.MODEL;
            int initialGpioValue;
            if (deviceModel.startsWith("Infos_Duple")) {
                initialGpioValue = Integer.parseInt(GpioApi.read_gpioA0());
            } else if (deviceModel.startsWith("rk3399")) {
                initialGpioValue = Integer.parseInt(GpioApi.read_gpioA2());
            } else {
                initialGpioValue = Integer.parseInt(GpioApi.read_gpioA0());
            }
            mLastGpioValue = initialGpioValue;
            if (mListener != null) {
                mListener.onMotionDetected(initialGpioValue == 1);
            }
        } catch (Exception e) {
            Log.e(TAG, "Initial GPIO read failed: " + e.getMessage(), e);
            if (mListener != null) mListener.onError("초기 GPIO 읽기 실패: " + e.getMessage());
        }
    }

    public void release() {
        if (!mIsInitialized) {
            Log.w(TAG, "모션센서 인식 실패");
            return;
        }

        mIsRunning = false;
        if (mPollingHandler != null) {
            mPollingHandler.removeCallbacks(mPollingRunnable);
        }
        if (mPollingThread != null) {
            mPollingThread.quitSafely();
            try {
                mPollingThread.join();
            } catch (InterruptedException e) {
                Log.e(TAG, "Failed to join polling thread.", e);
            }
        }
        mIsInitialized = false;
        Log.d(TAG, "Motion sensor released.");
    }
}