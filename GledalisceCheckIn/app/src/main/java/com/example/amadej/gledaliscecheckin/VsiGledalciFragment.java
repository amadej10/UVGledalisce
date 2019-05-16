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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class VsiGledalciFragment extends Fragment {
    private FloatingActionButton dodajGumb;
    private ListView listView;
    private GledalciAdapterListView adapter;
    ProgressDialog loading;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_vsi_gledalci, container, false);
        getActivity().setTitle("Gledalci");
        listView = v.findViewById(R.id.listViewVsiGledalci);
        dodajGumb =v.findViewById(R.id.floatingActionButtonDodajGledalca);
        getItems();

        dodajGumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new NovGledalec());
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NovGledalec()).commit();
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

        loading =  ProgressDialog.show(getContext(),"Nalaganje","prosimo počakajte",false,true);

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

        ArrayList<Gledalec> list = new ArrayList<>();

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


                list.add(new Gledalec(Integer.parseInt(id), ime, priimek, sedez, vrsta, Integer.parseInt(obiski)));


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new GledalciAdapterListView(getContext(), R.layout.gledalec, list);
        adapter.notifyDataSetChanged();
        //Toast.makeText(getContext(),"tuki smo",Toast.LENGTH_SHORT).show();
        listView.setAdapter(adapter);
/*
        adapter = new SimpleAdapter(this,list,R.layout.list_item_row,
                new String[]{"itemName","brand","price"},new int[]{R.id.tv_item_name,R.id.tv_brand,R.id.tv_price});


        listView.setAdapter(adapter);
    */
        loading.dismiss();
    }

    private void replaceFragment (Fragment fragment){
        String backStateName = fragment.getClass().getName();

        FragmentManager manager = getActivity().getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

        if (!fragmentPopped){ //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fragment_container, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }
}
