package jifa.racecarsandstuff;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {
    private volatile GameThread thread;
    private Handler mHandler;

    Sensor accelerometer;
    Sensor magnetometer;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //Get the holder of the screen and register interest
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        //Set up a handler for messages from GameThread
        mHandler = new Handler() {
        };
    }

    public void cleanup() {
        this.thread.setRunning(false);
        this.thread.cleanup();

        this.removeCallbacks(thread);
        thread = null;

        this.setOnTouchListener(null);

        SurfaceHolder holder = getHolder();
        holder.removeCallback(this);
    }

    public void setThread(GameThread newThread) {

        thread = newThread;

        setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                return thread != null && thread.onTouch(event);
            }
        });

        setClickable(true);
        setFocusable(true);
    }

    public GameThread getThread() {
        return thread;
    }

    public Handler getmHandler() {
        return mHandler;
    }

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if(thread!=null) {
            if (!hasWindowFocus)
                thread.pause();
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        if(thread!=null) {
            thread.setRunning(true);

            if(thread.getState() == Thread.State.NEW){
                thread.start();
            }
            else {
                if(thread.getState() == Thread.State.TERMINATED){
                    thread = new TheGame(this);
                    thread.setRunning(true);
                    thread.start();
                }
            }
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(thread!=null) {
            thread.setSurfaceSize(width, height);
        }
    }

    public void surfaceDestroyed(SurfaceHolder arg0) {
        boolean retry = true;
        if(thread!=null) {
            thread.setRunning(false);
        }

        while (retry) {
            try {
                if(thread!=null) {
                    thread.join();
                }
                retry = false;
            }
            catch (InterruptedException e) {
            }
        }
    }

    public void startSensor(SensorManager sm) {
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        sm.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);

    }

    public void removeSensor(SensorManager sm) {
        sm.unregisterListener(this);
        accelerometer = null;
        magnetometer = null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(thread!=null) {
            thread.onSensorChanged(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}