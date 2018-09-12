package co.avilatek.efficiencyapp.helpers;

import android.support.annotation.NonNull;

public class CycleTimeModel {

    private int cycle;
    private String time;

    private CycleTimeModel(int c, String t) {
        this.cycle = c;
        this.time = t;
    }

    @NonNull
    public static CycleTimeModel builder(int cycle, String time) {
        return new CycleTimeModel(cycle, time);
    }

    public static long getTimeinSeconds(CycleTimeModel ct) {
        String[] timeArray = ct.time.split(":");
        long seconds = 0L;
        if(timeArray.length == 3) {
            try {
                seconds = Long.parseLong(timeArray[0]) * 3600 + Long.parseLong(timeArray[1]) * 60 + Long.parseLong(timeArray[2]);
                return seconds;
            } catch (NumberFormatException e) {
                seconds = -1;
                return seconds;
            }
        } else if (timeArray.length == 2) {
            try {
                seconds = Long.parseLong(timeArray[0]) * 60 + Long.parseLong(timeArray[1]);
                return seconds;
            } catch (NumberFormatException e) {
                seconds = -1;
                return seconds;
            }
        } else {
            return -1;
        }
    }

}
