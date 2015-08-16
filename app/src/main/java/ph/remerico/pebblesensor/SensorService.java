package ph.remerico.pebblesensor;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import de.greenrobot.event.EventBus;

/**
 * Created by rem on 8/16/15.
 */
public class SensorService extends Service {

    private static final String LOG_TAG = "SensorService";

    public static class PressureChangedEvent {

        public PressureChangedEvent(double hpa) {
            this.pressureHpa = hpa;
            this.altitude = hPaToAltitudeInFeet(hpa);
        }

        public double pressureHpa;
        public double altitude;

        public double altitudeInMeter() {
            return feetToMeter(altitude);
        }

        private static double hPaToAltitudeInFeet(double hPa) {
            double pstd = 1013.25f;
            return (1 - Math.pow((hPa/pstd), 0.190284d)) * 145366.45d;
        }

        private static double feetToMeter(double feet) {
            return 0.3048 * feet;
        }

    }


    private SensorManager sensorManager;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onCreate()");

        sensorManager = (SensorManager) getSystemService(Service.SENSOR_SERVICE);

        Sensor pS = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        if (pS != null) {
            sensorManager.registerListener(pressureListener, pS, SensorManager.SENSOR_DELAY_UI);
        }

    }

    @Override
    public void onDestroy() {

        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onDestroy()");

        super.onDestroy();
        sensorManager.unregisterListener(pressureListener);
    }


    private SensorEventListener pressureListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float[] values = sensorEvent.values;
            EventBus.getDefault().post(new PressureChangedEvent(values[0]));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

}
