package co.avilatek.efficiencyapp.helpers;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class FileCyleHelper extends Thread {

    private final String FILE_NAME = "CycleData.csv";
    private String csvRow;
    private Context context;

    private FileCyleHelper(String r, Context c){
        this.csvRow = r;
        this.context = c;
    }

    @NonNull
    public static FileCyleHelper builder(String csvRow, Context context) {
        return new FileCyleHelper(csvRow, context);
    }

    private void writeBackup() {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_APPEND);
            fileOutputStream.write(csvRow.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            Log.e("FILe", e.getMessage());
        }
    }

    private void appendRow(File file) {
        try {
            FileWriter writer = new FileWriter(file.toString(), true);
            writer.append(csvRow);
            writer.close();
            writeBackup();
        } catch (IOException e) {
            Log.e("File", e.getMessage());
        }
    }

    private void writeCSVRow() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Efficiency_App");
            if(!folder.exists()) {
                if (!folder.mkdir()){
                    Log.e("","Aja");
                }
            }
            File file = new File(folder, FILE_NAME);
            appendRow(file);
        }
    }

    @Override
    public void run() {
        this.writeCSVRow();
    }

}
