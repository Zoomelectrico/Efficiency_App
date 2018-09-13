package co.avilatek.efficiencyapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Objects;

import co.avilatek.efficiencyapp.dummy.DummyContent;
import co.avilatek.efficiencyapp.helpers.CycleTimeHandler;
import co.avilatek.efficiencyapp.helpers.CycleTimeModel;

public class CycleTimeActivity extends AppCompatActivity implements CycleTimeFragment.OnListFragmentInteractionListener {

    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
