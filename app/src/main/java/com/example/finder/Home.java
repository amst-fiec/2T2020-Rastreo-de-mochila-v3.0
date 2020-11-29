package com.example.finder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Home extends AppCompatActivity {

    private TextView tv1;
    private ListView lv1;

    private String dispositivos[] = {"Dispositivo 1", "Dispositivo 2", "Dispositivo 3", "Dispositivo 4"};
    private String bateria [] = {"81%", "25%", "32%", "73%"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tv1 = (TextView)findViewById(R.id.txt_texto2);
        lv1 = (ListView)findViewById(R.id.lv_disp);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_bateria, dispositivos);
        lv1.setAdapter(adapter);

        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tv1.setText("Porcentaje de batería del "+  lv1.getItemAtPosition(i)+ " es " + bateria[i] );
            }
        });
    }

    public void mostrarMaletas(View view) {
        startActivity(new Intent(getApplicationContext(), Maletas.class));
    }

    public void Salir(View view) {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

}