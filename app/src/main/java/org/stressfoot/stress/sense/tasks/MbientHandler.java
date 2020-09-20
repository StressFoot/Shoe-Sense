package org.stressfoot.stress.sense.tasks;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import com.mbientlab.metawear.Data;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.Route;
import com.mbientlab.metawear.Subscriber;
import com.mbientlab.metawear.android.BtleService;
import com.mbientlab.metawear.builder.RouteBuilder;
import com.mbientlab.metawear.builder.RouteComponent;
import com.mbientlab.metawear.data.Acceleration;
import com.mbientlab.metawear.module.Accelerometer;

import org.stressfoot.stress.sense.service.BLEDataReadService;
import org.stressfoot.stress.sense.util.ConsolePreferences;
import org.stressfoot.stress.sense.util.Constants;
import org.stressfoot.stress.sense.util.StressSensePreferences;

import java.io.File;
import java.util.ArrayList;

import bolts.Continuation;
import bolts.Task;

public class MbientHandler implements ServiceConnection {
    private Context context;
    private boolean isConnected;
    private StressSensePreferences stressSensePreferences;

    private BtleService.LocalBinder serviceBinder;
    private Accelerometer accelerometer;
    private MetaWearBoard board;

    private ArrayList<Float> xValues = new ArrayList<Float>(500);
    private ArrayList<Float> yValues = new ArrayList<Float>(500);
    private ArrayList<Float> zValues = new ArrayList<Float>(500);
    private ArrayList<Float> sumList = new ArrayList<Float>();
    private float sum;
    private boolean calibrationMode;
    private int selectedAxis = 2;

    private static MbientHandler instance;

    public MbientHandler(Context context) {
        this.context = context;
        instance = this;
        context.bindService(new Intent(context, BtleService.class),
                this, Context.BIND_AUTO_CREATE);

        context.registerReceiver(sensorReceiver, getIntentFilters());

        stressSensePreferences = StressSensePreferences.getInstance(context);
        ConsolePreferences.getInstance(context).clearPreferences();

        showToast("Setting things up...");
    }

    public static MbientHandler getInstance() {
        return instance;
    }

    private final BroadcastReceiver sensorReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            String rootDir = context.getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath() + "/StressShoe_temp.png";

