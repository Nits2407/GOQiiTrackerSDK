package com.goqii.tracker.model;

/**
 * Created by Administrator on 2018/4/9.
 */

public class StepModel{
    private boolean stepState = true;//true开启实时计步，false停止实时计步

    public boolean isStepState() {
        return stepState;
    }

    public void setStepState(boolean stepState) {
        this.stepState = stepState;
    }

    public boolean isReallTimeHeartRate() {
        return isReallTimeHeartRate;
    }

    public void setReallTimeHeartRate(boolean reallTimeHeartRate) {
        isReallTimeHeartRate = reallTimeHeartRate;
    }

    private boolean  isReallTimeHeartRate=false;
}
