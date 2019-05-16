package com.example.amadej.gledaliscecheckin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PredstaveAdapterListView extends ArrayAdapter<Predstava>{
    private static final String TAG = "Predstava list adapter";

    private Context mContext;
    private int mResource;
    private ArrayList<Predstava> podatki;

    static class ViewHolder {
        TextView ime_predstave;

    }

    public PredstaveAdapterListView(@NonNull Context context, int resource, ArrayList<Predstava> podatki) {
        super(context, resource, podatki);
        this.mContext = context;
        this.podatki = podatki;
        this.mResource = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        String ime_predstave = getItem(position).getIme_predstave();

        ViewHolder holder;


        if (convertView == null) {

            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(mResource, parent, false);
            holder.ime_predstave = convertView.findViewById(R.id.txt_view_predstava_ime_predstave);



            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();

        }


        holder.ime_predstave.setText(ime_predstave);


        return convertView;
    }

}
