package com.example.amadej.gledaliscecheckin;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PredstaveAdapter extends RecyclerView.Adapter<PredstaveAdapter.PredstaveViewHolder> {

    private Context mContext;
    private Cursor mCursor;

    public PredstaveAdapter(Context context, Cursor cursor) {
        mContext = context;
        mCursor = cursor;
    }

    public class PredstaveViewHolder extends RecyclerView.ViewHolder {

        public TextView txt_predstava;


        public PredstaveViewHolder(View itemView) {
            super(itemView);
            txt_predstava = itemView.findViewById(R.id.txt_view_predstava_ime_predstave);

        }
    }

    @Override
    public PredstaveViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.predstava, parent, false);
        return new PredstaveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PredstaveViewHolder holder, int position) {
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        String ime_predstave = mCursor.getString(mCursor.getColumnIndex(GledalciContract.VsePredstave.COLUMN_IME_PREDSTAVE));
        long id = mCursor.getLong(mCursor.getColumnIndex(GledalciContract.VsePredstave._ID));
        holder.txt_predstava.setText(ime_predstave);
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
