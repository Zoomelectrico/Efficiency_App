package co.avilatek.efficiencyapp.helpers;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class CycleTimeModel implements Parcelable {

    private int cycle;
    private String time;

    private CycleTimeModel(int c, String t) {
        this.cycle = c;
        this.time = t;
        long time = getTimeInSeconds();
        StringBuilder sb = new StringBuilder();
        int h, m, s = 0;
        h = (int) time / 3600;
        m = (int) time / 60;
        s = (int) time % 60;
        if(h < 10) {
            sb.append("0");
        }
        sb.append(h);
        sb.append(":");
        if(m < 10) {
            sb.append("0");
        }
        sb.append(m);
        sb.append(":");
        if(s < 10) {
            sb.append("0");
        }
        sb.append(s);
        this.time = sb.toString();
    }

    private CycleTimeModel(Parcel in) {
        this.cycle = in.readInt();
        this.time = in.readString();
    }

    @NonNull
    public static CycleTimeModel builder(int cycle, String time) {
        return new CycleTimeModel(cycle, time);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(cycle);
        dest.writeString(time);
    }


    @NonNull
    public static Parcelable.Creator<CycleTimeModel> CREATOR = new Creator<CycleTimeModel>() {
        @Override
        public CycleTimeModel createFromParcel(@NonNull Parcel source) {
            return new CycleTimeModel(source);
        }

        @Override
        public CycleTimeModel[] newArray(int size) {
            return new CycleTimeModel[size];
        }
    };

    public long getTimeInSeconds() {
        String[] array = this.time.split(":");
        return (Long.parseLong(array[0])*3600) + (Long.parseLong(array[1])*60) + (Long.parseLong(array[2]));
    }

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

}
