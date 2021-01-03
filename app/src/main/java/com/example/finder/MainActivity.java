package com.example.finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
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
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        EditT_correo= (EditText) findViewById(R.id.Edit_txtCorreo);
        EditT_contrasena= (EditText) findViewById(R.id.Edit_txtContrasena);
        btn_login= findViewById(R.id.btn_iniciarSesion);
        progressDialog=new ProgressDialog(this);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_user();
            }
        });


    }

    public void iniciarSesion(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
    }

/*
    public void Login(View view){
        email =EditT_correo.getText().toString();
        password =EditT_contrasena.getText().toString();

        if(!email.isEmpty() && !password.isEmpty()){
            login_user();
        }else{
            Toast.makeText(getApplicationContext(),"Complete todos los campos.",Toast.LENGTH_LONG).show();
        }

    }

 */

    private void login_user() {

        /*
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(MainActivity.this,Home.class));
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Compruebe los datos, error en inicio de sesión.",Toast.LENGTH_LONG).show();
                }
            }
        });

         */

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
                    Toast.makeText(MainActivity.this,"Falla al iniciar sesión.",Toast.LENGTH_LONG).show();
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

    public void iniciarBaseDeDatos(){
        db_reference = FirebaseDatabase.getInstance().getReference().child("Usuario");
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            HashMap<String, String> info_user = new HashMap<String, String>();
            info_user.put("user_name", user.getDisplayName());
            info_user.put("user_email", user.getEmail());
            info_user.put("user_photo", String.valueOf(user.getPhotoUrl()));
            info_user.put("user_id", user.getUid());
            finish();
            Intent intent = new Intent(this, Home.class);
            intent.putExtra("info_user", info_user);
            startActivity(intent);

        } else {
            System.out.println("Sin registro.");
        }
    }

    public void registro (View view){
        startActivity(new Intent(MainActivity.this,Registro.class));
        finish();
    }

}