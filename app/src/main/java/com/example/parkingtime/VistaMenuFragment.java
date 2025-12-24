package com.example.parkingtime;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class VistaMenuFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vista_menu, container, false);


        Button btnValorHora = view.findViewById(R.id.btnValorHora);
        Button btnTest = view.findViewById(R.id.btnTest);
        Button btnSalir = view.findViewById(R.id.btnSalir);


        btnValorHora.setOnClickListener(v -> {


            ((MainActivity) getActivity()).showFragmentWithBackStack(
                    VistaValorHoraFragment.newInstance());
        });

        btnTest.setOnClickListener(v -> {
            ((MainActivity) getActivity()).showFragmentWithBackStack(
                    VistaTestFragment.newInstance());
        });

        btnSalir.setOnClickListener(v -> {
            getActivity().finish();
        });

        return view;
    }
}
