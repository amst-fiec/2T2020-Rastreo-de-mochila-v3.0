package com.example.finder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ModoLive extends AppCompatActivity implements OnMapReadyCallback {
    GoogleMap map;
    DatabaseReference db_reference = FirebaseDatabase.getInstance().getReference();
    double latitud = 0;
    double longitud = 0;
    Marker dispo;
    Location loc;
    LocationManager locManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modo_live);

        //getSupportActionBar().hide();
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }

    @Override
    protected void onStart() {
        super.onStart();
        ActivityCompat.requestPermissions(ModoLive.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    public void onMapReady(GoogleMap googlemap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        map = googlemap;

        db_reference.child("Dispositivos").child("5").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get Post object and use the values to update the UI
                        if (dispo != null) {
                            dispo.remove();
                        }

                        latitud =  Double.parseDouble(String.valueOf(dataSnapshot.child("latitud").getValue()));
                        longitud=  Double.parseDouble(String.valueOf(dataSnapshot.child("longitud").getValue()));
                        
                        LatLng marca= new LatLng(latitud, longitud);
                        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        if (ActivityCompat.checkSelfPermission(ModoLive.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ModoLive.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        double lat2=loc.getLatitude();
                        double lng2=loc.getLongitude();
                        double radioTierra = 6371;//en kilómetros
                        double dLat = Math.toRadians(lat2 - latitud);
                        double dLng = Math.toRadians(lng2 - longitud);
                        double sindLat = Math.sin(dLat / 2);
                        double sindLng = Math.sin(dLng / 2);
                        double va1 = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                                * Math.cos(Math.toRadians(latitud)) * Math.cos(Math.toRadians(lat2));
                        double va2 = 2 * Math.atan2(Math.sqrt(va1), Math.sqrt(1 - va1));
                        double distancia = (radioTierra * va2) * 1000;


                        dispo= map.addMarker(new MarkerOptions()
                                .position(marca)
                                .title(String.format("%.2f",distancia) + " m").icon(BitmapDescriptorFactory.defaultMarker(120)));
                        // ...new MarkerOptions().position(pos)
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("aqui", "loadPost:onCancelled", databaseError.toException());
                        // ...
                    }
                }
        );


        map.setMyLocationEnabled(true);

        LatLng marca= new LatLng(-2.1481404, -79.9666772);
        map.addMarker(new MarkerOptions()
                .position(marca)
                .title("Marker"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(marca,17));

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