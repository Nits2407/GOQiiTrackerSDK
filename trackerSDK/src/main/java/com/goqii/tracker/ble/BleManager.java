package com.goqii.tracker.ble;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.goqii.tracker.model.StepModel;


public class BleManager {
    private static BleManager ourInstance;
    private BluetoothManager bluetoothManager;
    private TrackerCallbacks trackerCallbacks;
    private String address;
    public boolean isConnectedInIOS;
    private TrackerBleService bleService;
    private Handler mHandler = new Handler();
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {// TODO Auto-generated method stub
            bleService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bleService = ((TrackerBleService.LocalBinder) service).getService();
            if (!TextUtils.isEmpty(address)) {
                bleService.initBluetoothDevice(address, context);
            }
        }
    };
    private Intent serviceIntent;
    BluetoothAdapter bluetoothAdapter;
    Context context;

    private BleManager(Context context, TrackerCallbacks trackerCallbacks) {
        this.context = context;
        setListener(trackerCallbacks);
        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (serviceIntent == null) {
            serviceIntent = new Intent(context, TrackerBleService.class);
            context.bindService(serviceIntent, serviceConnection,
                    Service.BIND_AUTO_CREATE);
        }
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

    }

    public void setListener(TrackerCallbacks listener) {
        trackerCallbacks = listener;
    }

    public TrackerCallbacks getListener() {
       return trackerCallbacks;
    }

    public static void init(Context context, TrackerCallbacks trackerCallbacks) {
        if (ourInstance == null) {
            synchronized (BleManager.class) {
                if (ourInstance == null) {
                    ourInstance = new BleManager(context, trackerCallbacks);
                }
            }
        }
    }

    public static void init(Context context) {
        if (ourInstance == null) {
            synchronized (BleManager.class) {
                if (ourInstance == null) {
                    ourInstance = new BleManager(context, null);
                }
            }
        }
    }

    public boolean isBleEnable() {
        return bluetoothAdapter.enable();
    }

    public static BleManager getInstance() {
        return ourInstance;
    }

    public void connectDevice(String address) {
        if (isLeAvailable()) {
            if (!bluetoothAdapter.isEnabled() || TextUtils.isEmpty(address) || isConnected())
                return;

            if (bleService == null) {
                this.address = address;
            } else {
                bleService.initBluetoothDevice(address, this.context);
            }
        }
    }

    public boolean isLeAvailable() {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }


    public void enableNotifaction() {
        if (bleService == null) return;
        bleService.setCharacteristicNotification();
    }

    public void writeValue(byte[] value) {
        if (bleService == null || ourInstance == null || !isConnected()) return;
        bleService.writeValue(value);
    }

    public void disconnectDevice() {
        if (bleService == null) return;
        bleService.disconnect();
    }

    public void setRealTimeData(boolean isStart) {
        StepModel stepModel = new StepModel();
        stepModel.setStepState(isStart);
        TrackerCommands.setRealTimeData(stepModel);
    }
    public boolean isConnected() {
        if (bleService == null) return false;
        return bleService.isConnected();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private
    BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.e("skipNAme", device.getName() + "");
            if (!TextUtils.isEmpty(device.getName()) && device.getName().toLowerCase().equalsIgnoreCase("goqii skip")) {
                scanLeDevice(false);
                //skippingRopeCallbacks.skipRopeFound(device);
            }
        }
    };

    public void stopBLEService() {
        try {
            //stopConnectTimer();
            Intent service = new Intent(context, TrackerBleService.class);
            context.stopService(service);
            CleanUpNow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CleanUpNow() {
        if (bleService != null) {
            bleService.cleanUp();
            //isServiceConnected = false;
        }
    }

    public void startScan() {
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        scanLeDevice(true);
    }

    private void scanLeDevice(boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //    skippingRopeCallbacks.scanFinished();
                    bluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, 10000);
            bluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            bluetoothAdapter.stopLeScan(mLeScanCallback);
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    public void getFirmware() {
        TrackerCommands.getFirmware();
    }
}
