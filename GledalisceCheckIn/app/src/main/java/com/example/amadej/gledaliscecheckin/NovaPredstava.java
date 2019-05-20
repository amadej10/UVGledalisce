package com.example.amadej.gledaliscecheckin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NovaPredstava extends Fragment {
    private EditText input_ime_predstave;
    private Button btn_dodaj_predstavo;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nova_predstava, container, false);
        getActivity().setTitle("Dodaj predstavo");

        input_ime_predstave = v.findViewById(R.id.input_predstava_naslov_predstave);
        btn_dodaj_predstavo = v.findViewById(R.id.btn_predstava_dodaj_predstavo);
        btn_dodaj_predstavo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ime_predstave = input_ime_predstave.getText().toString().trim();
                if (ime_predstave.length() > 0) {
                    //pošlje v main activiti in doda v bazo
                    //listener.onInputNovaPredstavaSend(new Predstava(ime_predstave));
                    addItemToSheet();
                    //input_ime_predstave.setText("");
                    /*

                    //nafili podakte
                    ArrayList<Predstava> list = new ArrayList<>();

                    adapter = new PredstaveAdapterListView(v.getContext(), R.layout.predstava, list);
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                    */
                } else {
                    Toast.makeText(getActivity(), "Vpišite ime predstave", Toast.LENGTH_SHORT).show();
                }
            }

        });
        return v;
    }

    private void   addItemToSheet() {

        final ProgressDialog loading = ProgressDialog.show(getContext(),"Dodajanje predstave","Prosimo počakajte");

        final String naslov = input_ime_predstave.getText().toString().trim();



        final StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbxr4Dv-AfWK-n927DhLpLoqVIZQvpGBPWi8QXihkR0E2PmBpXpv/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Predstave()).commit();
                        //Toast.makeText(getContext(),"Gledalec dodan ID:"+gledalecID,Toast.LENGTH_LONG).show();
/*
                        input_ime.setText("");
                        input_priimek.setText("");
                        input_sedez.setText("");
                        input_vrsta.setText("");
                        input_telefon.setText("");
                        input_email.setText("");

                        Intent intent = new Intent(getContext(), WriteNFCActivity.class);
                        intent.putExtra("ID_GLEDALCA", gledalecID);
                        intent.putExtra("ID_IME", ime);
                        intent.putExtra("ID_PRIIMEK", priimek);
                        intent.putExtra("ID_SEDEZ", sedez);
                        intent.putExtra("ID_VRSTA", vrsta);
                        intent.putExtra("ID_OBISKI", "6");

*/
                        //startActivity(intent);
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
                parmas.put("action","addPredstavaSheet");
                parmas.put("naslov",naslov);

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
