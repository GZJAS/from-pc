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

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Path;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Tab2 extends Fragment implements OnCheckedChangeListener, View.OnClickListener{

    private TextView send;
    private TextView receive;
    private SwitchCompat sendSwitch;
    private SwitchCompat receiveSwitch;
    private boolean fileSent;
    private boolean fileReceived;
    private FloatingActionButton fab;
    private SharedPreferences sharedPreferences;
    private static final int PICKFILE_RESULT_CODE = 1;
    private String filePath;
    private String fileName;
    private String name;
    String ip;
    String port;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_2, container, false);

        send = (TextView) v.findViewById(R.id.send);
        receive = (TextView) v.findViewById(R.id.receive);
        sendSwitch = (SwitchCompat) v.findViewById(R.id.sendSwitch);
        receiveSwitch = (SwitchCompat) v.findViewById(R.id.receiveSwitch);
        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        sendSwitch.setChecked(false);
        send.setText("Send");
        fileSent = false;
        receiveSwitch.setChecked(false);
        receive.setText("Receive");
        fileReceived = false;

        sendSwitch.setOnCheckedChangeListener(this);
        receiveSwitch.setOnCheckedChangeListener(this);
        loadPreferences();
        return v;
    }

    private void loadPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ip = sharedPreferences.getString("storedIpTab", "");
        port = sharedPreferences.getString("storedPortTab", "");
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        if (sendSwitch.isChecked()) {
            send.setText("Sending");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, PICKFILE_RESULT_CODE);
            new SendFile().execute();
        } else if (receiveSwitch.isChecked()) {
            receive.setText("Receiving");
            new ReceiveFile().execute();
        }
        if(!sendSwitch.isChecked())
            send.setText("Send");
        if(!receiveSwitch.isChecked())
            receive.setText("Receive");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        filePath = data.getData().getPath();
        File f = new File(filePath);
        fileName = f.getName();
    }

    public void popup(String text) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage(text);

        alertDialogBuilder.setPositiveButton("Open", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                File file = new File("/sdcard/" + fileName);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "*/*");
                startActivity(intent);
            }
        });

        alertDialogBuilder.setNegativeButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(getActivity(), MainActivity.class));
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void ShowDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        WifiManager wm = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        String add = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        alertDialogBuilder.setMessage("Your IP : " + add);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(getActivity(), ConfigTabActivity.class);
        startActivity(i);
    }


    class SendFile extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //ShowDialog();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                ServerSocket ss = new ServerSocket(Integer.parseInt(port));
                Socket s = ss.accept();
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                dos.writeUTF(fileName);

                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);

                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                oos.writeObject(buffer);
                fileSent = true;
                fis.close();
                oos.close();
                dos.flush();
                dos.close();
                s.close();
            } catch (IOException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String string) {
            if (fileSent) {
                Toast.makeText(getActivity(), "Sent", Toast.LENGTH_LONG).show();
            } else
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG).show();
        }
    }

    class ReceiveFile extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            int count;

            try {
                Socket socket = new Socket(ip, Integer.parseInt(port));

                if (socket.isConnected()) {
                    DataInputStream dis = new DataInputStream(socket.getInputStream());
                    name = dis.readUTF();

                    InputStream inputStream = new BufferedInputStream(socket.getInputStream());
                    OutputStream outputStream = new FileOutputStream("/sdcard/" + name);

                    byte buffer[] = new byte[1024];
                    while ((count = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, count);
                    }
                    fileReceived = true;
                    outputStream.close();
                    dis.close();
                    inputStream.close();
                    socket.close();
                }
            } catch (IOException e) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(String string) {
            if (fileReceived) {
                Toast.makeText(getActivity(), "Received", Toast.LENGTH_LONG).show();
                popup("File Received");
            } else
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_LONG).show();
        }
    }
}
