package com.example.parkingtime;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class VistaTestFragment extends Fragment {

    public static VistaTestFragment newInstance() {
        VistaTestFragment fragment = new VistaTestFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frafment_test, container, false);

        LinearLayout containerTest = view.findViewById(R.id.containerTest);

        Button btnFecha = new Button(getContext());
        btnFecha.setText("Seleccionar Fecha");
        btnFecha.setOnClickListener(v -> mostrarDatePicker(btnFecha));

        Button btnHora = new Button(getContext());
        btnHora.setText("Seleccionar Hora");
        btnHora.setOnClickListener(v -> mostrarDialogHoraScrolleable());

        //containerTest.addView(btnFecha);
        containerTest.addView(btnHora);

        return view;
    }

    private void mostrarDialogHoraScrolleable() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Selecciona la hora");

        // Layout para los NumberPickers
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(50, 40, 50, 40);
        layout.setGravity(Gravity.CENTER);

        // NumberPicker horas
        final NumberPicker pickerHora = new NumberPicker(getContext());
        pickerHora.setMinValue(0);
        pickerHora.setMaxValue(23);
        pickerHora.setValue(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
        pickerHora.setFormatter(i -> String.format("%02d", i));

        // Separador
        TextView separador = new TextView(getContext());
        separador.setText(" : ");
        separador.setTextSize(30);
        separador.setPadding(20, 0, 20, 0);

        // NumberPicker minutos
        final NumberPicker pickerMinuto = new NumberPicker(getContext());
        pickerMinuto.setMinValue(0);
        pickerMinuto.setMaxValue(59);
        pickerMinuto.setValue(Calendar.getInstance().get(Calendar.MINUTE));
        pickerMinuto.setFormatter(i -> String.format("%02d", i));

        layout.addView(pickerHora);
        layout.addView(separador);
        layout.addView(pickerMinuto);

        builder.setView(layout);
        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            int hora = pickerHora.getValue();
            int minuto = pickerMinuto.getValue();
            String tiempo = String.format("%02d:%02d", hora, minuto);
            // Usar la hora seleccionada
        });
        builder.setNegativeButton("Cancelar", null);

        builder.create().show();
    }

    private void mostrarDatePicker(Button btnFecha) {
        Calendar calendar = Calendar.getInstance();
        int año = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    // Fecha seleccionada
                    String fecha = dayOfMonth + "/" + (month + 1) + "/" + year;
                    btnFecha.setText(fecha);
                },
                año, mes, dia
        );
        datePickerDialog.show();
    }

    private void mostrarTimePicker(Button btnHora) {
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int minuto = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (view, hourOfDay, minute) -> {
                    // Hora seleccionada
                    String tiempo = hourOfDay + ":" + minute;
                    btnHora.setText(tiempo);
                },
                hora, minuto, true // true = formato 24 horas
        );
        timePickerDialog.show();
    }

}
