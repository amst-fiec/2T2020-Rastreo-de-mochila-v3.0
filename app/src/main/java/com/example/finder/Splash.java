package com.example.finder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Splash extends AppCompatActivity {

    private final int DURACION_SPLASH = 3000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        // se presenta el splash por 3 segundos

        new Handler().postDelayed(new Runnable(){
            public void run(){

                DatabaseReference db_reference = FirebaseDatabase.getInstance().getReference();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {

                    HashMap<String, String> map = new HashMap<>();
                    map.put("name", user.getDisplayName());
                    map.put("email", user.getEmail());
                    map.put("telefono",Home.telefono);

                    String id = mAuth.getCurrentUser().getUid();

                    db_reference.child("Usuario").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()) {
                               
                                Intent intent =new Intent(Splash.this,Home.class);
                                intent.putExtra("map",map);
                                startActivity(intent);

                                finish();
                            } else {
                                Toast.makeText(Splash.this, "No se pudieron crear los datos correctamente.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                } else {
                    Intent intent = new Intent(Splash.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }









            };
        }, DURACION_SPLASH);
    }
}