package co.avilatek.efficiencyapp.helpers;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class CycleTimeModel implements Parcelable {

    private int cycle;
    private String time;
    private long duration;

    private CycleTimeModel(int c, String t, long d) {
        this.cycle = c;
        this.time = t;
        this.duration = d;
    }

    private CycleTimeModel(Parcel in) {
        this.cycle = in.readInt();
        this.time = in.readString();
        this.duration = in.readLong();
    }

    @NonNull
    public static CycleTimeModel builder(int cycle, String time, long currentTime) {
        return new CycleTimeModel(cycle, time, currentTime);
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cycle);
        dest.writeString(time);
        dest.writeLong(duration);
    }


    public static Parcelable.Creator<CycleTimeModel> CREATOR = new Creator<CycleTimeModel>() {
        @Override
        public CycleTimeModel createFromParcel(Parcel source) {
            return new CycleTimeModel(source);
        }

        @Override
        public CycleTimeModel[] newArray(int size) {
            return new CycleTimeModel[size];
        }
    };

    public int getCycle() {
        return cycle;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
