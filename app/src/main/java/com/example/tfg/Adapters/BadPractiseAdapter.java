package com.example.tfg.Adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tfg.Models.BadPractise;
import com.example.tfg.R;

import java.util.ArrayList;

public class BadPractiseAdapter extends ArrayAdapter {
    ArrayList<BadPractise> incidences;
    int pos = -1;

    public BadPractiseAdapter(Context context, int textViewResourceId, ArrayList<BadPractise> objects) {
        super(context, textViewResourceId, objects);
        incidences = objects;

    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.badpractise_element, null);
        final Context group = parent.getContext();

        if (v != null) {
            TextView title = (TextView) v.findViewById(R.id.incidenceTitle);
            TextView description = (TextView) v.findViewById(R.id.incidenceDescription);

            title.setText(incidences.get(position).getTitulo());
            description.setText(incidences.get(position).getDescripcion());


        }
        return v;
    }
}
