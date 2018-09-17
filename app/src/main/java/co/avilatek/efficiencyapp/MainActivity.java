package co.avilatek.efficiencyapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Semaphore;

import co.avilatek.efficiencyapp.helpers.CycleTimeHandler;
import co.avilatek.efficiencyapp.helpers.CycleTimeModel;
import co.avilatek.efficiencyapp.helpers.FileCyleHelper;
import co.avilatek.efficiencyapp.helpers.FileHelper;
import co.avilatek.efficiencyapp.helpers.LocaleHelper;

public class MainActivity extends AppCompatActivity {

    private double SCT;
    private double UPS;
    private int TCD = 0;
    private final Handler handler = new Handler();
    private int s, m, h = 0;
    private int sE, mE, hE = 0;
    private long ms, st, tb, ut = 0L;
    private boolean undoable = true;
    @NonNull
    private Context context = this;
    private final CycleTimeHandler cycleTimeHandler = CycleTimeHandler.builder();
    @Nullable
    private String locale;
    private boolean isTicking = false;
    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.checkPermissions();
        this.configPlayButtons();
        this.configCCButtons();
        this.configLabels();
        locale = PreferenceManager.getDefaultSharedPreferences(this).getString("translateCode","en");
        ((TextView) findViewById(R.id.txtCT)).setText(getString(R.string.CT) + "\n0:00:00");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getInitialData();
        translate();
        configBottomNav();
    }

    @Override
    protected void attachBaseContext(@NonNull Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @Override
    protected final void onRestoreInstanceState(@Nullable final Bundle inState) {
        super.onRestoreInstanceState(inState);
        if(inState != null) {
            this.tb = inState.getLong("tb");
            this.TCD = inState.getInt("TCD");
            cycleTimeHandler.setList(inState.getParcelableArrayList("list"));
            if(inState.getBoolean("tick")) {
                startClock();
                findViewById(R.id.btnPlay).setVisibility(View.GONE);
                findViewById(R.id.btnPlusOne).setVisibility(View.VISIBLE);
                findViewById(R.id.btnPause).setVisibility(View.VISIBLE);
            }
            configLabels();
        }
    }

    @Override
    protected final void onSaveInstanceState(@Nullable final Bundle outState) {
        super.onSaveInstanceState(outState);
        if(outState != null) {
            outState.putLong("tb", tb);
            outState.putBoolean("tick", isTicking);
            outState.putInt("TCD", TCD);
            outState.putParcelableArrayList("list", cycleTimeHandler.getList());
        }
    }

    private void translate() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        LocaleHelper.setLocale(this, preferences.getString("translateCode", "en"));
        if(!preferences.getString("translateCode", "en").equals(locale)) {
            recreate();
        }

    }

    private void configBottomNav() {
        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(itemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);
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

    private void startClock() {
        st = SystemClock.uptimeMillis();
        handler.postDelayed(thread, 0);
        isTicking = true;
    }

    private void pauseClock() {
        tb += ms;
        handler.removeCallbacks(thread);
        isTicking = false;
    }

    private void stopClock() {
        ms = st = tb = ut = 0L;
        s = m = h = 0;
        handler.removeCallbacks(thread);
        ((TextView) findViewById(R.id.txtCT)).setText(getString(R.string.CT) + "\n0:00:00");
    }

    private void configPlayButtons() {
        findViewById(R.id.btnPlay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startClock();
                addFullDataRow("Start");
                findViewById(R.id.btnPlay).setVisibility(View.GONE);
                findViewById(R.id.btnPlusOne).setVisibility(View.VISIBLE);
                findViewById(R.id.btnPause).setVisibility(View.VISIBLE);
            }
        });
        findViewById(R.id.btnPause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseClock();
                addFullDataRow("Pause");
                findViewById(R.id.btnPlay).setVisibility(View.VISIBLE);
                findViewById(R.id.btnPlusOne).setVisibility(View.GONE);
                findViewById(R.id.btnPause).setVisibility(View.GONE);
            }
        });
        findViewById(R.id.btnStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopClock();
                addFullDataRow("Stop");
                findViewById(R.id.btnPlay).setVisibility(View.VISIBLE);
                findViewById(R.id.btnPause).setVisibility(View.GONE);
                findViewById(R.id.btnPlusOne).setVisibility(View.GONE);
                sE = mE = hE = 0;
                TCD = 0;
                cycleTimeHandler.getList().clear();
                configLabels();
            }
        });
    }

    private void configLabels() {
        String er = getString(R.string.EFF) + " " + efficiencyRate() + "%";
        String cc = getString(R.string.CC) + " " + String.valueOf(TCD + 1);
        String bct = getString(R.string.BCT) + "\n" + cycleTimeHandler.bestTime();
        String lct;
        if(cycleTimeHandler.getList().isEmpty()) {
            lct = getString(R.string.LCT) + "\n" +"0:00:00";
        } else {
            lct = getString(R.string.LCT) + "\n" + cycleTimeHandler.getList().get(cycleTimeHandler.getList().size() - 1).getTime();
        }
        ((TextView) findViewById(R.id.txtEFF)).setText(er);
        ((TextView) findViewById(R.id.txtCC)).setText(cc);
        ((TextView) findViewById(R.id.txtLCT)).setText(lct);
        ((TextView) findViewById(R.id.txtBCT)).setText(bct);
    }

    private void configCCButtons() {
        findViewById(R.id.btnPlusOne).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TCD++;
                undoable = true;
                addFullDataRow("Cycle");
                addCycleDataRow();
                cycleTimeHandler.addElement(TCD, h, m, s);
                sE = s; mE = m; hE = h;
                findViewById(R.id.btnPlay).setVisibility(View.GONE);
                findViewById(R.id.btnPause).setVisibility(View.VISIBLE);
                stopClock();
                configLabels();
                startClock();
            }
        });
        findViewById(R.id.btnUndo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (undoable) {
                    if(TCD > 0) {
                        if(cycleTimeHandler.undoLastCycle()) {
                            undoable = false;
                            TCD--;
                            addFullDataRow("Undo Cycle");
                            removeCycleData();
                            stopClock();
                            configLabels();
                            startClock();
                        } else {
                            Toast.makeText(context, getString(R.string.error1), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, getString(R.string.error1), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, getString(R.string.error2), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addFullDataRow(String event) {
        StringBuilder sb = new StringBuilder();
        sb.append(new Timestamp(Calendar.getInstance().getTimeInMillis() / 1000).toString());
        sb.append(",");
        sb.append(event);
        sb.append(",");
        sb.append(SCT);
        sb.append(",");
        sb.append(UPS);
        sb.append(",");
        sb.append(TCD);
        sb.append(",");
        sb.append((h*60) + m + (s/60));
        sb.append(",");
        sb.append(efficiencyRate());
        sb.append("%");
        sb.append("\n");
        FileHelper.builder(this, sb.toString()).run();

    }

    private void addCycleDataRow() {
        String string = String.valueOf(TCD) + "," + String.valueOf(h) + ":" + String.valueOf(m) + ":" + String.valueOf(s) + "\n";
        FileCyleHelper.builder(string, this).run();
    }

    private void removeCycleData() {
        // TODO: Implementar
    }

    private String efficiencyRate() {
        if (sE > 0) {
            double totalMinutes = (hE * 60) + mE + ((sE * 1.0) / 60);
            Log.e("minutes", String.valueOf(sE / 60));
            Log.e("minutes", String.valueOf(totalMinutes));
            double eff = ((SCT * UPS) / (totalMinutes)) * 100;
            Log.e("Eff", String.valueOf(eff));
            return new DecimalFormat("#.00").format(eff);
        } else {
            return "100";
        }
    }

    @NonNull
    private BottomNavigationView.OnNavigationItemSelectedListener itemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_settings:
                    navigation.setSelectedItemId(R.id.navigation_home);
                    new PasswordDialog().show(getSupportFragmentManager(), "Algo");
                    return true;
                case R.id.navigation_history:
                    Intent intent = new Intent(context, CycleTimeActivity.class);
                    intent.putExtra("handler", cycleTimeHandler);
                    intent.putExtra("TCD", TCD);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    @NonNull
    private Runnable thread = new Runnable() {
        @Override
        public void run() {
            ms = SystemClock.uptimeMillis() - st;
            ut = tb + ms;
            s = (int) (ut / 1000);
            m = s / 60;
            h = m / 60;
            s = s % 60;
            String string = getString(R.string.CT) + "\n" + h + ":" + String.format("%02d", m) + ":" + String.format("%02d", s);
            ((TextView) findViewById(R.id.txtCT)).setText(string);
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
