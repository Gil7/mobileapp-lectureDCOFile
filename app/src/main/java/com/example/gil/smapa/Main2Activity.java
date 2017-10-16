package com.example.gil.smapa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity {
    EditText lecture;
    TextView dataClient;
    private String currennData;
    final ArrayList<String> dataTosend = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        lecture = (EditText)findViewById(R.id.lecture);
        dataClient = (TextView)findViewById(R.id.dataClient);
        String client; //variable to get data sent from activity_main

        String[] dataToshow;//
        String finalData = "";
        int counter = 1;
        Bundle extras = getIntent().getExtras();
        if (extras == null){
            client = null;
        }else {
            client = extras.getString("client");
            System.out.println(client);
            dataToshow= client.split("\\t");
            for (String row: dataToshow){
                finalData += "Dato" + counter + ": " + row + "\n";
                dataTosend.add(row);
                counter++;
            }
        }
        dataClient.setText(finalData);
    }
    public void SendData(View v){
        Intent i = new Intent();
        String data = lecture.getText().toString();
        String helper = "";
        dataTosend.set(16,data);
        int counter = 0;
        for (String info: dataTosend){
            if (counter > 11){
                helper += info + "\t\n";
                counter = 0;
            } else {
                helper += info + "\t";
            }
            counter++;
        }

        i.putExtra("lecture", helper);
        System.out.println(helper);

        setResult(RESULT_OK, i);
        finish();
    }
}
