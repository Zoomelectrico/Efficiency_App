package co.avilatek.efficiencyapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.Locale;
import java.util.Objects;

import co.avilatek.efficiencyapp.helpers.CycleTimeHandler;
import co.avilatek.efficiencyapp.helpers.CycleTimeModel;

public class CycleTimeActivity extends AppCompatActivity implements CycleTimeFragment.OnListFragmentInteractionListener {

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        language();
        super.onCreate(savedInstanceState);
        CycleTimeHandler handler = (CycleTimeHandler) Objects.requireNonNull(getIntent()).getParcelableExtra("handler");
        Bundle bundle = new Bundle();
        bundle.putParcelable("handler", handler);
        CycleTimeFragment fragment = new CycleTimeFragment();
        fragment.setArguments(bundle);
        setContentView(R.layout.activity_cycle_time);
        this.configBottomNav();

    }

    private void configBottomNav() {
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(itemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_history);
    }

    private void language() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Locale locale;
        if(preferences.getBoolean("translate",false)) {
            // Spanish
            locale = new Locale("es");
        } else {
            //English
            locale = new Locale("en");
        }
        Locale.setDefault(locale);
        Resources res = context.getResources();
        Configuration config = new Configuration(res.getConfiguration());
        config.setLocale(locale);
        context = context.createConfigurationContext(config);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener itemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    finish();
                    return true;
                case R.id.navigation_settings:
                    startActivity(new Intent(context, SettingsActivity.class));
                    finish();
                    return true;
                case R.id.navigation_history:
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onListFragmentInteraction(CycleTimeModel item) {

    }
}
