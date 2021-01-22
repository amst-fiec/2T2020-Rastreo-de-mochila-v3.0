package com.example.finder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ModoLive extends AppCompatActivity  implements OnMapReadyCallback  {
    GoogleMap map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_live);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync( this);
    }



    @Override
    public void onMapReady(GoogleMap map) {
        LatLng marca= new LatLng(-2.1481404, -79.9666772);
        map.addMarker(new MarkerOptions()
                .position(marca)
                .title("Marker"));
        map.moveCamera(CameraUpdateFactory.newLatLng(marca));
    }

    public void volverMenu(View view) {
        startActivity(new Intent(getApplicationContext(), Home.class));
    }

    public void irModoEstatico(View view) {
        startActivity(new Intent(getApplicationContext(), ModoEstatico.class));
    }

    public void irSettings(View view) {
        startActivity(new Intent(getApplicationContext(), Configuraciones.class));
    }

    public void Ubicacion(View view){
        AlertaInternet();
    }

    private void AlertaInternet(){
        AlertDialog.Builder builder= new AlertDialog.Builder(ModoLive.this);
        builder.setTitle("ALERTA")
                .setMessage("No se puede establecer conexión con la nube.")
                .setIcon(R.drawable.warning)
                .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        recreate();
                    }

                })
                .setCancelable(false);

        AlertDialog dialog= builder.create();
        dialog.show();
    }
}