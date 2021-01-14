package com.example.finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {

    private EditText emailEt,passwordEt1,passwordEt2, telefonoEt;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private Button btn_registro;

    String email="";
    String password1="";
    String password2="";
    String telefono="";

    DatabaseReference db_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        firebaseAuth = FirebaseAuth.getInstance();
        db_reference = FirebaseDatabase.getInstance().getReference();

        emailEt=findViewById(R.id.et_correo);
        passwordEt1=findViewById(R.id.ed_password1);
        passwordEt2=findViewById(R.id.ed_password2);
        telefonoEt=findViewById(R.id.ed_celular);

        progressDialog=new ProgressDialog(this);

        btn_registro=findViewById(R.id.btn_registro);

    }

    public void Registro(View view) {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        if (null == activeNetwork || !activeNetwork.isConnected()|| !activeNetwork.isAvailable()) {
            AlertaInternet();
        }
        else{
            RegistroUser();
        }
    }

    private void RegistroUser(){
        email=emailEt.getText().toString();
        password1=passwordEt1.getText().toString();
        password2=passwordEt2.getText().toString();
        telefono=telefonoEt.getText().toString();

        if(TextUtils.isEmpty(email)){
            emailEt.setError("Campo vacío.");
            return;
        }
        else if(TextUtils.isEmpty(password1)){
            passwordEt1.setError("Campo vacío.");
            return;
        }
        else if(TextUtils.isEmpty(password2)){
            passwordEt2.setError("Campo vacío.");
            return;
        }
        else if(TextUtils.isEmpty(telefono)){
            telefonoEt.setError("Campo vacío.");
            return;
        }
        else if(!password1.equals(password2)){
            passwordEt2.setError("Las contraseñas no coinciden.");
            return;
        }
        else if(password1.length()<6){
            passwordEt1.setError("La contraseña debe ser mayor a 6 caracteres.");
            return;
        }
        else if(telefono.length()!=10){
            telefonoEt.setError("El teléfono debe tener 10 dígitos.");
            return;
        }
        else if(!isValidEmail(email)){
            emailEt.setError("Email inválido.");
            return;
        }

        progressDialog.setMessage("Cargando...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth.createUserWithEmailAndPassword(email, password1).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Map<String, String> map= new HashMap<>();
                    map.put("email",email);
                    map.put("password",password1);
                    map.put("telefono",telefono);


                    String id = firebaseAuth.getCurrentUser().getUid();
                    db_reference.child("Usuario").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()) {
                                Toast.makeText(Registro.this, "Registro Exitoso.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(Registro.this, Home.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Registro.this, "Error al registrar datos.", Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }
                else{
                    Toast.makeText(Registro.this,"Falla al registrar.",Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });

    }

    private Boolean isValidEmail(CharSequence target){
        return (!TextUtils.isEmpty(target)&& Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private void AlertaInternet(){
        AlertDialog.Builder builder= new AlertDialog.Builder(Registro.this);
        builder.setTitle("ALERTA")
                .setMessage("Error de Conexión.")
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