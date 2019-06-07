package com.example.amadej.gledaliscecheckin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GledalciAdapterListView extends ArrayAdapter<Gledalec> implements Filterable {
    private Context mContext;
    private int mResource;
    private ArrayList<Gledalec> podatki;
    private ArrayList<Gledalec> podatkiTemp;

    static class ViewHolder {
        TextView ime_gledalca;
        TextView priimek_gledalca;
        TextView vrsta_gledalca;
        TextView sedez_gledalca;
        //TextView stevilo_ogledov_gledalca;
    }

    public GledalciAdapterListView(Context context, int resource, ArrayList<Gledalec> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.podatki = objects;
        this.podatkiTemp = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Gledalec gledalec = getItem(position);


        ViewHolder holder;

        if (convertView == null) {

            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(mResource, parent, false);

            holder.ime_gledalca = convertView.findViewById(R.id.txt_gledalec_ime);
            holder.priimek_gledalca = convertView.findViewById(R.id.txt_gledalec_priimek);
            holder.sedez_gledalca = convertView.findViewById(R.id.txt_gledalec_vrsta);
            holder.vrsta_gledalca = convertView.findViewById(R.id.txt_gledalec_sedez);
            //holder.stevilo_ogledov_gledalca = convertView.findViewById(R.id.txt_gledalec_stevilo_obiskov);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.ime_gledalca.setText(gledalec.getIme());
        holder.priimek_gledalca.setText(gledalec.getPriimek());
        holder.sedez_gledalca.setText(gledalec.getSedez());
        holder.vrsta_gledalca.setText(gledalec.getVrsta());
        //holder.stevilo_ogledov_gledalca.setText(String.valueOf(gledalec.getStevilo_obiskov()));


        return convertView;
    }


}
