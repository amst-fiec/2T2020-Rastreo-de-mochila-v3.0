package com.example.finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    //Varibales públicas
    static final int GOOGLE_SIGN_IN = 123;
    FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private Button btn_login;
    private ProgressDialog progressDialog;
    DatabaseReference db_reference;

    private EditText EditT_correo,EditT_contrasena;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        db_reference = FirebaseDatabase.getInstance().getReference();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent intent = getIntent();
        String msg = intent.getStringExtra("msg");
        if(msg != null){
            if(msg.equals("cerrarSesion")){
                cerrarSesion();
            }
        }

        EditT_correo= (EditText) findViewById(R.id.Edit_txtCorreo);
        EditT_contrasena= (EditText) findViewById(R.id.Edit_txtContrasena);
        btn_login= findViewById(R.id.btn_iniciarSesion);
        progressDialog=new ProgressDialog(this);
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            updateUI(firebaseUser);
        }

    }

    public void Login(View view) {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        if (null == activeNetwork || !activeNetwork.isConnected()|| !activeNetwork.isAvailable()) {
            AlertaInternet();
        }
        else{
            login_user();
        }
    }


    public void iniciarSesion(View view) {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

        if (null == activeNetwork || !activeNetwork.isConnected()|| !activeNetwork.isAvailable()) {
            AlertaInternet();
        }
        else{
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
        }
    }

    private void cerrarSesion() {
        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> updateUI(null));
    }

    private void login_user() {

        String email=EditT_correo.getText().toString();
        String password=EditT_contrasena.getText().toString();

        if(TextUtils.isEmpty(email)){
            EditT_correo.setError("Campo vacío.");
            return;
        }
        else if(TextUtils.isEmpty(password)){
            EditT_contrasena.setError("Campo vacío.");
            return;
        }

        progressDialog.setMessage("Validando...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Ingreso Exitoso.",Toast.LENGTH_LONG).show();
                    Intent intent= new Intent(MainActivity.this,Home.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(MainActivity.this,"Usuario no registrado.",Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("TAG", "Fallo el inicio de sesión con google.", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(),
                null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        System.out.println("error");
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {

            HashMap<String, String> map = new HashMap<>();
            map.put("name", user.getDisplayName());
            map.put("email", user.getEmail());

            String id = mAuth.getCurrentUser().getUid();

            db_reference.child("Usuario").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task2) {
                    if (task2.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Ingreso exitoso", Toast.LENGTH_SHORT).show();
                        Intent intent =new Intent(MainActivity.this,Home.class);
                        intent.putExtra("map",map);
                        startActivity(intent);

                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "No se pudieron crear los datos correctamente.", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        } else {
            System.out.println("Sin registro.");
        }
    }

    public void registro (View view){
        startActivity(new Intent(MainActivity.this,Registro.class));
        finish();
    }

    private void AlertaInternet(){
        AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("ALERTA")
                .setMessage("Error de Conexion.")
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