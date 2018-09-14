package co.avilatek.efficiencyapp.helpers;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;

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
    public void setList(ArrayList list) {
        this.list = list;
    }

    public void undoLastCycle() {
        this.list.remove(list.size() - 1);
    }

    public void addElement(int cycle, int hours, int minutes, int seconds) {
        this.list.add(CycleTimeModel.builder(cycle, String.valueOf(hours)+":"+String.valueOf(minutes)+":"+String.valueOf(seconds)));
    }


    public String bestTime() {
        if(list.size() > 0) {
            long[] times = new long[this.list.size()];
            for (int i = 0; i < times.length; i++) {
                times[i] = this.list.get(i).getTimeInSeconds();
            }
            Arrays.sort(times);
            long better = times[0];
            StringBuilder sb = new StringBuilder();
            sb.append((int) better / 3600);
            sb.append(":");
            sb.append((int) better / 60);
            sb.append(":");
            sb.append((int) better % 60);
            return sb.toString();
        } else {
            return "0:00:00";
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
