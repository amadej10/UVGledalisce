package com.example.amadej.gledaliscecheckin;

import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, NovGledalec.FragmentNovGledalecListener, Predstave.FragmentNovaPredstavaListener {

    private SQLiteDatabase mDatabase;
    private GledalciAdapter mAdapter;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    //private GledalciDBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Baza
        //   dbHelper = new GledalciDBHelper(this);
        // mDatabase = dbHelper.getWritableDatabase();

        //Predalnik menu
        drawer = findViewById(R.id.draw_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == DrawerLayout.STATE_SETTLING && !drawer.isDrawerOpen(GravityCompat.START)) {
                    closeKeyboard();
                }
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //nalozi prvi fragment
        /*novGledalec = new NovGledalec();*/
        if (savedInstanceState == null) {

            Intent intent = getIntent();
            int fragment = 0;
            if (intent.hasExtra("frgToLoad")) {
                fragment = getIntent().getExtras().getInt("frgToLoad");
            }
            if (fragment == 1) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new VsiGledalciFragment())
                        .commit();
                navigationView.setCheckedItem(R.id.nav_vsi_gledalci);
            } else if (fragment == 2) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Predstave())
                        .commit();
                navigationView.setCheckedItem(R.id.nav_vsi_gledalci);
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MeniFragment())
                        .commit();
                navigationView.setCheckedItem(R.id.nav_predstave);
            }

        }


    }

    @Override
    public void onBackPressed() {

        Object frg = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if (frg instanceof VsiGledalciFragment) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MeniFragment())
                    .commit();
        } else if (frg instanceof Predstave) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MeniFragment())
                    .commit();

        } else if (frg instanceof Sedezi) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new VsiGledalciFragment())
                    .commit();

        } else if (frg instanceof MeniFragment) {
            new AlertDialog.Builder(this)
                    .setMessage("Ali res želite zapreti aplikacijo?")
                    .setCancelable(false)
                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("Ne", null)
                    .show();

        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_vsi_gledalci:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new VsiGledalciFragment()).commit();
                closeKeyboard();
                break;
            case R.id.nav_predstave:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Predstave()).commit();
                closeKeyboard();
                break;

        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onInputNovGledalecSend(Gledalec gledalec) {
        closeKeyboard();
        //dbHelper.addGledalec(gledalec);
    }


    public void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onInputNovaPredstavaSend(Predstava predstava) {
/*
        //Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
        ContentValues cv = new ContentValues();
        cv.put(GledalciContract.VsePredstave.COLUMN_IME_PREDSTAVE, input);
        mDatabase.insert(GledalciContract.VsePredstave.TABLE_NAME, null, cv);
        //Toast.makeText(this, "Dodano v Bazo", Toast.LENGTH_SHORT).show();
        */

        closeKeyboard();
        //Doda novo predstavo v bazo
        //dbHelper.addPredstava(predstava);

    }


}
