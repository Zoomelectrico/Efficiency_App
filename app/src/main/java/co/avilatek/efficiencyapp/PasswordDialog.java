package co.avilatek.efficiencyapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

public class PasswordDialog extends DialogFragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final Context context = Objects.requireNonNull(getContext());
        final View view = inflater.inflate(R.layout.fragment_password_dialog, container, false);
        ((Button) view.findViewById(R.id.btnPassword)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = ((EditText) view.findViewById(R.id.password)).getText().toString();
                String pw = PreferenceManager.getDefaultSharedPreferences(context).getString("password","1234");
                Log.e("ad",pass);
                Log.e("ad",pw);
                if(pw.equals("") || pass.equals("")) {
                    Toast.makeText(context, "Aja1", Toast.LENGTH_SHORT).show();
                } else if (pw.equals(pass)) {
                    Intent intent = new Intent(context, SettingsActivity.class);
                    getDialog().dismiss();
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "aja2", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}