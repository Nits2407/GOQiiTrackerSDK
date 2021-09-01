package com.goqii.tracker.util;

import android.content.Context;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class Utils {
    public static final String REALTIME_DATA = "realtime_data";
    public static final String ROPE_FOUND = "rope_found";

    public static String byte2Hex(byte[] data) {
        if (data != null && data.length > 0) {
            StringBuilder sb = new StringBuilder(data.length);
            for (byte tmp : data) {
                sb.append(String.format("%02X ", tmp));
            }
            return sb.toString();
        }
        return "no data";
    }

    public static String checkValueLessthanTen(String value) {
        if (Integer.parseInt(value) < 10)
            return "0" + value;

        return value;
    }

    public static String getFirmwareVersion(Context mContext, byte[] byte_array) {
        String status;
        String date;
        String hex1 = "" + String.format("%02X", byte_array[1]);
        //For version 7
        if ((int) byte_array[1] >= 7 && (int) byte_array[1] <= 9) {
            status = "" + (int) byte_array[1] + "." + (int) byte_array[2] + "." + (int) byte_array[3];
            date = "20" + (int) byte_array[9] + "-" + (int) byte_array[10] + "-" + (int) byte_array[11];
        } else if ((char) byte_array[1] == '5' || (char) byte_array[1] == '6' || (char) byte_array[1] == '8') {
            status = "" + (char) byte_array[1] + "." + (char) byte_array[2] + "." + (char) byte_array[3];
            date = "20" + String.format("%02X", byte_array[9]) + "-" + String.format("%02X", byte_array[10]) + "-" + String.format("%02X", byte_array[11]);
        } else if (hex1.equalsIgnoreCase("15")) {
            status = hex1 + "." + (int) byte_array[2] + "." + (int) byte_array[3];
            date = "20" + String.format("%02X", byte_array[9]) + "-" + String.format("%02X", byte_array[10]) + "-" + String.format("%02X", byte_array[11]);
        } else if ((int) byte_array[1] < 7) {
            status = "" + (int) byte_array[1] + "." + (int) byte_array[2] + "." + (int) byte_array[3];
            date = "20" + (int) byte_array[9] + "-" + (int) byte_array[10] + "-" + (int) byte_array[11];
        } else if (byte_array[1] == '2') {
            final char status1 = (char) byte_array[1];
            final char status2 = (char) byte_array[2];
            final char status3 = (char) byte_array[3];
            final char status4 = (char) byte_array[4];
            final char status5 = (char) byte_array[5];
            status = "" + status1 + status2 + status3 + status4 + status5;
            date = "20" + (char) byte_array[9] + "-" + (char) byte_array[10] + "-" + (char) byte_array[11];
        } else if (Integer.parseInt(hex1) >= 16) {
            status = hex1 + "." + (int) byte_array[2] + "." + (int) byte_array[3];
            date = "20" + String.format("%02X", byte_array[9]) + "-" + String.format("%02X", byte_array[10]) + "-" + String.format("%02X", byte_array[11]);
        } else {
            final char status1 = (char) byte_array[1];
            final char status2 = (char) byte_array[2];
            final char status3 = (char) byte_array[3];
            status = "" + status1 + "." + status2 + "." + status3;
            date = "20" + (char) byte_array[9] + "-" + (char) byte_array[10] + "-" + (char) byte_array[11];
        }
        return status;
    }

    public static boolean isVitalBand(String version) {
        String arrayVersion;
        if (!version.isEmpty() && version.contains(".")) {
            arrayVersion = version.replaceAll("\\.", "");
            return Integer.parseInt(arrayVersion) >= 1600;
        }
        return false;
    }

    public static String[] getActivityData(Context context, byte[] value, String version) {
        if (value.length > 4) {
            String[] activityData;
            if (isTemperatureBand(version))
                activityData = new String[6];
            else
                activityData = new String[5];

            int step = 0;
            float cal = 0;
            float distance = 0;
            int time = 0;
            int heart = 0;
            float temp = 0;
            if ((version.isEmpty() || !Utils.isVitalBand(version) || value.length == 16)) {
                step = (256 * 256 * (255 & value[1]) + (256 * (255 & value[2])) + ((255 & value[3])));
                cal = (256 * 256 * (255 & value[7])) + (256 * (255 & value[8])) + (255 & value[9]);
                distance = (256 * 256 * (255 & value[10])) + (256 * (255 & value[11])) + (255 & value[12]);
            } else {
                for (int i = 1; i < 5; i++) {
                    step += getValue(value[i], i - 1);
                }
                for (int i = 5; i < 9; i++) {
                    cal += getValue(value[i], i - 5);
                }
                for (int i = 9; i < 13; i++) {
                    distance += getValue(value[i], i - 9);
                }
                for (int i = 13; i < 17; i++) {
                    time += getValue(value[i], i - 13);
                }
                heart = getValue(value[17], 0);
                if (isTemperatureBand(version)) {
                    for (int i = 18; i < 20; i++) {
                        temp += getValue(value[i], i - 18);
                    }
                }
            }
            NumberFormat numberFormat = NumberFormat.getInstance(new Locale("en", "US"));
            numberFormat.setMinimumFractionDigits(2);
            activityData[0] = String.valueOf(step);
            activityData[1] = numberFormat.format(cal / 100);
            activityData[2] = numberFormat.format(distance / 100);
            activityData[3] = String.valueOf(time);
            activityData[4] = String.valueOf(heart);
            if (isTemperatureBand(version)) {
                numberFormat.setMinimumFractionDigits(1);
                activityData[5] = numberFormat.format(celsiusTofahrenheit(temp / 10));
            }

            return activityData;
        }
        return null;
    }

    public static float celsiusTofahrenheit(float temperature) {
        NumberFormat format = NumberFormat.getInstance(new Locale("en", "US"));
        String temString = format.format(temperature);
        try {
            Number number = format.parse(temString);
            temperature = number.floatValue();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DecimalFormat decimalFormat = new DecimalFormat("##.#", new DecimalFormatSymbols(Locale.US));
        return Float.parseFloat(decimalFormat.format(temperature * 1.8 + 32));
    }

    public static int getValue(byte b, int count) {
        return (int) ((b & 0xff) * Math.pow(256, count));
    }

    public static boolean isTemperatureBand(String version) {
        String arrayVersion;
        if (!version.isEmpty() && version.contains(".")) {
            arrayVersion = version.replaceAll("\\.", "");
            return Integer.parseInt(arrayVersion) >= 2400;
        }
        return false;
    }
}
