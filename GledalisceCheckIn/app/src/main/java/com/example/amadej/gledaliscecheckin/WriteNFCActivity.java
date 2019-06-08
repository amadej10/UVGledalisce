package com.example.amadej.gledaliscecheckin;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class WriteNFCActivity extends AppCompatActivity {
    private NfcAdapter nfcAdapter;
    private TextView txt_karta_zapis;
    String karta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_nfc);
        this.setTitle("Zapisovanje karte");
        txt_karta_zapis = findViewById(R.id.txt_karta_zapis);

        Intent intent = getIntent();
        String id_gledalca = intent.getStringExtra("ID_GLEDALCA");
        String ime = intent.getStringExtra("ID_IME");
        String priimek = intent.getStringExtra("ID_PRIIMEK");
        String sedez = intent.getStringExtra("ID_SEDEZ");
        String vrsta = intent.getStringExtra("ID_VRSTA");
        String obiski = intent.getStringExtra("ID_OBISKI");

        //preveri ce je nfc vkloplen
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter != null && nfcAdapter.isEnabled()) {
            //Toast.makeText(this, "nfc dela", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Vklopi NFC", Toast.LENGTH_LONG).show();
            finish();
        }
        //Toast.makeText(this, id_gledalca, Toast.LENGTH_SHORT).show();


        karta = "ID: " + id_gledalca + "\n" + "Priimek: " + priimek + "\n" + "Ime: " + ime + "\n" + "Vrsta: " + vrsta + "\n" + "Sedez: " + sedez;

        txt_karta_zapis.setText(karta);


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Toast.makeText(this, "NFC Intent", Toast.LENGTH_SHORT).show();

        if (intent.hasExtra(NfcAdapter.EXTRA_TAG) && karta != null) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NdefMessage ndefMessage = createNdefMessage(karta);
            writeNdefMessage(tag, ndefMessage);
        }

    }

    /*
        private void readTextFromMessage(NdefMessage ndefMessage) {
            NdefRecord[] ndefRecords = ndefMessage.getRecords();

            if (ndefRecords != null && ndefRecords.length > 0) {
                NdefRecord ndefRecord = ndefRecords[0];

                String tagContent = getTextFromNdefRecord(ndefRecord);

            } else {
                Toast.makeText(this, "No NDEF records", Toast.LENGTH_SHORT).show();

            }
        }
    */
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

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage) {
        try {
            if (tag == null) {
                Toast.makeText(this, "Tag object can not be null", Toast.LENGTH_SHORT).show();
                return;
            }

            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                foramtTag(tag, ndefMessage);
            } else {
                ndef.connect();
                if (!ndef.isWritable()) {
                    Toast.makeText(this, "Not writable", Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
                Toast.makeText(this, "NFC karta je zapisana", Toast.LENGTH_SHORT).show();

                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(200);
                }

                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("frgToLoad", 1);
                startActivity(intent);
                finish();


            }

        } catch (Exception e) {
            Log.e("writeNdefMessage", e.getMessage());
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
/*
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
*/

    private NdefMessage createNdefMessage(String content) {
        NdefRecord ndefRecord = createTextRecord(content);

        return new NdefMessage(new NdefRecord[]{ndefRecord});
    }

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("frgToLoad", 1);
        startActivity(intent);
        finish();
    }
}
