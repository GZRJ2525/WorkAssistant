package com.gzrijing.workassistant.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.List;

/**
 * 方向传感器
 */
public class MyOrientationListener implements SensorEventListener {
    private Context context;
    private SensorManager sensorManager;
    private Sensor magneticSensor;          //地磁传感器
    private Sensor accelerometerSensor;     //加速度传感器
    private float[] accelerometerValues = new float[3];
    private float[] magneticValues = new float[3];
    private float lastRotateDegree;
    private OnOrientationListener onOrientationListener;

    public MyOrientationListener(Context context) {
        this.context = context;
    }

    public void start() {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        //  获得当前手机支持的所有传感器
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensors) {
            //  输出当前传感器的名称
            Log.e("sensor", sensor.getName() + "\n");
        }


        if (sensorManager != null) {
            magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if (magneticSensor != null) {
            sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_UI);
        }
        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // 判断当前是加速度传感器还是地磁传感器
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // 注意赋值时要调用clone()方法
            accelerometerValues = event.values.clone();
        } else if ((event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)) {
            // 注意赋值时要调用clone()方法
            magneticValues = event.values.clone();
        }

        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticValues);
        SensorManager.getOrientation(R, values);

        float rotateDegree = (float) Math.toDegrees(values[0]);
        if (Math.abs(rotateDegree - lastRotateDegree) > 1.0) {
            onOrientationListener.onOrientationChanged(rotateDegree);
        }
        lastRotateDegree = rotateDegree;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void setOnOrientationListener(OnOrientationListener onOrientationListener) {
        this.onOrientationListener = onOrientationListener;
    }


    public interface OnOrientationListener {
        void onOrientationChanged(float x);
    }
}
