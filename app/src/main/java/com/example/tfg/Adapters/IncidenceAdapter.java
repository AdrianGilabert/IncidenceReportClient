package com.example.tfg.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tfg.Activitis.AddInfo;
import com.example.tfg.Activitis.EditInfo;
import com.example.tfg.Models.Incidence;
import com.example.tfg.R;

import java.util.ArrayList;

public class IncidenceAdapter extends ArrayAdapter {
    ArrayList<Incidence> incidences;

    public IncidenceAdapter(Context context, int textViewResourceId, ArrayList<Incidence> objects) {
        super(context, textViewResourceId, objects);
        incidences = objects;

    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
      final  Context context=parent.getContext();

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.incidence_element, null);
        if (v != null) {
            LinearLayout background = (LinearLayout) v.findViewById(R.id.incidenceclick);
            TextView title = (TextView) v.findViewById(R.id.incidenceTitle);
            TextView description = (TextView) v.findViewById(R.id.incidenceDescription);
            TextView matricula = (TextView) v.findViewById(R.id.plateview);
            matricula.setText(incidences.get(position).getMatricula());
            title.setText(incidences.get(position).getMala_practica().getTitulo());
            description.setText(incidences.get(position).getMala_practica().getDescripcion());
            background.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EditInfo.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("incidenteId", incidences.get(position).getId().toString());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                }
            });

        }
        return v;
    }
}
