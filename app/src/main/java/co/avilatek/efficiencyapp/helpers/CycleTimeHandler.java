package co.avilatek.efficiencyapp.helpers;

import java.util.ArrayList;

public class CycleTimeHandler {

    private ArrayList<CycleTimeModel> list;

    private CycleTimeHandler() {
        list = new ArrayList<>();
    }

    public static CycleTimeHandler builder() {
        return new CycleTimeHandler();
    }

}
