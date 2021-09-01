package com.goqii.tracker.model;

import java.io.Serializable;

public class SkipDayData extends SendSkipData implements Serializable {
    String logDate,logDateTime,status;
    int localActivityId,dailySkipActivityId,totalSkip,activityTime,totalCalories,totalSpeed;

    public int getLocalActivityId() {
        return localActivityId;
    }

    public void setLocalActivityId(int localActivityId) {
        this.localActivityId = localActivityId;
    }

    public int getDailySkipActivityId() {
        return dailySkipActivityId;
    }

    public void setDailySkipActivityId(int dailySkipActivityId) {
        this.dailySkipActivityId = dailySkipActivityId;
    }

    public String getLogDate() {
        return logDate;
    }

    public void setLogDate(String logDate) {
        this.logDate = logDate;
    }

    public String getLogDateTime() {
        return logDateTime;
    }

    public void setLogDateTime(String logDateTime) {
        this.logDateTime = logDateTime;
    }

    public int getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(int activityTime) {
        this.activityTime = activityTime;
    }

    public int getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(int totalCalories) {
        this.totalCalories = totalCalories;
    }

    public int getTotalSpeed() {
        return totalSpeed;
    }

    public void setTotalSpeed(int totalSpeed) {
        this.totalSpeed = totalSpeed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalSkip() {
        return totalSkip;
    }

    public void setTotalSkip(int totalSkip) {
        this.totalSkip = totalSkip;
    }
}
