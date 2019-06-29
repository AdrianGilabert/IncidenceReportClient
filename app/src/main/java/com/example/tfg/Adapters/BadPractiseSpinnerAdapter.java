package com.example.tfg.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tfg.Models.BadPractise;
import com.example.tfg.Models.Incidence;
import com.example.tfg.R;

import java.util.ArrayList;

public class BadPractiseSpinnerAdapter extends ArrayAdapter<BadPractise> {
    ArrayList<BadPractise> incidences;

    public BadPractiseSpinnerAdapter(Context context, ArrayList<BadPractise> countryList) {
        super(context, 0, countryList);
    incidences=countryList;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.badpractise_spin_element, parent, false
            );
        }
        TextView title = (TextView) convertView.findViewById(R.id.incidenceTitle);
        TextView description = (TextView) convertView.findViewById(R.id.incidenceDescription);




        BadPractise currentItem = getItem(position);

        if (currentItem != null) {
            title.setText(incidences.get(position).getTitulo());
            description.setText(incidences.get(position).getDescripcion());
        }

        return convertView;
    }
}

