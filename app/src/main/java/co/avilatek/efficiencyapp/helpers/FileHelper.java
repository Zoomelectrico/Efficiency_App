package co.avilatek.efficiencyapp.helpers;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileHelper extends Thread {

    private final String FILE_NAME = "Efficiency_App_Data.csv";
    private String csvRow;
    private Context context;

    private FileHelper(Context context, String csvRow){
        this.context = context;
        this.csvRow = csvRow;
    }

    @NonNull
    public static FileHelper builder(Context context, String csvRow) {
        return new FileHelper(context, csvRow);
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

    private void writeCSVRow() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Efficiency_App");
            if(folder.exists()) {
                try {
                    FileWriter writer = new FileWriter(folder.toString() + "/" + FILE_NAME);
                    writer.append(csvRow);
                    writer.close();
                    this.writeBackup();
                } catch (IOException e) {
                    Log.e("File", e.getMessage());
                }
            } else {
               if(folder.mkdir()) {
                   try {
                       FileWriter writer = new FileWriter(folder.toString() + "/" + FILE_NAME);
                       String s = "Timestamp,Event,Standard cycle time,Units per cycle,Total cycles done,Current worked minutes,Efficiency";
                       writer.append(s);
                       writer.append(csvRow);
                       writer.close();
                       this.writeBackup();
                   } catch (IOException e) {
                       Log.e("File", e.getMessage());
                   }
               } else {
                   Log.e("File", "MKDIR FAIL");
               }
            }
        }
    }

    @Override
    public void run() {
        this.writeCSVRow();
    }

}
