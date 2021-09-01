package com.goqii.tracker.ble;

import com.goqii.tracker.model.SendSkipData;

public interface TrackerCallbacks {
    void connectionStatus(int newState);
    void dataFound(byte[] value);
    void realTimeData(SendSkipData sendSkipData);
}
