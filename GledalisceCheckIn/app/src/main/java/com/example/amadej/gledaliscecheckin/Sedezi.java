package com.example.amadej.gledaliscecheckin;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class Sedezi extends Fragment {
    ViewGroup layout;
    int seatSize = 100;
    int seatGaping = 10;
    int[][] sedezi;
    List<TextView> seatViewList = new ArrayList<>();

    int STATUS_AVAILABLE = 0;
    int STATUS_BOOKED = 1;
    int STATUS_RESERVED = 3;

    int loadCase = 0;

    int LOAD_CASE_IZBERI_SEDEZ_GLEDALCA = 1;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sedezi, container, false);
        sedezi = new int[10][10];
        loadCase = (int) getArguments().getSerializable("loadCase");

        if (loadCase == LOAD_CASE_IZBERI_SEDEZ_GLEDALCA) {
            getActivity().setTitle("Izberi sedež");
            sedezi = (int[][]) getArguments().getSerializable("zasedeniSedezi");
        }

        drawSeats(v);

        return v;
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

    private void drawSeats(View v) {
        layout = v.findViewById(R.id.SeatcontainerPicker);

        LinearLayout layoutSeat = new LinearLayout(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutSeat.setOrientation(LinearLayout.VERTICAL);
        layoutSeat.setLayoutParams(params);
        layoutSeat.setPadding(8 * seatGaping, 8 * seatGaping, 8 * seatGaping, 8 * seatGaping);
        layout.addView(layoutSeat);

        LinearLayout layout = null;
        int count = 0;
        for (int i = 0; i < sedezi.length; i++) {
            layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layoutSeat.addView(layout);

            TextView view = new TextView(getContext());
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
                    view = new TextView(getContext());
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
                            Toast.makeText(getContext(), "Sedez Rezerviran", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (rez == STATUS_AVAILABLE) {
                    view = new TextView(getContext());
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
                            if (loadCase == LOAD_CASE_IZBERI_SEDEZ_GLEDALCA) {
                                int[] pozicija = (int[]) v.getTag();

                                Bundle bundle = new Bundle();
                                bundle.putSerializable("zasedeniSedezi", sedezi);
                                bundle.putSerializable("izbranSedez", pozicija);
                                NovGledalec gledalec = new NovGledalec();
                                gledalec.setArguments(bundle);
                                replaceFragment(gledalec);
                            }

                            //Toast.makeText(getContext(), "Vrsta "+pozicija[0]+" Sedez "+pozicija[1]+" prost", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

            view = new TextView(getContext());
            layoutParams = new LinearLayout.LayoutParams(seatSize, seatSize);
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


        }
    }



}
