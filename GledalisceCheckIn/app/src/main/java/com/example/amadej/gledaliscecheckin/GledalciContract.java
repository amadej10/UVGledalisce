package com.example.amadej.gledaliscecheckin;

import android.provider.BaseColumns;

public class GledalciContract {

    private GledalciContract() {
    }

    public static final class VsiGledalci implements BaseColumns {
        public static final String TABLE_NAME = "VsiGledalci";
        public static final String COLUMN_IME = "Ime";
        public static final String COLUMN_PRIIMEK = "Priimek";
        public static final String COLUMN_VRSTA = "Vrsta";
        public static final String COLUMN_SEDEZ = "Sedez";
        public static final String COLUMN_STEVILO_OBISKOV = "steviloObiskov";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }

    public static final class VsePredstave implements BaseColumns {
        public static final String TABLE_NAME = "VsePredstave";
        public static final String COLUMN_IME_PREDSTAVE = "ImePredstave";
    }

    public static final class Ogledi implements BaseColumns {
        public static final String TABLE_NAME = "Ogledi";
        public static final String COLUMN_ID_PREDSTAVE = "IDPredstave";
        public static final String COLUMN_ID_GLEDALCA = "IDGledalca";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
