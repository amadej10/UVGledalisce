package com.example.amadej.gledaliscecheckin;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReadNFCActivity extends AppCompatActivity {
    private NfcAdapter nfcAdapter;
    private String id_predstave;
    private TextView txt_ime_predstave;
    private TextView txt_stevilo_gledalcev;
    //private GledalciDBHelper dbHelper;
    private ListView listView_gledalci_na_predstavi;
    private GledalciAdapterListView adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_nfc);
        //ID predstave in ime predstave dobimo v nov activity
        id_predstave = getIntent().getStringExtra("ID_PREDSTAVE");
        String ime_predstave = getIntent().getStringExtra("IME_PREDSTAVE");
        //inicializacija dbhelperja
        //dbHelper = new GledalciDBHelper(this);
        //iskanje elementov xml layouta
        listView_gledalci_na_predstavi = findViewById(R.id.listView_gledalci_na_predstavi);
        txt_ime_predstave = findViewById(R.id.txt_predstava_ime_predstave);
        txt_stevilo_gledalcev = findViewById(R.id.txt_predstava_stevilo_gledalcev);
        //polnenje elementov xml layouta
        txt_ime_predstave.setText(ime_predstave);
        //adapter = new GledalciAdapterListView(this, R.layout.gledalec, OsveziPodatke(id_predstave, dbHelper));
        adapter.notifyDataSetChanged();
        listView_gledalci_na_predstavi.setAdapter(adapter);


        //Toast.makeText(this, id_predstave, Toast.LENGTH_LONG).show();

        //preveri ce je nfc vkloplen
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            //Toast.makeText(this, "nfc dela", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Vklopi NFC", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Toast.makeText(this, "NFC Povezava", Toast.LENGTH_SHORT).show();

        if (intent.hasExtra(NfcAdapter.EXTRA_TAG)) {
            Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

            if (parcelables != null && parcelables.length > 0) {
                String nfcSporocilo = readTextFromMessage((NdefMessage) parcelables[0]);
                String id_gledalca = getIdFromString(nfcSporocilo);
/*
                boolean gledalec_prisel = dbHelper.addOgledPredstave(id_predstave, id_gledalca);
                if (gledalec_prisel) {
                    dbHelper.addOgledGledalca(id_gledalca);
                    Toast.makeText(this, nfcSporocilo, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Gledalec je že uporabil karto za to predstavo", Toast.LENGTH_SHORT).show();
                }
         */
                // naloži podatke tuki
                ArrayList<Gledalec> list = new ArrayList<>();

                adapter = new GledalciAdapterListView(this, R.layout.gledalec, list);
                adapter.notifyDataSetChanged();
                listView_gledalci_na_predstavi.setAdapter(adapter);

            } else {
                Toast.makeText(this, "Ni NFC sporočila", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private String readTextFromMessage(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();

        if (ndefRecords != null && ndefRecords.length > 0) {
            NdefRecord ndefRecord = ndefRecords[0];

            return getTextFromNdefRecord(ndefRecord);
        } else {
            Toast.makeText(this, "No NDEF records", Toast.LENGTH_SHORT).show();
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

    private void foramtTag(Tag tag, NdefMessage ndefMessage) {
        try {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);

            if (ndefFormatable == null) {
                Toast.makeText(this, "Tag not foramtable", Toast.LENGTH_SHORT).show();

            }

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            Toast.makeText(this, "Tag Written", Toast.LENGTH_SHORT).show();


        } catch (Exception e) {
            Log.e("formatTag", e.getMessage());
        }
    }

    private NdefRecord createTextRecord(String content) {
        try {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language, 0, languageSize);
            payload.write(text, 0, textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());

        } catch (UnsupportedEncodingException e) {
            Log.e("createTextRecord", e.getMessage());
        }
        return null;
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

    private String getIdFromString(String input) {
        String[] razrez = input.split(": ");

        return razrez[1].split("\n")[0];
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
}
