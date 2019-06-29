package com.example.tfg.Activitis;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tfg.Adapters.BadPractiseAdapter;
import com.example.tfg.Models.BadPractise;
import com.example.tfg.Models.Incidence;
import com.example.tfg.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private FloatingActionButton mSpeakBtn, next;
    private EditText matricula;
    private TextView selectincident;
    private ListView lista;
    private FirebaseFirestore firestoreDatabase;
    private ProgressBar progressbar;
    private int first = 0;
    ArrayList<BadPractise> incidences;
    int pos = -1;
    private FirebaseAuth auth;
    private LocationManager locationManager;
    private String provider;
    private LocationListener listener;
    private MediaRecorder recorder = null;
    private Location locationincidence;
    private Incidence audioIncidence;
    private String fileName;
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        progressbar = (ProgressBar) findViewById(R.id.progress);
        selectincident = (TextView) findViewById(R.id.textView2);
        matricula = (EditText) findViewById(R.id.editText);
        lista = (ListView) findViewById(R.id.listview);
        mSpeakBtn = (FloatingActionButton) findViewById(R.id.fab2);
        next = (FloatingActionButton) findViewById(R.id.fab1);

        selectincident.setVisibility(View.INVISIBLE);
        mSpeakBtn.hide();
        next.hide();

        auth = FirebaseAuth.getInstance();
        firestoreDatabase = FirebaseFirestore.getInstance();
        incidences = new ArrayList<>();

        firestoreDatabase.collection("badpractises")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                BadPractise aux = document.toObject(BadPractise.class);
                                incidences.add(aux);
                            }
                            updateListView();
                        }
                    }
                });

        mSpeakBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });


        final SpeechRecognizer mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
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
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        provider = locationManager.getBestProvider(criteria, true);

        if (locationManager.isProviderEnabled(provider)) {


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
            listener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Toast.makeText(getApplicationContext(),
                            "location: " + location.toString(), Toast.LENGTH_SHORT).show();
                    locationincidence = location;
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            locationManager.requestLocationUpdates(provider, 50000, 5, listener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.historialbutton) {
            Intent intent = new Intent(this, History.class);
            this.startActivity(intent);
        }
        if (id == R.id.exitbutton) {
            auth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            this.startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    private void updateListView() {
        selectincident.setVisibility(View.VISIBLE);
        progressbar.setVisibility(View.INVISIBLE);
        mSpeakBtn.show();
        next.show();
        lista.setAdapter(new BadPractiseAdapter(this, R.layout.incidence_element, incidences));
        lista.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (first < firstVisibleItem) {
                    mSpeakBtn.hide();
                    next.hide();
                }
                if (first > firstVisibleItem) {
                    mSpeakBtn.show();
                    next.show();
                }
                first = firstVisibleItem;

                if (pos <= lista.getLastVisiblePosition() && pos >= lista.getFirstVisiblePosition()) {
                    View v = lista.getChildAt(pos - lista.getFirstVisiblePosition());
                    ((LinearLayout) v.findViewById(R.id.background)).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                    ((TextView) v.findViewById(R.id.incidenceTitle)).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
                    ((TextView) v.findViewById(R.id.incidenceDescription)).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));


                }
            }
        });
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSpeakBtn.show();
                next.show();
                final LinearLayout background = (LinearLayout) view.findViewById(R.id.background);
                final Context group = parent.getContext();
                if (pos == -1) {
                    background.setBackgroundColor(ContextCompat.getColor(group, R.color.colorPrimary));
                    ((TextView) view.findViewById(R.id.incidenceTitle)).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
                    ((TextView) view.findViewById(R.id.incidenceDescription)).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
                    pos = position;
                } else if (pos == position) {
                    background.setBackgroundColor(ContextCompat.getColor(group, R.color.colorLight));
                    ((TextView) view.findViewById(R.id.incidenceTitle)).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                    ((TextView) view.findViewById(R.id.incidenceDescription)).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));

                    pos = -1;

                } else {

                    background.setBackgroundColor(ContextCompat.getColor(group, R.color.colorPrimary));
                    ((TextView) view.findViewById(R.id.incidenceTitle)).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
                    ((TextView) view.findViewById(R.id.incidenceDescription)).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorWhite));
                    if (pos <= lista.getLastVisiblePosition() && pos >= lista.getFirstVisiblePosition()) {
                        ((LinearLayout) lista.getChildAt(pos - lista.getFirstVisiblePosition()).findViewById(R.id.background)).setBackgroundColor(ContextCompat.getColor(group, R.color.colorLight));
                        ((TextView) lista.getChildAt(pos - lista.getFirstVisiblePosition()).findViewById(R.id.incidenceTitle)).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                        ((TextView) lista.getChildAt(pos - lista.getFirstVisiblePosition()).findViewById(R.id.incidenceDescription)).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBlack));
                    }
                    pos = position;
                }

            }
        });
    }

    private void startVoiceInput() {
        CountDown countDownDialog = new CountDown();
        countDownDialog.show(getSupportFragmentManager(), "fragment_count_down");
        if (locationincidence != null)
            audioIncidence = new Incidence("PROVISIONAL", new BadPractise("Audio", "Mala práctica añadida en audio"), "", new Date(), FirebaseAuth.getInstance().getCurrentUser().getUid(), locationincidence.getLatitude(), locationincidence.getLongitude());
        else
            audioIncidence = new Incidence("PROVISIONAL", new BadPractise("Audio", "Mala práctica añadida en audio"), "", new Date(), FirebaseAuth.getInstance().getCurrentUser().getUid(), 0.0, 0.0);

        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/" + audioIncidence.getId() + ".3gp";
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncodingBitRate(16 * 44100);
        recorder.setAudioSamplingRate(16000);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                recorder.stop();
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Introduce una Descripción");
                try {
                    startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
                } catch (ActivityNotFoundException a) {

                }
            }
        }, 5000);

    }

    @Override
    protected void onStart() {
        super.onStart();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    }

    public void nextButton(View v) {
        if (checkData(matricula, pos)) {

            matricula.setError(null);
            Intent intent = new Intent(this, AddInfo.class);
            Bundle bundle = new Bundle();
            bundle.putString("incidenteId", incidences.get(pos).getId().toString());
            bundle.putString("matricula", matricula.getText().toString().trim());
            bundle.putString("provider", provider);
            intent.putExtras(bundle);
            this.startActivity(intent);
        }
    }

    public boolean checkData(EditText matriculaux, int posaux) {
        if (matriculaux.getError() == null && matriculaux.getText().toString().trim().length() != 0) {
            if (posaux == -1) {
                Toast.makeText(getApplicationContext(),
                        "Por favor. Seleccione un incidente.", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        } else Toast.makeText(getApplicationContext(),
                "Por favor. Introduce una matricula correcta.", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.
                            getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    audioIncidence.setDescripcion(result.get(0));
                    Uri file = Uri.fromFile(new File(fileName));
                    StorageReference storageRef = storage.getReference();
                    StorageReference riversRef = storageRef.child(audioIncidence.getUrl());
                    UploadTask uploadTask = riversRef.putFile(file);

                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            firestoreDatabase.collection("incidences")
                                    .document(audioIncidence.getId())
                                    .set(audioIncidence);
                        }
                    });

                }
                break;
            }

        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) || !(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) || !(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) || !(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(getApplicationContext(),
                        "Esta aplicación necesita que se concedan todos los permisos", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(listener);
        recorder = null;
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