            assert action != null;
            switch (action) {
                case Constants.ACTION_START_ACCELEROMETER:
                    if (!isConnected) {
                        isConnected = true;
                        accelerometer.acceleration().start();
                        accelerometer.start();

                        File file = new File(rootDir);
                        if (file.exists()) {
                            file.delete();
                        }

                        Intent bleService = new Intent(context, BLEDataReadService.class);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(bleService);
                        } else {
                            context.startService(bleService);
                        }
                    }
                    break;
                case Constants.ACTION_STOP_ACCELEROMETER:
                    stopAccelerometer();
                    break;
                case Constants.ACTION_CALIBRATE:
                    calibrationMode = true;
                    break;
            }
        }
    };

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        ///< Typecast the binder to the service's LocalBinder class
        serviceBinder = (BtleService.LocalBinder) service;

        retrieveBoard();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
    }

    public void retrieveBoard() {
        final BluetoothManager btManager =
                (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothDevice remoteDevice =
                btManager.getAdapter().getRemoteDevice(stressSensePreferences.getMacAddress());

        // Create a MetaWear board object for the Bluetooth Device
        board = serviceBinder.getMetaWearBoard(remoteDevice);
        board.onUnexpectedDisconnect(new MetaWearBoard.UnexpectedDisconnectHandler() {
            @Override
            public void disconnected(int status) {
                Log.e("ble_disconnect", "*******unexpected disconnect");
                if (isConnected) {
                    context.sendBroadcast(new Intent(Constants.ACTION_ALERT_USER));
                    stopAccelerometer();
                }
                context.sendBroadcast(new Intent(Constants.ACTION_BLE_DISCONNECTED));
                destroyMbient();
                //retrieveBoard();
            }
        });
        board.connectAsync().onSuccessTask(new Continuation<Void, Task<Route>>() {
            @Override
            public Task<Route> then(Task<Void> task) throws Exception {
                Log.i("sensor", "CONNECTED");
                accelerometer = board.getModule(Accelerometer.class);
                accelerometer.configure()
                        .odr(50f)   //50Hz
                        .commit();
                return accelerometer.acceleration().addRouteAsync(new RouteBuilder() {
                    @Override
                    public void configure(RouteComponent source) {
                        source.stream(new Subscriber() {
                            @Override
                            public void apply(Data data, Object... env) {
                                if (!isConnected) {
                                    Log.v("sensor", "Resetting accelerometer");
                                    stopAccelerometer();
                                    return;
                                }
                                //Log.i("MainActivity", data.value(Acceleration.class).toString());
                                if (zValues.size() <= 500) { //50Hz x 10s
                                    xValues.add(data.value(Acceleration.class).x());
                                    yValues.add(data.value(Acceleration.class).y());
                                    zValues.add(data.value(Acceleration.class).z());
                                    int size = zValues.size();
                                    if (size >= 2) {
                                        float x = Math.abs(xValues.get(size - 1) - xValues.get(size - 2));
                                        float y = Math.abs(yValues.get(size - 1) - yValues.get(size - 2));
                                        float z = Math.abs(zValues.get(size - 1) - zValues.get(size - 2));
                                        sum += Math.max(Math.max(x, y), z);

                                        if (size % 50 == 0) {
                                            sumList.add(sum);
                                            sum = 0f;
                                        }
                                    }
                                } else {
                                    if (calibrationMode) {
                                        Log.i(this.getClass().getName(), "calibration mode: ");

                                        double xMean = xValues.stream().mapToDouble(val -> val).average().orElse(0.0);
                                        double yMean = yValues.stream().mapToDouble(val -> val).average().orElse(0.0);
                                        double zMean = zValues.stream().mapToDouble(val -> val).average().orElse(0.0);

                                        double xVar = xValues.stream().map(i -> i - xMean).map(i -> i * i).mapToDouble(i -> i).average().orElse(0.0);
                                        double yVar = yValues.stream().map(i -> i - yMean).map(i -> i * i).mapToDouble(i -> i).average().orElse(0.0);
                                        double zVar = zValues.stream().map(i -> i - zMean).map(i -> i * i).mapToDouble(i -> i).average().orElse(0.0);

                                        if (xVar > Math.max(yVar, zVar)) {
                                            selectedAxis = 0;
                                        }
                                        if (yVar > Math.max(xVar, zVar)) {
                                            selectedAxis = 1;
                                        }
                                        if (zVar > Math.max(yVar, xVar)) {
                                            selectedAxis = 2;
                                        }

                                        Intent intent = new Intent(Constants.ACTION_CALIBRATION_COMPLETE);
                                        intent.putExtra(Constants.ACTIVE_AXIS, selectedAxis);
                                        context.sendBroadcast(intent);

                                        xValues.clear();
                                        yValues.clear();
                                        zValues.clear();
                                        sumList.clear();
                                        calibrationMode = false;
                                        return;
                                    }

                                    double avc = sumList.stream().mapToDouble(val -> val).average().orElse(0.0);
                                    if (avc >= 2f) {
                                        broadcastUpdate("walk");
                                    } else if (avc < 2f) {
                                        broadcastUpdate("sit");

                                        double average = 0.0;
                                        switch (selectedAxis) {
                                            case 0:
                                                average = xValues.stream().mapToDouble(val -> val).average().orElse(0.0);
                                                break;

                                            case 1:
                                                average = yValues.stream().mapToDouble(val -> val).average().orElse(0.0);
                                                break;

                                            case 2:
                                                average = zValues.stream().mapToDouble(val -> val).average().orElse(0.0);
                                                break;
                                        }


                                        final Intent intent = new Intent(Constants.ACTION_UPDATE_ACCELEROMETER_READING);
                                        intent.putExtra(Constants.ACCELEROMETER_AVG, average);
                                        context.sendBroadcast(intent);
                                    }
                                    xValues.clear();
                                    yValues.clear();
                                    zValues.clear();
                                    sumList.clear();
                                }
                            }
                        });
                    }
                });
            }
        }).continueWith(new Continuation<Route, Void>() {
            @Override
            public Void then(Task<Route> task) throws Exception {
                if (task.isFaulted()) {
                    showToast("Accelerometer setup failed. Please restart app");
                    Log.w("ble_config", "APP CONFIG FAILED");
                } else {
                    showToast("Accelerometer Configured");
                    context.sendBroadcast(new Intent(Constants.ACTION_BLE_CONNECTED));
                    Log.i("ble_config", "APP CONFIGURED");
                }
                return null;
            }
        });
    }

    private void showToast(String message) {
        if (context != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void stopAccelerometer() {
        accelerometer.acceleration().stop();
        accelerometer.stop();

        Intent bleService = new Intent(context, BLEDataReadService.class);
        context.stopService(bleService);

        isConnected = false;
    }

    private void broadcastUpdate(String state) {
        final Intent intent = new Intent(Constants.ACTION_UPDATE_SITTING_STATE);
        intent.putExtra(Intent.EXTRA_TEXT, state);
        context.sendBroadcast(intent);
    }

    private IntentFilter getIntentFilters() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.ACTION_START_ACCELEROMETER);
        intentFilter.addAction(Constants.ACTION_STOP_ACCELEROMETER);
        intentFilter.addAction(Constants.ACTION_CALIBRATE);

        return intentFilter;
    }

    public void destroyMbient() {
        if (!isConnected) {
            board.tearDown();
            context.unbindService(this);
            context.unregisterReceiver(sensorReceiver);
            instance = null;
        }
    }
}
