package com.example.parkingtime;

import android.app.AlertDialog;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class VistaValorHoraFragment extends Fragment {

    int naranja = Color.parseColor("#ffb66c");
    int verde = Color.parseColor("#77bc65");
    int azul = Color.parseColor("#AFCBFF");
    int rosa = Color.parseColor("#ffb66c");
    int grisMedio = Color.parseColor("#E0E0E0");
    int grisClaro = Color.parseColor("#E5E5E5");
    int grisOscuro = Color.parseColor("#9E9E9E");
    int amarillo = Color.parseColor("#ffd428");
    int grisLetra = Color.parseColor("#666666");

    int verticalBloque = 8;
    int horizontalBloque = 32;

    // ENV
    private static int tarifaActual;
    private static int toleranciaActual;
    private static int fraccionActual;
    private static int horaIngreso;
    private static int minutoIngreso;
    private static int horaSalida;
    private static int minutoSalida;
    private static TextView txt_tiempo;
    private static TextView txt_montoTotal;

    public static VistaValorHoraFragment newInstance() {
        VistaValorHoraFragment fragment = new VistaValorHoraFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_valor_hora, container, false);

        // boton back
        Button btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> getActivity().onBackPressed());

        // layout ppal
        LinearLayout containerFormulario = view.findViewById(R.id.containerInfo);

        generarFormulario(containerFormulario);

        return view;
    }

    private void generarFormulario(LinearLayout container) {
        getDatosIniciales();

        // config
        agregarTexto(container, "Fraccion: 1/" + MainBack.fraccionDefault + " hs.", false);
        agregarTexto(container, "Tolerancia: " + MainBack.toleranciaDefault + " min.", false);
        agregarSeparador(container);
        // hora
        agregarBoton(container,"Hora Ingreso",false);
        agregarBoton(container,"Hora Salida",true);
        agregarSeparador(container);

        // tarifa
        agregarEditorNumero(container,"Tarifa Hora");
        agregarSeparador(container);

        // calculo
        txt_tiempo = agregarTexto(container, "tiempo?", true);
        txt_montoTotal = agregarTexto(container, "monto?", true);
        updateFormulario();
    }

    private void updateFormulario(){
        // tiempo
        int mins = MainBack.calcularMinutosTotales(horaIngreso,minutoIngreso,horaSalida,minutoSalida);
        txt_tiempo.setText("Tiempo Total: " + MainBack.minutoAHora(mins));
        // vaor
        Logger.logMain("OK - " + mins);
        Logger.logMain("OK - " + fraccionActual);
        Logger.logMain("OK - " + tarifaActual);

        String precioTotal = MainBack.calcularPrecioTotal(mins,fraccionActual,tarifaActual, toleranciaActual);
        txt_montoTotal.setText("Costo Total: " + precioTotal);
        // OK
        Logger.logMain("OK");
    }

    private void getDatosIniciales(){
        //TODO esto debe poder configurarse
        fraccionActual = MainBack.fraccionDefault;
        toleranciaActual = MainBack.toleranciaDefault;

        tarifaActual = MainBack.tarifaDefault;

        horaSalida = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        minutoSalida = Calendar.getInstance().get(Calendar.MINUTE);
        horaIngreso = horaSalida - 1;
        minutoIngreso = minutoSalida;
    }

    private void agregarSeparador(LinearLayout container){

        int vertical = 45;
        int horizontal = 0;
        //linear
        // Línea que no llega a los bordes
        View separador = new View(getContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                2
        );
        params.setMargins(horizontal, vertical, horizontal, vertical);  // Márgenes laterales también
        separador.setLayoutParams(params);
        separador.setBackgroundColor(Color.BLACK);

        container.addView(separador);
    }

    private void agregarBoton(LinearLayout container, String nombre, boolean actual){
        // lavel
        agregarTexto(container,nombre + ":",true);
        //linear
        LinearLayout liearHora = new LinearLayout(requireContext());
        liearHora.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        liearHora.setOrientation(LinearLayout.HORIZONTAL);
        liearHora.setGravity(Gravity.CENTER_VERTICAL);
        liearHora.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
        liearHora.setBackgroundColor(grisMedio);
        liearHora.setClickable(true);
        liearHora.setFocusable(true);

        // TextView clave
        TextView tvHoraActual = new TextView(requireContext());
        LinearLayout.LayoutParams claveParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1
        );
        tvHoraActual.setLayoutParams(claveParams);
        tvHoraActual.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvHoraActual.setTextSize(24);
        // setFechaActual
        String horaActual;
        if (actual){
            horaActual = horaSalida + ":" + minutoSalida;
        }
        else {
            horaActual = horaIngreso + ":" + minutoIngreso;
        }
        tvHoraActual.setText(horaActual);

        // añadir al linear
        liearHora.addView(tvHoraActual);

        // listener
        liearHora.setOnClickListener(v -> mostrarDialogHoraScrolleable(nombre, actual, tvHoraActual));

        //añadir al container
        container.addView(liearHora);

    }

    private void mostrarDialogHoraScrolleable(String nombre, boolean actual, TextView tvHora) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(nombre);

        // Layout para los NumberPickers
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(50, 40, 50, 40);
        layout.setGravity(Gravity.CENTER);

        // NumberPicker horas
        final NumberPicker pickerHora = new NumberPicker(getContext());
        pickerHora.setMinValue(0);
        pickerHora.setMaxValue(23);
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
        if (actual){
            pickerHora.setValue(horaSalida);
            pickerMinuto.setValue(minutoSalida);
        }
        else {
            pickerHora.setValue(horaIngreso);
            pickerMinuto.setValue(minutoIngreso);
        }
        pickerMinuto.setFormatter(i -> String.format("%02d", i));

        layout.addView(pickerHora);
        layout.addView(separador);
        layout.addView(pickerMinuto);

        builder.setView(layout);
        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            int hora = pickerHora.getValue();
            int minuto = pickerMinuto.getValue();
            // Usar la hora seleccionada
            if (actual){
                horaSalida = hora;
                minutoSalida = minuto;
                Logger.logMain("salida: " + horaSalida  + ":" + minutoSalida);
            }
            else {
                horaIngreso = hora;
                minutoIngreso = minuto;
                Logger.logMain("ingreso: " + horaIngreso  + ":" + minutoIngreso);
            }
            tvHora.setText(hora + ":" + minuto);
            updateFormulario();
        });
        builder.setNegativeButton("Cancelar", null);

        builder.create().show();
    }

    private void agregarSpiner(LinearLayout container, String nombre){
        // lavel
        agregarTexto(container,nombre + ":",true);
        // spinner
        Spinner spinner = new Spinner(getContext());
        spinner.setId(View.generateViewId());
        LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        spinner.setLayoutParams(spinnerParams);
        spinner.setMinimumHeight(144);

        // opciones
        String[] opciones = {"Opción 1", "Opción 2", "Opción 3", "Opción 4"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        // agregar al layout
        container.addView(spinner);
    }

    private void agregarEditorNumero(LinearLayout container, String nombre){
        // lavel
        agregarTexto(container,nombre + ":",true);
        // editor
        EditText editTextNumber = new EditText(getContext());
        editTextNumber.setId(View.generateViewId());
        editTextNumber.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        editTextNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextNumber.setText("" + tarifaActual);

        // agregar editor
        container.addView(editTextNumber);
        // incrementadores
        agregarIncrementadores(container,editTextNumber, true);
        agregarIncrementadores(container,editTextNumber, false);
    }

    private void agregarIncrementadores(LinearLayout container, EditText numerico, boolean incrementar)   {

        LinearLayout lineaIncrementador = new LinearLayout(requireContext());
        lineaIncrementador.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        lineaIncrementador.setOrientation(LinearLayout.HORIZONTAL);

        int [] inc = MainBack.incrementdores;
        for (int j = 0; j < inc.length; j++) {

            // crear boton
            Button btnIncrementador = new Button(requireContext());
            btnIncrementador.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            btnIncrementador.setTextSize(14);

            // discriminar valores
            String signo = "";
            int multiplicadorSigno;
            int colorBoton = azul;
            if (incrementar){
                signo = "+";
                multiplicadorSigno = 1;
            } else {
                multiplicadorSigno = -1;
                colorBoton = rosa;
            }

            int tarifaBoton = inc[j] * multiplicadorSigno;

            // setear valores
            btnIncrementador.setOnClickListener(v -> {
                tarifaActual =  MainBack.incrementar(tarifaActual, tarifaBoton);
                Logger.logMain("Tarifa: " + tarifaActual);
                numerico.setText("" + tarifaActual);
                updateFormulario();
            });
            btnIncrementador.setBackgroundColor(colorBoton);
            btnIncrementador.setText(signo + tarifaBoton);

            // añadir cada boton
            lineaIncrementador.addView(btnIncrementador);
        }
        // añadir la linea
        container.addView(lineaIncrementador);

    }

    private TextView agregarTexto(LinearLayout container, String texto, boolean bold) {
        TextView txt = new TextView(getContext());
        txt.setText(texto);
        txt.setTextSize(16);
        txt.setTextColor(Color.parseColor("#000000"));
        if (bold) {
            txt.setTypeface(null, android.graphics.Typeface.BOLD);
        }
        txt.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) txt.getLayoutParams();
        params.setMargins(horizontalBloque, verticalBloque, horizontalBloque, verticalBloque);
        txt.setLayoutParams(params);
        // agregar al layout
        container.addView(txt);
        return txt;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
