package com.example.finder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    DatabaseReference db_reference;
    FirebaseAuth mAuth;
    EditText tlf;

    public static String telefono;

    private String dispositivos[] = {" ","Dispositivo 1", "Dispositivo 2", "Dispositivo 3", "Dispositivo 4"};
    private String bateria [] = {"81%", "25%", "32%", "73%"};

    private Button btn_Modo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        Spinner Valores= findViewById(R.id.spinner);
        Valores.setOnItemSelectedListener(this);

        ArrayAdapter items= new ArrayAdapter(this, android.R.layout.simple_spinner_item,dispositivos);
        items.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Valores.setAdapter(items);

        btn_Modo= (Button)findViewById(R.id.btn_modo);

        iniciarBaseDeDatos();
        leerTelefono();

        if(telefono==null) {
            obtenerTelefono();
        }

    }

    public void iniciarBaseDeDatos(){
        db_reference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i>0) {
            Toast.makeText(this,"La batería del " + adapterView.getSelectedItem().toString() + " es " + bateria[i-1],Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void mostrarMaletas(View view) {
        startActivity(new Intent(getApplicationContext(), Maletas.class));
    }

    public void modo(View view) {
        final CharSequence[] items= {"Modo Estático","Modo Live"};
        AlertDialog.Builder alertaNotificaciones= new AlertDialog.Builder(this);
        alertaNotificaciones.setTitle(Html.fromHtml("<font color= '#00BCD4'>"+"Elija una opción:"+"</font>"));

        alertaNotificaciones.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast mensaje= Toast.makeText(getApplicationContext(),"Opción seleccionada: "+ items[i],Toast.LENGTH_LONG);
                mensaje.show();

                if (i==0){
                    startActivity(new Intent(getApplicationContext(), ModoEstatico.class));
                }
                else {
                    startActivity(new Intent(getApplicationContext(), ModoLive.class));
                }

            }
        });
        AlertDialog alertaModo = alertaNotificaciones.create();
        alertaModo.show();
    }

    public void cerrarSesion(View view){
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        Toast.makeText(this,"Sesión Finalizada.", Toast.LENGTH_LONG).show();
        intent.putExtra("msg", "cerrarSesion");
        startActivity(intent);
    }

    private void obtenerTelefono(){
        AlertDialog.Builder builder= new AlertDialog.Builder(Home.this);
        LayoutInflater inflater= this.getLayoutInflater();
        builder.setTitle("DATO NO REGISTRADO").setIcon(R.drawable.tlf);

        View dialogView= inflater.inflate(R.layout.dialog_telefono,null);
        builder.setView(dialogView);

        tlf= dialogView.findViewById(R.id.edit_tlf);

        builder.setPositiveButton("REGISTRAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ingresoTelefono(tlf.getText().toString());
                        Toast.makeText(getApplicationContext(), "Registro exitoso.", Toast.LENGTH_LONG).show();
                    }
                })
                .setCancelable(false);

        builder.show();
    }

    public void ingresoTelefono(String telefono){
        Map<String, String> nuevoDato = new HashMap<String, String>();
        nuevoDato.put("telefono", telefono);
        DatabaseReference baseDatos = db_reference.child("Usuario");
        baseDatos.child(mAuth.getCurrentUser().getUid()).child("telefono").setValue(telefono);
    }

    public void registroBaterias(View view){
        Intent intent = new Intent(this, RegistroBaterias.class);
        startActivity(intent);
    }

    public void leerTelefono(){
        db_reference.child("Usuario").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String numero = String.valueOf(dataSnapshot.child("telefono").getValue());
                telefono=numero;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.out.println(error.toException());
            }
        });

    }
}