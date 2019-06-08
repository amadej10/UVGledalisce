package com.example.amadej.gledaliscecheckin;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.zip.Inflater;


public class MeniFragment extends Fragment {
    ConstraintLayout gledalci;
    ConstraintLayout predstave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_meni, container, false);
        getActivity().setTitle("Gledališče");
        gledalci = v.findViewById(R.id.constraintLayoutGledalci);
        predstave = v.findViewById(R.id.constraintLayoutPredstave);
        gledalci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentActivity activity = getActivity();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new VsiGledalciFragment())
                        .commit();
                NavigationView navigationView = activity.findViewById(R.id.nav_view);
                navigationView.setCheckedItem(R.id.nav_vsi_gledalci);
            }
        });

        predstave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentActivity activity = getActivity();
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new Predstave())
                        .commit();
                NavigationView navigationView = activity.findViewById(R.id.nav_view);
                navigationView.setCheckedItem(R.id.nav_predstave);
            }
        });

        return v;
    }
}
