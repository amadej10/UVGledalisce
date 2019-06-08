package com.example.amadej.gledaliscecheckin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Predstave extends Fragment {

    private FragmentNovaPredstavaListener listener;
    private EditText input_search;

    private FloatingActionButton dodaj_predstavo;
    private ListView listView;
    //private RecyclerView recyclerView;
    private PredstaveAdapterListView adapter;

    ProgressDialog loading;
    private ArrayList<Predstava> list;

    int[][] sedezi = new int[17][13];

    public interface FragmentNovaPredstavaListener {
        void onInputNovaPredstavaSend(Predstava predstava);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_predstave, container, false);
        getActivity().setTitle("Predstave");

        input_search = v.findViewById(R.id.input_search_predstave);
        dodaj_predstavo = v.findViewById(R.id.floatingActionButton_dodajPredstavo);

        dodaj_predstavo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new NovaPredstava());
            }
        });


        listView = v.findViewById(R.id.listView_predstave);
        getItems();


        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object object = parent.getItemAtPosition(position);
                Predstava predstava = (Predstava) object;

                Intent intent = new Intent(view.getContext(), ReadNFCActivity.class);
                intent.putExtra("IME_PREDSTAVE", predstava.getIme_predstave());
                intent.putExtra("rezerveraniSedezi", sedezi);
                startActivity(intent);
                getActivity().finish();
                //Toast.makeText(getActivity(), predstava.getIme_predstave() + " " + Long.valueOf(predstava.getId_predstave()).toString(), Toast.LENGTH_SHORT).show();
            }
        });

        input_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Predstava> results = new ArrayList<>();

                if (s != null && s.length() > 0) {
                    String q = s.toString().toUpperCase();
                    for (Predstava p : list) {

                        if (p.getIme_predstave().toUpperCase().contains(q)) {
                            results.add(p);
                        }
                    }
                    adapter = new PredstaveAdapterListView(getContext(), R.layout.predstava, results);

                } else {
                    adapter = new PredstaveAdapterListView(getContext(), R.layout.predstava, list);
                }
                adapter.notifyDataSetChanged();
                //Toast.makeText(getContext(),"tuki smo",Toast.LENGTH_SHORT).show();
                listView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return v;
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

    private void getItems() {

        loading = ProgressDialog.show(getContext(), "Nalaganje", "prosimo počakajte", false, true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbxr4Dv-AfWK-n927DhLpLoqVIZQvpGBPWi8QXihkR0E2PmBpXpv/exec?action=getPredstave",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItems(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbxr4Dv-AfWK-n927DhLpLoqVIZQvpGBPWi8QXihkR0E2PmBpXpv/exec?action=getItems",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItems2(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);
        stringRequest2.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(stringRequest2);
        queue.add(stringRequest);

    }


    private void parseItems(String jsonResposnce) {

        list = new ArrayList<>();

        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("items");


            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String naslov = jo.getString("naslov");


                list.add(new Predstava(naslov));


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.sort(list, new Comparator<Predstava>() {
            public int compare(Predstava o1, Predstava o2) {
                return o1.getIme_predstave().compareToIgnoreCase(o2.getIme_predstave());
            }
        });


        adapter = new PredstaveAdapterListView(getContext(), R.layout.predstava, list);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        loading.dismiss();
    }

    private void parseItems2(String jsonResposnce) {
        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("items");


            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String sedez = jo.getString("sedez");
                String vrsta = jo.getString("vrsta");

                int v = Integer.parseInt(vrsta) - 1;
                int s = Integer.parseInt(sedez) - 1;

                //String sedezVrsta = sedez + " " + vrsta;
                //zasedeniZedezi.add(sedezVrsta);
                sedezi[v][s] = 2;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //loading.dismiss();
    }

    private void replaceFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getActivity().getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }
}
