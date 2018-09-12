package co.avilatek.efficiencyapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import co.avilatek.efficiencyapp.helpers.FileHelper;

public class MainActivity extends AppCompatActivity {

    private double SCT;
    private double UPS;
    private int TCD = 0;

    private Chronometer clock;
    private final Handler handler = new Handler();
    private int s, m, h = 0;
    private long ms, st, tb, ut = 0L;

    private boolean undoable = true;

    private final Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(itemSelectedListener);
        checkPermissions();
        this.clock = findViewById(R.id.clock);
        this.configPlayButtons();
        this.configCCButons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getInitialData();
    }

    private void getInitialData() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            this.SCT = Double.parseDouble(preferences.getString("SCT", "0.20"));
            this.UPS = Double.parseDouble(preferences.getString("UPS", "30.0"));
        } catch (NumberFormatException e) {
            Log.e("Main", e.getMessage());
            this.SCT = 0.20;
            this.UPS = 30.0;
        }
    }

    private void configPlayButtons() {
        findViewById(R.id.btnPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                st = SystemClock.uptimeMillis();
                handler.postDelayed(thread, 0);
                clock.start();
                findViewById(R.id.btnPlay).setEnabled(false);
                addCSVRow("Start");
            }
        });
        findViewById(R.id.btnPause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tb += ms;
                handler.removeCallbacks(thread);
                clock.stop();
                addCSVRow("Pause");
            }
        });
        findViewById(R.id.btnStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ms = st = tb = ut = 0L;
                s = m = h = 0;
                handler.removeCallbacks(thread);
                clock.stop();
                clock.setText("0:00:00");
                findViewById(R.id.btnPlay).setEnabled(true);
                addCSVRow("Stop");
            }
        });
    }

    private void configLabels() {
        String er = getString(R.string.EFF) + " " + efficiencyRate() + "%";
        String cc = getString(R.string.CC) + " " + String.valueOf(TCD);
        ((TextView) findViewById(R.id.txtEFF)).setText(er);
        ((TextView) findViewById(R.id.txtCC)).setText(cc);
    }

    private void configCCButons() {
        findViewById(R.id.btnPlusOne).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TCD++;
                undoable = true;
                addCSVRow("Cycle");
                configLabels();

            }
        });
        findViewById(R.id.btnUndo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (undoable) {
                    TCD--;
                    addCSVRow("Undo Cycle");
                } else {
                    Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addCSVRow (String event) {
        StringBuilder sb = new StringBuilder();
        sb.append(new Timestamp(System.currentTimeMillis() / 1000).toString());
        sb.append(",");
        sb.append(event);
        sb.append(",");
        sb.append(SCT);
        sb.append(",");
        sb.append(UPS);
        sb.append(",");
        sb.append(TCD);
        sb.append(",");
        sb.append(m);
        sb.append(",");
        sb.append(efficiencyRate());
        FileHelper.builder(this, sb.toString()).run();
    }

    private String efficiencyRate() {
        return new DecimalFormat("#.00").format((SCT * UPS * TCD) / m);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener itemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_settings:
                    startActivity(new Intent(context, SettingsActivity.class));
                    return true;
                case R.id.navigation_history:
                    startActivity(new Intent(context, CycleTimeActivity.class));
                    return true;
            }
            return false;
        }
    };

    private Runnable thread = new Runnable() {
        @Override
        public void run() {
            ms = SystemClock.uptimeMillis() - st;
            ut = tb + ms;
            s = (int) (ut / 1000);
            m = s / 60;
            h = m / 60;
            s = s % 60;
            String string = "" + h + ":" + String.format("%02d", m) + ":" + String.format("%02d", s);
            clock.setText(string);
            handler.postDelayed(this, 0);
        }
    };


    private void checkPermissions() {
        if(!gotPermissions()) {
            askPermissions();
        }
    }

    private boolean gotPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void askPermissions() {
        boolean shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if(!shouldProvideRationale) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    34);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 34) {
            if (grantResults.length <= 0) {
                Log.i("", "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("Per", "Good");
            }
        }
    }


}
