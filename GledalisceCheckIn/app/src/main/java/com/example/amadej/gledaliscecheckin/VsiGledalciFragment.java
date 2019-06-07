package com.example.amadej.gledaliscecheckin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.ListView;

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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

public class VsiGledalciFragment extends Fragment {
    private FloatingActionButton dodajGumb;
    private EditText search;
    private ListView listView;
    private GledalciAdapterListView adapter;
    ProgressDialog loading;
    ArrayList<Gledalec> list;
    HashSet<String> zasedeniZedezi;
    int[][] sedezniRed = new int[17][13];


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_vsi_gledalci, container, false);
        getActivity().setTitle("Gledalci");
        listView = v.findViewById(R.id.listViewVsiGledalci);
        dodajGumb = v.findViewById(R.id.floatingActionButtonDodajGledalca);
        search = v.findViewById(R.id.input_search_predstave);
        getItems();

        dodajGumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("zasedeniSedezi", sedezniRed);
                bundle.putSerializable("loadCase", 1);
                Sedezi sedezi = new Sedezi();
                sedezi.setArguments(bundle);
                replaceFragment(sedezi);
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NovGledalec()).commit();
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<Gledalec> results = new ArrayList<>();

                if (s != null && s.length() > 0) {
                    String q = s.toString().toUpperCase();
                    for (Gledalec g : list) {
                        String imePriimek = g.getIme() + " " + g.getPriimek();
                        String priimekIme = g.getPriimek() + " " + g.getIme();
                        if (imePriimek.toUpperCase().contains(q) || priimekIme.toUpperCase().contains(q)) {
                            results.add(new Gledalec(g));
                        }
                    }
                    adapter = new GledalciAdapterListView(getContext(), R.layout.gledalec, results);

                } else {
                    adapter = new GledalciAdapterListView(getContext(), R.layout.gledalec, list);
                }
                adapter.notifyDataSetChanged();
                //Toast.makeText(getContext(),"tuki smo",Toast.LENGTH_SHORT).show();
                listView.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object object = parent.getItemAtPosition(position);
                Gledalec gledalec = (Gledalec) object;

                String id_gledalca = String.valueOf(gledalec.getId());

                // Toast.makeText(getActivity(), id_gledalca, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(view.getContext(), WriteNFCActivity.class);
                intent.putExtra("ID_GLEDALCA", id_gledalca);
                intent.putExtra("ID_IME", gledalec.getIme());
                intent.putExtra("ID_PRIIMEK", gledalec.getPriimek());
                intent.putExtra("ID_SEDEZ", gledalec.getSedez());
                intent.putExtra("ID_VRSTA", gledalec.getVrsta());
                intent.putExtra("ID_OBISKI", gledalec.getStevilo_obiskov());


                startActivity(intent);
            }
        });
        return v;
    }

    private void getItems() {

        loading = ProgressDialog.show(getContext(), "Nalaganje", "prosimo počakajte", false, true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbxr4Dv-AfWK-n927DhLpLoqVIZQvpGBPWi8QXihkR0E2PmBpXpv/exec?action=getItems",
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

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(stringRequest);

    }


    private void parseItems(String jsonResposnce) {

        list = new ArrayList<>();
        //zasedeniZedezi = new HashSet<>();

        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("items");


            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String id = jo.getString("id");
                String ime = jo.getString("ime");
                String priimek = jo.getString("priimek");
                String sedez = jo.getString("sedez");
                String vrsta = jo.getString("vrsta");
                String obiski = jo.getString("obiski");

                int v = Integer.parseInt(vrsta)-1;
                int s = Integer.parseInt(sedez)-1;

                //String sedezVrsta = sedez + " " + vrsta;
                //zasedeniZedezi.add(sedezVrsta);
                sedezniRed[v][s] = 1;
                list.add(new Gledalec(Integer.parseInt(id), ime, priimek, sedez, vrsta, Integer.parseInt(obiski)));


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.sort(list, new Comparator<Gledalec>(){
            public int compare(Gledalec o1, Gledalec o2) {
                int res =  o1.getPriimek().compareToIgnoreCase(o2.getPriimek());
                if (res != 0)
                    return res;
                return o1.getIme().compareToIgnoreCase(o2.getIme());
            }
        });


        adapter = new GledalciAdapterListView(getContext(), R.layout.gledalec, list);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        loading.dismiss();
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
