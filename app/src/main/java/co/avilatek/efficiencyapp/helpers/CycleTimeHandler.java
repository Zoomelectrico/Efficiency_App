package co.avilatek.efficiencyapp.helpers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class CycleTimeHandler implements Parcelable {

    private ArrayList<CycleTimeModel> list;

    private CycleTimeHandler() {
        list = new ArrayList<>();
    }

    private CycleTimeHandler(Parcel in){
        this.list = new ArrayList<>();
        in.readTypedList(list, CycleTimeModel.CREATOR);
    }

    public static CycleTimeHandler builder() {
        return new CycleTimeHandler();
    }

    public ArrayList<CycleTimeModel> getList() {
        return list;
    }

    public void undoLastCycle() {
        this.list.remove(list.size() - 1);
    }

    public void addElement(int cycle, int hours, int minutes, int seconds) {
        if(list.isEmpty()) {
            String time = String.valueOf(hours) + ":" + String.valueOf(minutes) + ":" + String.valueOf(seconds);
            list.add(CycleTimeModel.builder(cycle, time, hours*3600 + minutes*60 + seconds));
        } else {
            CycleTimeModel ct = list.get(list.size()-1);
            long timeInSeconds = (hours*3600 + minutes*60 + seconds) - ct.getDuration();
            String time = String.valueOf((int) timeInSeconds / 3600) + ":" + String.valueOf((int) minutes/60) + ":" + String.valueOf(seconds%60);
            list.add(CycleTimeModel.builder(cycle, time, (hours*3600 + minutes*60 + seconds)));
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.list);
    }

    public static Parcelable.Creator<CycleTimeHandler> CREATOR = new Creator<CycleTimeHandler>() {
        @Override
        public CycleTimeHandler createFromParcel(Parcel source) {
            return new CycleTimeHandler(source);
        }

        @Override
        public CycleTimeHandler[] newArray(int size) {
            return new CycleTimeHandler[size];
        }
    };
}
