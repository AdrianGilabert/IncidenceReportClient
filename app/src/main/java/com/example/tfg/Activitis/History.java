package com.example.tfg.Activitis;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.tfg.Adapters.IncidenceAdapter;
import com.example.tfg.Models.Incidence;
import com.example.tfg.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.Collections;

public class History extends AppCompatActivity {
    private ListView lista;
    ArrayList<Incidence> incidences;
    private FirebaseFirestore firestoreDatabase;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Slidr.attach(this);
        firestoreDatabase = FirebaseFirestore.getInstance();
        incidences = new ArrayList<>();
        lista = (ListView) findViewById(R.id.incidence_list);
        setTitle("Historial");
        progress=(ProgressBar)findViewById(R.id.progressbarh) ;

        firestoreDatabase.collection("incidences")
                .whereEqualTo("user", FirebaseAuth.getInstance()
                        .getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                incidences.add(document.toObject(Incidence.class));

                            }
                            updateListView();
                        }
                    }
                });
    }

    private void updateListView() {
        progress.setVisibility(View.INVISIBLE);
        Collections.sort(incidences);
        lista.setAdapter(new IncidenceAdapter(this, R.layout.incidence_element, incidences));
    }

}
