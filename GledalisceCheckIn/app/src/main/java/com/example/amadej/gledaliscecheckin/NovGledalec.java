package com.example.amadej.gledaliscecheckin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class NovGledalec extends Fragment {
    private FragmentNovGledalecListener listener;
    //private EditText input_sifra;
    private EditText input_ime;
    private EditText input_priimek;
    private TextView txt_vrsta;
    private TextView txt_sedez;
    private EditText input_email;
    private EditText input_telefon;
    HashSet<String> zasedeniSedezi;
    int [][] sedezniRed;

    private Button btn_dodaj_gledalca;
    //private ListView listView;
    private GledalciAdapterListView adapter;
    ProgressDialog loading;

    private String gledalecID;

    public interface FragmentNovGledalecListener {
        void onInputNovGledalecSend(Gledalec gledalec);

    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nov_gledalec, container, false);
        getActivity().setTitle("Dodaj gledalca");
        sedezniRed = (int[][]) getArguments().getSerializable("zasedeniSedezi");
        int [] pozicija = (int[]) getArguments().getSerializable("izbranSedez");
        //input_sifra = v.findViewById(R.id.input_sifra);
        input_ime = v.findViewById(R.id.input_ime);
        input_priimek = v.findViewById(R.id.input_priimek);
        txt_vrsta = v.findViewById(R.id.textViewVrsta);
        txt_sedez = v.findViewById(R.id.textViewSedez);
        input_email = v.findViewById(R.id.input_email);
        input_telefon = v.findViewById(R.id.input_telefon);

        txt_vrsta.setText(String.valueOf(pozicija[0]));
        txt_sedez.setText(String.valueOf(pozicija[1]));

        //listView = v.findViewById(R.id.listView_gledalci);
        btn_dodaj_gledalca = v.findViewById(R.id.btn_dodaj_gledalca);

        btn_dodaj_gledalca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ime = input_ime.getText().toString().trim();
                String priimek = input_priimek.getText().toString().trim();
                String vrsta = txt_vrsta.getText().toString().trim();
                String sedez = txt_sedez.getText().toString().trim();
                String telefon = input_telefon.getText().toString().trim();
                String email = input_email.getText().toString().trim();


                if (ime.length() > 0 && priimek.length() > 0 && vrsta.length() > 0 && sedez.length() > 0 ) {

                    int vr = Integer.parseInt(vrsta)-1;
                    int se = Integer.parseInt(sedez)-1;

                    if(sedezniRed[vr][se] == 1){
                        Toast.makeText(getActivity(), "Ta sedež je zaseden", Toast.LENGTH_SHORT).show();
                    }else {
                        addItemToSheet();
                    }
                    //Gledalec gledalec = new Gledalec(ime, priimek, vrsta, sedez,telefon,email);

                    //pošlje objekt gledalec v main activity in ga doda v bazo
                    //listener.onInputNovGledalecSend(gledalec);

                    //adapter = new GledalciAdapterListView(v.getContext(), R.layout.gledalec, OsveziPodatke(v));



                    //getItems();

                    //adapter.notifyDataSetChanged();
                    //listView.setAdapter(adapter);
                } else {
                    Toast.makeText(getActivity(), "Izpolnite vsa polja", Toast.LENGTH_SHORT).show();
                }


            }
        });

        input_email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    addItemToSheet();

                }
                return false;
            }
        });
/*
        adapter = new GledalciAdapterListView(v.getContext(), R.layout.gledalec, OsveziPodatke(v));
        adapter.notifyDataSetChanged();

        listView.setAdapter(adapter);
*/

        //getItems();




        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentNovGledalecListener) {
            listener = (FragmentNovGledalecListener) context;
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
    private ArrayList<Gledalec> OsveziPodatke(View v) {
        GledalciDBHelper dbHelper = new GledalciDBHelper(v.getContext());
        Cursor data = dbHelper.getGledalci();
        ArrayList<Gledalec> listData = new ArrayList<>();
        while (data.moveToNext()) {
            listData.add(new Gledalec(data.getInt(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getInt(5)));
        }

        return listData;
    }
*/

    private void   addItemToSheet() {

        final ProgressDialog loading = ProgressDialog.show(getContext(),"Dodajanje gledalca","Prosimo počakajte");

        final String ime = input_ime.getText().toString().trim();
        final String priimek = input_priimek.getText().toString().trim();
        final String vrsta = txt_vrsta.getText().toString().trim();
        final String sedez = txt_sedez.getText().toString().trim();
        final String email = input_email.getText().toString().trim();
        final String telefon = input_telefon.getText().toString().trim();



        final StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbxr4Dv-AfWK-n927DhLpLoqVIZQvpGBPWi8QXihkR0E2PmBpXpv/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        gledalecID = response;

                        loading.dismiss();
                        Toast.makeText(getContext(),"Gledalec dodan ID:"+gledalecID,Toast.LENGTH_LONG).show();

                        input_ime.setText("");
                        input_priimek.setText("");
                        txt_sedez.setText("");
                        txt_vrsta.setText("");
                        input_telefon.setText("");
                        input_email.setText("");

                        Intent intent = new Intent(getContext(), WriteNFCActivity.class);
                        intent.putExtra("ID_GLEDALCA", gledalecID);
                        intent.putExtra("ID_IME", ime);
                        intent.putExtra("ID_PRIIMEK", priimek);
                        intent.putExtra("ID_SEDEZ", sedez);
                        intent.putExtra("ID_VRSTA", vrsta);
                        intent.putExtra("ID_OBISKI", "6");


                        startActivity(intent);
                        getActivity().finish();

                        //Intent intent = new Intent(getContext(),MainActivity.class);
                        //startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(),"Pri vstavljanju je prišlo do napake",Toast.LENGTH_LONG).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                parmas.put("action","addItem");
                parmas.put("ime",ime);
                parmas.put("priimek",priimek);
                parmas.put("sedez",sedez);
                parmas.put("vrsta",vrsta);
                parmas.put("obiski","6");
                parmas.put("telefon",telefon);
                parmas.put("email",email);


                return parmas;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this.getContext());

        queue.add(stringRequest);


    }




}
