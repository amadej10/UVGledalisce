package com.example.amadej.gledaliscecheckin;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class GledalciAdapter extends RecyclerView.Adapter<GledalciAdapter.GledalciViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public GledalciAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    public class GledalciViewHolder extends RecyclerView.ViewHolder {

        public TextView gledalec_ime;
        public TextView gledalec_priimek;
        public TextView gledalec_vrsta;
        public TextView gledalec_sedez;
        //public TextView gledalec_stevilo_obiskov;
        public Button btn_uredi;

        public GledalciViewHolder(View itemView) {
            super(itemView);
            gledalec_ime = itemView.findViewById(R.id.txt_gledalec_ime);
            gledalec_priimek = itemView.findViewById(R.id.txt_gledalec_priimek);
            gledalec_vrsta = itemView.findViewById(R.id.txt_gledalec_vrsta);
            gledalec_sedez = itemView.findViewById(R.id.txt_gledalec_sedez);
            //gledalec_stevilo_obiskov = itemView.findViewById(R.id.txt_gledalec_stevilo_obiskov);
           // btn_uredi = itemView.findViewById(R.id.btn_gledalec_uredi);
        }
    }

    @Override
    public GledalciViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.gledalec, parent, false);
        return new GledalciViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GledalciViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        String ime = mCursor.getString(mCursor.getColumnIndex(GledalciContract.VsiGledalci.COLUMN_IME));
        String priimek = mCursor.getString(mCursor.getColumnIndex(GledalciContract.VsiGledalci.COLUMN_PRIIMEK));
        String vrsta = mCursor.getString(mCursor.getColumnIndex(GledalciContract.VsiGledalci.COLUMN_VRSTA));
        String sedez = mCursor.getString(mCursor.getColumnIndex(GledalciContract.VsiGledalci.COLUMN_SEDEZ));
        int steviloObiskov = mCursor.getInt(mCursor.getColumnIndex(GledalciContract.VsiGledalci.COLUMN_STEVILO_OBISKOV));
        long id = mCursor.getLong(mCursor.getColumnIndex(GledalciContract.VsiGledalci._ID));

        holder.gledalec_ime.setText(ime);
        holder.gledalec_priimek.setText(priimek);
        holder.gledalec_vrsta.setText(vrsta);
        holder.gledalec_sedez.setText(sedez);
        //holder.gledalec_stevilo_obiskov.setText(Integer.valueOf(steviloObiskov).toString());
        holder.itemView.setTag(id);

    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

}
