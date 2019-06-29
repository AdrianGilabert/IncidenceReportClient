package com.example.tfg.Activitis;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tfg.Models.BadPractise;
import com.example.tfg.Models.Incidence;
import com.example.tfg.Models.Plate;
import com.example.tfg.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.r0adkll.slidr.Slidr;

import java.util.Date;

public class AddInfo extends AppCompatActivity {
    String matricula;
    String incidenteId;
    private FirebaseFirestore firestoreDatabase;
    private EditText description;
    private LocationManager locationManager;
    private Location location;
    private String provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);
        Slidr.attach(this);
        matricula = getIntent().getExtras().getString("matricula");
        incidenteId = getIntent().getExtras().getString("incidenteId");
        provider = getIntent().getExtras().getString("provider");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        firestoreDatabase = FirebaseFirestore.getInstance();
        setTitle(R.string.add_window_title);

        description = (EditText) findViewById(R.id.description);

    }

    public void nextButton(View v) {
        final BadPractise[] practise = new BadPractise[1];

        firestoreDatabase.collection("badpractises").document(incidenteId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        practise[0] = document.toObject(BadPractise.class);
                        addIncidence(matricula,practise[0],description.getText().toString());
                    } else {
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Se ha producido un error al añadir la incidencia..", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


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

        location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            Toast.makeText(getApplicationContext(),
                    "Location:  " + location.getLatitude() + "  ,  " + location.getLongitude(), Toast.LENGTH_SHORT).show();
        }
    }

    public void updateCount(Incidence e) {
        final Plate[] plate = new Plate[1];
        final Incidence in = e;

        firestoreDatabase.collection("plates").document(e.getMatricula())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        plate[0] = document.toObject(Plate.class);
                        plate[0].setCount();
                        plate[0].setIncidence(in);
                        firestoreDatabase.collection("plates").document(plate[0].getMatricula()).set(plate[0]);
                    } else {
                        plate[0] = new Plate(in);
                        firestoreDatabase.collection("plates").document(plate[0].getMatricula()).set(plate[0]);
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Se ha producido un error al añadir la incidencia..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addIncidence(String matricula,BadPractise practise, String description) {
        Double latitud = 0.0;
        Double longitud = 0.0;
        if (location != null) {
            latitud = location.getLatitude();
            longitud = location.getLongitude();
        }
        final Incidence n = new Incidence(matricula, practise, description, new Date(), FirebaseAuth.getInstance().getCurrentUser().getUid(), latitud, longitud);

        firestoreDatabase.collection("incidences").document(n.getId()).set(n).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                goToHome();

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "Se ha producido un error, por favor vuelva a intentarlo.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void goToHome() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        Bundle bundle = new Bundle();

        this.startActivity(intent);
    }
}
