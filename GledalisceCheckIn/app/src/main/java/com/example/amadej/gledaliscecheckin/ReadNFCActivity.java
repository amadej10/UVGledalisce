package com.example.amadej.gledaliscecheckin;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Parcelable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReadNFCActivity extends AppCompatActivity {
    private NfcAdapter nfcAdapter;
    private String id_predstave;
    private TextView txt_ime_predstave;
    private TextView txt_stevilo_gledalcev;
    //private GledalciDBHelper dbHelper;
    private ListView listView_gledalci_na_predstavi;
    private GledalciAdapterListView adapter;
    private String stevilo_gledalcev;
    String ime_predstave;
    ProgressDialog loading;
    int[][] sedezi = new int[17][13];
    ArrayList<Gledalec> list;

    ViewGroup layout;
    int seatSize = 100;
    int seatGaping = 10;
    List<TextView> seatViewList = new ArrayList<>();
    int STATUS_AVAILABLE = 0;
    int STATUS_BOOKED = 1;
    int STATUS_RESERVED = 2;

    //zoom
    private float mScale = 1f;
    private ScaleGestureDetector mScaleDetector;
    GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_nfc);

        Intent intent = getIntent();

        ime_predstave = intent.getStringExtra("IME_PREDSTAVE");

        sedezi = (int[][]) intent.getSerializableExtra("rezerveraniSedezi");
        this.setTitle("Predstava: " + ime_predstave);
        getItems(ime_predstave);
        txt_stevilo_gledalcev = findViewById(R.id.txt_predstava_stevilo_gledalcev);
        //preveri ce je nfc vkloplen
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            //Toast.makeText(this, "nfc dela", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Vklopi NFC", Toast.LENGTH_LONG).show();
            finish();
        }

        gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }

        });

        mScaleDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scale = 1 - detector.getScaleFactor();

                float prevScale = mScale;
                mScale += scale;

                if (mScale < 0.3f) // Minimum scale condition:
                    mScale = 0.3f;

                if (mScale > 3f) // Maximum scale condition:
                    mScale = 3f;
                ScaleAnimation scaleAnimation = new ScaleAnimation(1f / prevScale, 1f / mScale, 1f / prevScale, 1f / mScale, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
                scaleAnimation.setDuration(0);
                scaleAnimation.setFillAfter(true);
                LinearLayout layout = findViewById(R.id.Seatcontainer);
                layout.startAnimation(scaleAnimation);

                return true;
            }
        });

        drawSeats();

        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0.55f, 1f, 0.55f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        scaleAnimation.setDuration(0);
        scaleAnimation.setFillAfter(true);
        LinearLayout layout = findViewById(R.id.Seatcontainer);
        layout.startAnimation(scaleAnimation);

        mScale = 1f / 0.55f;

        //sedezi = (int[][]) getArguments().getSerializable("zasedeniSedezi");


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Toast.makeText(this, "NFC Povezava", Toast.LENGTH_SHORT).show();

        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (parcelables != null && parcelables.length > 0) {
                String nfcSporocilo = readTextFromMessage((NdefMessage) parcelables[0]);

                if (nfcSporocilo.matches("^ID: [\\d]+\\nPriimek: [\\w]+\\nIme: [\\w]+\\nVrsta: [\\d]+\\nSedez: [\\d]+")) {
                    String[] poVrsticah = nfcSporocilo.split("\n");
                    final String ime = poVrsticah[1].split(": ")[1];
                    final String priimek = poVrsticah[2].split(": ")[1];
                    final String vrsta = poVrsticah[3].split(": ")[1];
                    final String sedez = poVrsticah[4].split(": ")[1];

                    int v = Integer.parseInt(vrsta) - 1;
                    int s = Integer.parseInt(sedez) - 1;

                    if (sedezi[v][s] == STATUS_BOOKED) {
                        Toast.makeText(this, "Gledalec je že porabil karto za to predstavo", Toast.LENGTH_SHORT).show();
                    } else {
                        addItemToSheet(this, nfcSporocilo);
                        drawSeats();
                        Toast.makeText(this, "Prišel je " + priimek + " " + ime, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "Zapis na kartici ni pravilnega formata", Toast.LENGTH_SHORT).show();

                }

                //adapter = new GledalciAdapterListView(this, R.layout.gledalec, list);
                //adapter.notifyDataSetChanged();
                //listView_gledalci_na_predstavi.setAdapter(adapter);

            } else {
                Toast.makeText(this, "Ni NFC sporočila", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private String readTextFromMessage(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if (ndefRecords != null && ndefRecords.length > 0) {
            NdefRecord ndefRecord = ndefRecords[0];

            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(200);
            }

            return getTextFromNdefRecord(ndefRecord);
        } else {
            Toast.makeText(this, "Ni NDF zapisa", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    private void enableForegroundDispachSystem() {
        Intent intent = new Intent(this, this.getClass()).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        IntentFilter[] intentFilters = new IntentFilter[]{};

        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disbleForegroundDispachSystem() {
        nfcAdapter.disableForegroundDispatch(this);
    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord) {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1, payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }

    /*

        private NdefMessage createNdefMessage(String content) {
            NdefRecord ndefRecord = createTextRecord(content);

            return new NdefMessage(new NdefRecord[]{ndefRecord});
        }
    */
    @Override
    protected void onResume() {
        super.onResume();
        enableForegroundDispachSystem();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disbleForegroundDispachSystem();
    }

/*
    private ArrayList<Gledalec> OsveziPodatke(String id_predstave, GledalciDBHelper dbHelper) {
        Cursor data = dbHelper.getGledalciPredstave(id_predstave);
        ArrayList<Gledalec> listData = new ArrayList<>();
        int c = 0;
        while (data.moveToNext()) {
            listData.add(new Gledalec(data.getInt(0), data.getString(1), data.getString(2), data.getString(3), data.getString(4), data.getInt(5)));
            c++;
        }
        txt_stevilo_gledalcev.setText(String.valueOf(c));

        return listData;
    }
    */


    private void addItemToSheet(final Context context, String nfcSporocilo) {

        //final ProgressDialog loading = ProgressDialog.show(this, "Dodajanje gledalca", "Prosimo počakajte");

        String[] poVrsticah = nfcSporocilo.split("\n");
        final String id = poVrsticah[0].split(": ")[1];
        final String ime = poVrsticah[1].split(": ")[1];
        final String priimek = poVrsticah[2].split(": ")[1];
        final String vrsta = poVrsticah[3].split(": ")[1];
        final String sedez = poVrsticah[4].split(": ")[1];

        int v = Integer.parseInt(vrsta) - 1;
        int s = Integer.parseInt(sedez) - 1;


        sedezi[v][s] = 1;


        final StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbxr4Dv-AfWK-n927DhLpLoqVIZQvpGBPWi8QXihkR0E2PmBpXpv/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        stevilo_gledalcev = response;
                        txt_stevilo_gledalcev.setText(stevilo_gledalcev);
                        //loading.dismiss();
                        //Intent intent = new Intent(getContext(),MainActivity.class);
                        //startActivity(intent);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "Pri vstavljanju je prišlo do napake", Toast.LENGTH_LONG).show();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                parmas.put("action", "addObiskPredstave");
                parmas.put("naslov", ime_predstave);
                parmas.put("id", id);
                parmas.put("ime", ime);
                parmas.put("priimek", priimek);
                parmas.put("sedez", sedez);
                parmas.put("vrsta", vrsta);


                return parmas;
            }
        };

        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }


    private void getItems(String naslov) {

        loading = ProgressDialog.show(this, "Nalaganje", "prosimo počakajte", false, true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbxr4Dv-AfWK-n927DhLpLoqVIZQvpGBPWi8QXihkR0E2PmBpXpv/exec?action=getObiskPredstave&naslov=" + naslov,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItems(response);
                        drawSeats();
                        loading.dismiss();
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

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
        drawSeats();


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

                int v = Integer.parseInt(vrsta) - 1;
                int s = Integer.parseInt(sedez) - 1;

                //String sedezVrsta = sedez + " " + vrsta;
                //zasedeniZedezi.add(sedezVrsta);
                sedezi[v][s] = 1;
                list.add(new Gledalec(Integer.parseInt(id), ime, priimek, sedez, vrsta));


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        txt_stevilo_gledalcev.setText(String.valueOf(list.size()));
        adapter = new GledalciAdapterListView(this, R.layout.gledalec, list);
        adapter.notifyDataSetChanged();
        //listView.setAdapter(adapter);


        //loading.dismiss();

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        mScaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return gestureDetector.onTouchEvent(event);
    }

    public void drawSeats() {
        layout = findViewById(R.id.Seatcontainer);
        layout.removeAllViews();
        LinearLayout layoutSeat = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutSeat.setOrientation(LinearLayout.VERTICAL);
        layoutSeat.setLayoutParams(params);
        layoutSeat.setPadding(8 * seatGaping, 8 * seatGaping, 8 * seatGaping, 8 * seatGaping);
        layout.addView(layoutSeat);

        LinearLayout layout = null;
        int count = 0;
        for (int i = 0; i < sedezi.length; i++) {
            layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layoutSeat.addView(layout);

            TextView view = new TextView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
            layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
            view.setLayoutParams(layoutParams);
            view.setPadding(0, 0, 0, 2 * seatGaping);
            view.setId(count);
            view.setGravity(Gravity.CENTER);
            view.setTextColor(Color.BLACK);
            view.setTag(STATUS_BOOKED);
            view.setText(i + 1 + "");
            view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
            layout.addView(view);
            seatViewList.add(view);
            for (int j = 0; j < sedezi[i].length; j++) {
                int rez = sedezi[i][j];
                String sedez = j + 1 + "";
                if (rez == STATUS_BOOKED) {
                    view = new TextView(this);
                    layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                    layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                    view.setLayoutParams(layoutParams);
                    view.setPadding(0, 0, 0, 2 * seatGaping);
                    view.setId(count);
                    view.setGravity(Gravity.CENTER);
                    view.setBackgroundResource(R.drawable.ic_event_seat_red_24dp);
                    view.setTextColor(Color.WHITE);
                    view.setTag(STATUS_BOOKED);
                    view.setText(sedez);
                    view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                    layout.addView(view);
                    seatViewList.add(view);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(this, "Sedez Rezerviran", Toast.LENGTH_SHORT).show();
                            //Toast.makeText(v.getContext(), "Zaseden sedež", Toast.LENGTH_SHORT).show();

                        }
                    });
                } else if (rez == STATUS_AVAILABLE) {
                    view = new TextView(this);
                    layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                    layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                    view.setLayoutParams(layoutParams);
                    view.setPadding(0, 0, 0, 2 * seatGaping);
                    view.setId(count);
                    view.setGravity(Gravity.CENTER);
                    view.setBackgroundResource(R.drawable.ic_event_seat_black_24dp);
                    view.setTextColor(Color.WHITE);
                    view.setTag(new int[]{i + 1, j + 1});
                    view.setText(sedez);
                    view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                    layout.addView(view);
                    seatViewList.add(view);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //if (loadCase == LOAD_CASE_IZBERI_SEDEZ_GLEDALCA) {

                            //}

                            //Toast.makeText(v.getContext(), "Prost sedež", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else if (rez == STATUS_RESERVED) {
                    view = new TextView(this);
                    layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
                    layoutParams.setMargins(seatGaping, seatGaping, seatGaping, seatGaping);
                    view.setLayoutParams(layoutParams);
                    view.setPadding(0, 0, 0, 2 * seatGaping);
                    view.setId(count);
                    view.setGravity(Gravity.CENTER);
                    view.setBackgroundResource(R.drawable.ic_event_seat_gray_24dp);
                    view.setTextColor(Color.WHITE);
                    view.setTag(new int[]{i + 1, j + 1});
                    view.setText(sedez);
                    view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                    layout.addView(view);
                    seatViewList.add(view);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //if (loadCase == LOAD_CASE_IZBERI_SEDEZ_GLEDALCA) {

                            //}

                            //Toast.makeText(v.getContext(), "Rezerviran za abonmajevce", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }


        }

    }
}


