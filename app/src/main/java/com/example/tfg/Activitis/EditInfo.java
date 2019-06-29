package com.example.tfg.Activitis;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tfg.Adapters.BadPractiseSpinnerAdapter;
import com.example.tfg.Models.BadPractise;
import com.example.tfg.Models.Incidence;
import com.example.tfg.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.r0adkll.slidr.Slidr;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class EditInfo extends AppCompatActivity {
    String incidenteId;
    Spinner spinner;
    private FirebaseFirestore firestoreDatabase;
    ArrayList<BadPractise> badPractises;
    Incidence incidence;
    EditText matricula;
    EditText description;
    Button date;
    FloatingActionButton save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        setTitle("Editar Incidencia");
        incidenteId = getIntent().getExtras().getString("incidenteId");
        spinner = (Spinner) this.findViewById(R.id.spinner);
        matricula = (EditText) this.findViewById(R.id.edit_matricula);
        description = (EditText) this.findViewById(R.id.edit_description);
        date = (Button) this.findViewById(R.id.date_button);
        Slidr.attach(this);
        firestoreDatabase = FirebaseFirestore.getInstance();
        badPractises = new ArrayList<>();
        firestoreDatabase.collection("badpractises")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                badPractises.add(document.toObject(BadPractise.class));

                            }
                            updateView();
                        }
                    }
                });
        matricula.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        matricula.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString().replaceAll("\\s+", "");
                //caso de 0 digitos
                if (text.length() == 0) matricula.setError("Introduce la matricula");
                    //matriculo de 6 digitos
                else if (text.length() == 6) {

                    //caso 1 letra + 4 digitos + 1 letra
                    if (!tieneNumeros(text.toString().substring(0, 1)) && !tieneLetras(text.toString().substring(1, 5)) && !tieneNumeros(text.toString().substring(5, 6))) {
                        matricula.setError(null);

                    }
                    else matricula.setError("El formato de la matricula no es correcto.");
                }
                //matricula de 7 digitos
                else  if (text.length() == 7) {
                    //caso  1 letra + 6 digitos
                    if (!tieneNumeros(text.toString().substring(0, 1)) && !tieneLetras(text.toString().substring(1, 7))) {
                        matricula.setError(null);
                    }
                    //caso 4 digitos + 3 letras
                    else if (!tieneLetras(text.toString().substring(0, 4)) && !tieneNumeros(text.toString().substring(4, 7))) {
                        matricula.setError(null);
                    }
                    // caso 2 letras +4 digitos + 1 letra
                    else if (!tieneNumeros(text.toString().substring(0, 2)) && !tieneLetras(text.toString().substring(2, 6)) && !tieneNumeros(text.toString().substring(6, 7))) {
                        matricula.setError(null);
                    }
                    // caso 1 letra + 4 digitos + 2 letras
                    else if (!tieneNumeros(text.toString().substring(0, 1)) && !tieneLetras(text.toString().substring(1, 5)) && !tieneNumeros(text.toString().substring(5, 7))) {
                        matricula.setError(null);
                    } else matricula.setError("El formato de la matricula no es correcto.");

                }
                //matriculo de 8 digitos
                else  if (text.length() == 8) {
                    //caso 2 letra + 4 digitos +  2etra
                    if (!tieneNumeros(text.toString().substring(1, 2)) && !tieneLetras(text.toString().substring(3, 6)) && !tieneNumeros(text.toString().substring(7, 8))) {
                        matricula.setError(null);
                    }
                    //caso  2 letra + 6 digitos
                    else if (!tieneNumeros(text.toString().substring(0, 2)) && !tieneLetras(text.toString().substring(2, 8))) {
                        matricula.setError(null);
                    }
                    else matricula.setError("El formato de la matricula no es correcto.");
                }
                else {
                    matricula.setError("El numero de caracteres es incorrecto.");
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    save=(FloatingActionButton) findViewById(R.id.floatingActionButton);
    save.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (matricula.getError() == null && matricula.getText().toString().trim().length() != 0) {

                    matricula.setError(null);
               incidence.setDescripcion(description.getText().toString());
               incidence.setMala_practica(badPractises.get(spinner.getSelectedItemPosition()));
               incidence.setMatricula(matricula.getText().toString());
                firestoreDatabase.collection("incidences").document(incidence.getId()).set(incidence);

                goToHome();

            } else Toast.makeText(getApplicationContext(),
                    "Por favor. Introduce una matricula correcta.", Toast.LENGTH_SHORT).show();

        }
    });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date incdate=incidence.getFecha();

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditInfo.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                String pattern = "dd/MM/yyyy";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);


                            Date fecha=new Date(year-1900,month,day);
                            date.setText(simpleDateFormat.format(fecha));
                            incidence.setFecha(fecha);
                            }
                        }, year, month, dayOfMonth);
            datePickerDialog.show();
            }
        });
    }

    public int searchBadPractise() {
        for (int i = 0; i < badPractises.size(); i++) {
            if (badPractises.get(i).getId() == incidence.getMala_practica().getId()) {
                return i;
            }
        }
        return 0;
    }

    private void showIncidence() {
        spinner.setSelection(searchBadPractise());
        matricula.setText(incidence.getMatricula());
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        date.setText(simpleDateFormat.format(incidence.getFecha()));

        description.setText(incidence.getDescripcion());
    }

    private void updateView() {
        spinner.setAdapter(new BadPractiseSpinnerAdapter(this, badPractises));
        firestoreDatabase.collection("incidences").document(incidenteId)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        incidence = document.toObject(Incidence.class);
                        showIncidence();
                    } else {
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Se ha producido un error al añadir la incidencia..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void goToHome() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        Bundle bundle = new Bundle();

        this.startActivity(intent);
    }
    private boolean tieneLetras(String texto) {
        String letras = "abcdefghyjklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        texto = texto.toLowerCase();
        for (int i = 0; i < texto.length(); i++) {
            if (letras.indexOf(texto.charAt(i), 0) != -1) {
                return true;
            }
        }
        return false;
    }

    private boolean tieneNumeros(String texto) {
        String numeros = "0123456789";
        texto = texto.toLowerCase();
        for (int i = 0; i < texto.length(); i++) {
            if (numeros.indexOf(texto.charAt(i), 0) != -1) {
                return true;
            }
        }
        return false;
    }
}
