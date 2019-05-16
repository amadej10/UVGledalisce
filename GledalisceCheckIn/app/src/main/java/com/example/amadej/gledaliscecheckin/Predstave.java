package com.example.amadej.gledaliscecheckin;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Predstave extends Fragment {

    private FragmentNovaPredstavaListener listener;

    private EditText input_ime_predstave;
    private Button btn_dodaj_predstavo;
    private ListView listView;
    //private RecyclerView recyclerView;
    private SQLiteDatabase mDatabase;
    private PredstaveAdapterListView adapter;

    public interface FragmentNovaPredstavaListener {
        void onInputNovaPredstavaSend(Predstava predstava);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_predstave, container, false);
        input_ime_predstave = v.findViewById(R.id.input_predstava_ime_predstave);
        btn_dodaj_predstavo = v.findViewById(R.id.btn_predstava_dodaj_predstavo);
        btn_dodaj_predstavo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ime_predstave = input_ime_predstave.getText().toString().trim();
                if (ime_predstave.length() > 0) {
                    //pošlje v main activiti in doda v bazo
                    listener.onInputNovaPredstavaSend(new Predstava(ime_predstave));
                    input_ime_predstave.setText("");

                    //nafili podakte
                    ArrayList<Predstava> list = new ArrayList<>();

                    adapter = new PredstaveAdapterListView(v.getContext(), R.layout.predstava, list);
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                } else {
                    Toast.makeText(getActivity(), "Vpišite ime predstave", Toast.LENGTH_SHORT).show();
                }
            }
        });


        listView = v.findViewById(R.id.listView_predstave);

//nafili podakte
        ArrayList<Predstava> list = new ArrayList<>();

        adapter = new PredstaveAdapterListView(v.getContext(), R.layout.predstava, list);


        adapter.notifyDataSetChanged();

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object object = parent.getItemAtPosition(position);
                Predstava predstava = (Predstava) object;
                String id_predstave = String.valueOf(predstava.getId_predstave());

                Intent intent = new Intent(view.getContext(), ReadNFCActivity.class);
                intent.putExtra("ID_PREDSTAVE", id_predstave);
                intent.putExtra("IME_PREDSTAVE", predstava.getIme_predstave());
                startActivity(intent);
                //Toast.makeText(getActivity(), predstava.getIme_predstave() + " " + Long.valueOf(predstava.getId_predstave()).toString(), Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    public void getData(CharSequence input) {
        Toast.makeText(getActivity(), "hlo", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof NovGledalec.FragmentNovGledalecListener) {
            listener = (Predstave.FragmentNovaPredstavaListener) context;
        } else {
            throw new RuntimeException(context.toString() +
                    "Must implement FragmentNovGledalecListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
/*
    private ArrayList<Predstava> OsveziPodatke(View v) {
        GledalciDBHelper dbHelper = new GledalciDBHelper(v.getContext());
        Cursor data = dbHelper.getPredstave();
        ArrayList<Predstava> listData = new ArrayList<>();
        while (data.moveToNext()) {
            listData.add(new Predstava(data.getInt(0), data.getString(1)));
        }

        return listData;
    }
    */
}
