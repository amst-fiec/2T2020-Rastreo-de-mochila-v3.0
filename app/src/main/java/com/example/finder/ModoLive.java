package com.example.finder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class ModoLive extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_live);
    }

    public void Salir(View view) {
        System.exit(0);
    }

    public void volverMenu(View view) {
        System.exit(1);
    }
}