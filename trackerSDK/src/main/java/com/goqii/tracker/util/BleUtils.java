package com.goqii.tracker.util;

import android.content.Context;
import android.content.Intent;

import com.goqii.tracker.model.BleOperationModel;
import com.goqii.tracker.model.SendSkipData;

public class BleUtils {

    public static void sendBleOperation(String operation) {
        Intent intent = new Intent();
        intent.setAction("bleOperation");
        intent.putExtra("bleTask", operation);
        BleOperationModel bleOperationModel = new BleOperationModel();
        bleOperationModel.setIntent(intent);
        RxBus.getInstance().post(bleOperationModel);

    }

    public static void sendBleLinkingOperation(String bleMac) {
        Intent intent = new Intent();
        intent.setAction("bleOperation");
        intent.putExtra("bleTask", Utils.ROPE_FOUND);
        intent.putExtra("mac", bleMac);
        BleOperationModel bleOperationModel = new BleOperationModel();
        bleOperationModel.setIntent(intent);
        RxBus.getInstance().post(bleOperationModel);

    }

    public static void sendBleSkipData(String operation,int cmd, SendSkipData sendSkipData) {
        Intent intent = new Intent();
        intent.setAction("bleOperation");
        intent.putExtra("skipCmd", cmd);
        intent.putExtra("bleTask", operation);
        if (sendSkipData != null)
            intent.putExtra("bleValue", sendSkipData);
        BleOperationModel bleOperationModel = new BleOperationModel();
        bleOperationModel.setIntent(intent);
        RxBus.getInstance().post(bleOperationModel);

    }
}
