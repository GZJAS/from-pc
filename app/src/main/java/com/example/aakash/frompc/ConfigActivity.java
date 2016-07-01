/*FromPC is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Foobar is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FromPC.  If not, see <http://www.gnu.org/licenses/>.*/

package com.example.aakash.frompc;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class ConfigActivity extends ActionBarActivity implements OnClickListener {

    public String Ip;
    public String Port;
    private CheckBox checkBox;
    private EditText ip;
    private EditText port;
    private Button buttonConfig;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        checkBox = (CheckBox) findViewById(R.id.checkbox);
        ip = (EditText) findViewById(R.id.IpText);
        port = (EditText) findViewById(R.id.PortText);
        buttonConfig = (Button) findViewById(R.id.buttonConfig);
        buttonConfig.setOnClickListener(this);
        loadSavedPreferences();
    }

    private void loadSavedPreferences() {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean checkBoxValue = sharedPreferences.getBoolean("CheckBox_value", false);
        Ip = sharedPreferences.getString("storedIp", "");
        Port = sharedPreferences.getString("storedPort", "");

        if (checkBoxValue)
            checkBox.setChecked(true);
        else
            checkBox.setChecked(false);

        ip.setText(Ip);
        port.setText(Port);
    }


    private void savePreferences(String key, boolean value) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private void savePreferences(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    @Override
    public void onClick(View v) {
        savePreferences("CheckBox_value", checkBox.isChecked());
        if (checkBox.isChecked()) {
            savePreferences("storedIp", ip.getText().toString());
            savePreferences("storedPort", port.getText().toString());
            Toast.makeText(this, "Configured", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

}
