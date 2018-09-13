package co.avilatek.efficiencyapp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import co.avilatek.efficiencyapp.helpers.LocaleHelper;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }

}
