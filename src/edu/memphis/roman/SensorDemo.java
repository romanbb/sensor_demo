
package edu.memphis.roman;

import java.text.DecimalFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.TextView;

public class SensorDemo extends Activity implements SensorEventListener {

    public static final String TAG = "SensorDemo";

    private TelephonyManager mTelephonyManager;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mLightSensor;

    private TextView mLastScreenOffTime;
    private TextView mLastScreenOnTime;
    private TextView mGyroscopeValue;
    private TextView mAccelerometerValue;
    private TextView mLightSensorValue;
    private TextView mNetworkType;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context c, Intent i) {
            String action = i.getAction();

            if (Intent.ACTION_SCREEN_OFF.equals(action)) {

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                mLastScreenOffTime.setText(cal.getTime().toLocaleString());

            } else if (Intent.ACTION_SCREEN_ON.equals(action)) {

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                mLastScreenOnTime.setText(cal.getTime().toLocaleString());
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mLastScreenOffTime = (TextView) findViewById(R.id.screen_off_value);
        mLastScreenOnTime = (TextView) findViewById(R.id.screen_on_value);
        mGyroscopeValue = (TextView) findViewById(R.id.gyroscope_values);
        mAccelerometerValue = (TextView) findViewById(R.id.accelerometer_value);
        mLightSensorValue = (TextView) findViewById(R.id.light_sensor_values);
        mNetworkType = (TextView) findViewById(R.id.modem_values);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(new ModemNetworkListener(),
                PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);

        IntentFilter filters = new IntentFilter();
        filters.addAction(Intent.ACTION_SCREEN_OFF);
        filters.addAction(Intent.ACTION_SCREEN_ON);
        getApplicationContext().registerReceiver(mBroadcastReceiver, filters);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mLightSensor, SensorManager.SENSOR_DELAY_UI);

    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(this);

        super.onPause();
    }

    private void updateLight(SensorEvent event) {

        mLightSensorValue.setText(String.valueOf(event.values[0]));

    }

    private void updateGyroscope(SensorEvent event) {

        float x = event.values[0]; // x-axis rotation rate in radians/second.
        float y = event.values[1];
        float z = event.values[2];

        DecimalFormat df = new DecimalFormat("#.###");

        String s = "(" + df.format(x) + ", " + df.format(y) + ", " + df.format(z) + ")";
        mGyroscopeValue.setText(s);
    }

    private void updateAccelerometer(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        DecimalFormat df = new DecimalFormat("#.###");

        String s = "(" + df.format(x) + ", " + df.format(y) + ", " + df.format(z) + ")";
        mAccelerometerValue.setText(s);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                updateAccelerometer(event);
                break;
            case Sensor.TYPE_GYROSCOPE:
                updateGyroscope(event);
                break;
            case Sensor.TYPE_LIGHT:
                updateLight(event);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private class ModemNetworkListener extends PhoneStateListener {

        @Override
        public void onDataConnectionStateChanged(int state, int networkType) {
            super.onDataConnectionStateChanged(state, networkType);

            mNetworkType.setText(getNetworkText(networkType));
        }

        private String getNetworkText(int networkType) {

            switch (networkType) {
                default:
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    return "Unknown";
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return "CDMA";
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return "1xRTT";
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return "EDGE";
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                    return "EHRPD";
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return "EVDO_0";
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return "EVDO_A";
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return "EVDO_B";
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return "GPRS";
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return "HSDPA";
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return "HSPA";
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "HSPAP";
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return "HSUPA";
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return "IDEN";
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return "LTE";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return "UMTS";
            }
        }
    }
}
