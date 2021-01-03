package com.example.finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
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

public class Registro extends AppCompatActivity {

    private EditText emailEt,passwordEt1,passwordEt2, telefonoEt;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private Button btn_registro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        firebaseAuth = FirebaseAuth.getInstance();

        emailEt=findViewById(R.id.et_correo);
        passwordEt1=findViewById(R.id.ed_password1);
        passwordEt2=findViewById(R.id.ed_password2);
        telefonoEt=findViewById(R.id.ed_celular);

        progressDialog=new ProgressDialog(this);

        btn_registro=findViewById(R.id.btn_registro);
        btn_registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistroUser();
            }
        });

    }

    private void RegistroUser(){
        String email=emailEt.getText().toString();
        String password1=passwordEt1.getText().toString();
        String password2=passwordEt2.getText().toString();
        String telefono=telefonoEt.getText().toString();

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
                    Toast.makeText(Registro.this,"Registro Exitoso.",Toast.LENGTH_LONG).show();
                    Intent intent= new Intent(Registro.this,Home.class);
                    startActivity(intent);
                    finish();
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
}