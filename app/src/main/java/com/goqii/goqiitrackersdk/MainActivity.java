package com.goqii.goqiitrackersdk;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.goqii.tracker.ble.BleManager;
import com.goqii.tracker.ble.TrackerCallbacks;
import com.goqii.tracker.ble.TrackerCommands;
import com.goqii.tracker.model.RealTimeData;
import com.goqii.tracker.model.SendSkipData;

public class MainActivity extends AppCompatActivity implements TrackerCallbacks {
    Button btnConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BleManager.init(getApplicationContext(), this);
        btnConnect = findViewById(R.id.btnConnect);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BleManager.getInstance().isConnected())
                    BleManager.getInstance().disconnectDevice();
                else
                    BleManager.getInstance().connectDevice("AA:BB:33:44:A3:C7");
            }
        });
    }

    @Override
    public void connectionStatus(int connectionState) {
        runOnUiThread(() -> {
            switch (connectionState) {
                case BluetoothProfile.STATE_CONNECTED:
                    btnConnect.setText("Disconnect");
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    btnConnect.setText("Connect");
                    break;
                case BluetoothProfile.STATE_CONNECTING:
                    btnConnect.setText("Connecting...");
                    break;
            }
        });
    }

    @Override
    public void dataFound(byte[] value) {
        runOnUiThread(() -> {
            switch (value[0]) {
                case TrackerCommands.SET_DATE_TIME:
                    break;
            }
        });
    }

    @Override
    public void realTimeData(SendSkipData sendSkipData) {
        if (sendSkipData instanceof RealTimeData)
            setRealTimeData(sendSkipData);
    }

    private void setRealTimeData(SendSkipData sendSkipData) {
        RealTimeData realTimeData = (RealTimeData) sendSkipData;
        Log.e("Steps:","" + realTimeData.getSteps());
        Log.e("Calories:","" + realTimeData.getCalories());
        Log.e("Distance:","" + realTimeData.getDistance());
        Log.e("Time:","" + realTimeData.getTime());
        Log.e("HeartRate:","" + realTimeData.getHeart());
        Log.e("Temperature:","" + realTimeData.getTemperature());
    }
}       