package com.goqii.tracker.ble;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.goqii.tracker.model.RealTimeData;
import com.goqii.tracker.model.StepModel;
import com.goqii.tracker.util.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TrackerCommands {

    public static final byte SET_DATE_TIME = (byte) 0x01;
    public static final byte REAL_TIME_DATA = (byte) 0x09;
    public static final byte READ_VERSION = (byte) 0x27;
    private static String version;


    public static void setDateTime() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);

        byte[] value = new byte[16];
        value[0] = SET_DATE_TIME;
        value[1] = getTimeValue(year);
        value[2] = getTimeValue(month);
        value[3] = getTimeValue(day);
        value[4] = getTimeValue(hour);
        value[5] = getTimeValue(min);
        value[6] = getTimeValue(second);
        value[7] = (byte) weekDay;
        int timeZone = getTimeZone();
        // this is not a standard documentation
        if (timeZone > 0) {
            value[9] = (byte) (0x80 + timeZone / 256);
            value[8] = (byte) (timeZone % 256);
        } else {
            //don't remove - sign
            value[9] = (byte) (-timeZone / 256);
            value[8] = (byte) (-timeZone % 256);
        }
        BleManager.getInstance().writeValue(value);
    }


    public static void setRealTimeData(StepModel stepModel) {
        byte[] value;
        value = stepModel.isStepState() ? startGo() : stopGo();
        crcValue(value);
        BleManager.getInstance().writeValue(value);
    }

    public static void getFirmware() {
        byte[] value = new byte[16];
        value[0] = READ_VERSION;
        crcValue(value);
        BleManager.getInstance().writeValue(value);
    }

    private static byte[] startGo() {
        byte[] value = new byte[16];
        value[0] = REAL_TIME_DATA;
        value[1] = 1;
        value[2] = 1;
        crcValue(value);
        return value;
    }

    private static byte[] stopGo() {
        byte[] value = new byte[16];
        value[0] = REAL_TIME_DATA;
        value[1] = 0;
        value[2] = 0;
        crcValue(value);
        return value;
    }

    public static int getTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        Date now = new Date();
//Import part : x.0 for double number
        double offsetFromUtc = tz.getOffset(now.getTime()) / 3600000.0;
        return (int) (offsetFromUtc * 60);
    }

    public static String readResponse(Context context, byte[] value) {
        String readbleFormat = Utils.byte2Hex(value);
        switch ((byte) value[0]) {
            case SET_DATE_TIME:
                break;
            case READ_VERSION:
                version = Utils.getFirmwareVersion(context, value);
                BleManager.getInstance().setRealTimeData(true);
                break;
            case REAL_TIME_DATA:
                sendRealTimeData(context, value);
                break;
        }

        return readbleFormat;
    }

    private static void sendRealTimeData(Context context, byte[] value) {
        if (version != null) {
            RealTimeData realTimeData = new RealTimeData();
            if (Utils.isVitalBand(version)) {
                String[] activityData = Utils.getActivityData(context, value, version);
                if (activityData != null) {
                    realTimeData.setSteps(activityData[0]);
                    realTimeData.setCalories(activityData[1]);
                    realTimeData.setDistance(activityData[2]);
                    realTimeData.setTime(activityData[3]);
                    realTimeData.setHeart(activityData[4]);
                    if (Utils.isTemperatureBand(version))
                        realTimeData.setTemperature(activityData[5]);
                    BleManager.getInstance().getListener().realTimeData(realTimeData);
                }
            } else {
                int steps = (256 * 256 * (255 & value[1]) + (256 * (255 & value[2])) + ((255 & value[3])));
                int calories = (256 * 256 * (255 & value[7])) + (256 * (255 & value[8])) + (255 & value[9]);
                int distance = (256 * 256 * (255 & value[10])) + (256 * (255 & value[11])) + (255 & value[12]);

                realTimeData.setSteps(String.valueOf(steps));
                realTimeData.setCalories(String.valueOf(calories));
                realTimeData.setDistance(String.valueOf(distance));
                BleManager.getInstance().getListener().realTimeData(realTimeData);
            }
        }
    }

    public static byte getTimeValue(int value) {
        String data = value + "";
        int m = Integer.parseInt(data, 16);
        return (byte) m;
    }

    private static void crcValue(byte[] value) {
        byte crc = 0;
        for (int i = 0; i < value.length - 1; i++) {
            crc += value[i];
        }
        value[value.length - 1] = (byte) (crc & 0xff);
    }
}
