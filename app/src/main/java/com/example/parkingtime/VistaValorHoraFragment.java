package com.example.parkingtime;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
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
import android.widget.TableLayout;
import android.widget.TableRow;
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
    // fuentes
    int tamanoTitulo = 20;
    int tamanoSubTitulo = 18;
    int tamanoDatoGrande = 28;
    int tamanoDatoChico = 16;
    // separacion paddings
    int separacionBotones = 4;
    int paddingV = 12;
    int paddingH = 2;

    // ENV
    private static int tarifaActual;
    private static int toleranciaActual;
    private static int fraccionActual;
    private static int horaInicio;
    private static int minutoInicio;
    private static int horaFin;
    private static int minutoFin;
    private static TextView txt_fraccion;
    private static TextView txt_tolerancia;
    private static TextView txt_tiempoCobrado;
    private static TextView txt_tiempoReal;
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
        // presConfig
        LinearLayout moduloConfig = nuevoModulo();
        txt_fraccion = nuevoTexto("fraccion?",tamanoSubTitulo,false,true);
        moduloConfig.addView(txt_fraccion);
        txt_tolerancia = nuevoTexto("tolerancia?",tamanoSubTitulo,false,true);
        moduloConfig.addView(txt_tolerancia);
        container.addView(moduloConfig);

        // presHora
        LinearLayout moduloHora = nuevoModulo();
        agregarSelectorHorarios(moduloHora);
        txt_tiempoReal = nuevoTexto("treal?",tamanoDatoChico,false,true);
        moduloHora.addView(txt_tiempoReal);
        container.addView(moduloHora);

        // presTarifa
        LinearLayout moduloTarifa = nuevoModulo();
        agregarEditorTarifa(moduloTarifa);
        container.addView(moduloTarifa);

        // presCalculo
        LinearLayout moduloCalculo = nuevoModulo();
        agregarTitulo(moduloCalculo,"Tiempo Cobrado");
        txt_tiempoCobrado = nuevoTexto("tCobrado?",tamanoDatoGrande,false,true);
        moduloCalculo.addView(txt_tiempoCobrado);
        agregarTitulo(moduloCalculo,"Monto Total");
        txt_montoTotal = nuevoTexto("mTotal?",tamanoDatoGrande,false,true);
        moduloCalculo.addView(txt_montoTotal);
        container.addView(moduloCalculo);

        // primer update
        updateFormulario();
    }

    private void updateFormulario(){
        //config
        txt_fraccion.setText("Fraccion: " + (60 / fraccionActual) + " min.");
        txt_tolerancia.setText("Tolerancia: " + toleranciaActual + " min.");

        // tiempo real
        int mins = MainBack.calcularMinutosTotales(horaInicio, minutoInicio, horaFin, minutoFin);
        txt_tiempoReal.setText(MainBack.minutoAHora(mins));

        //tiempoCobrado
        String tiempoCobrado = MainBack.calcularTiempoCobrado(mins,toleranciaActual,fraccionActual);
        txt_tiempoCobrado.setText(tiempoCobrado);

        // precioCobrado
        String precioTotal = MainBack.calcularPrecioTotal(mins,fraccionActual,tarifaActual, toleranciaActual);
        txt_montoTotal.setText(enPesos(precioTotal));
        // OK
        Logger.logMain("OK");
    }

    private void getDatosIniciales(){
        //TODO esto debe poder configurarse
        fraccionActual = MainBack.fraccionDefault;
        toleranciaActual = MainBack.toleranciaDefault;

        tarifaActual = MainBack.tarifaDefault;

        horaFin = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        minutoFin = Calendar.getInstance().get(Calendar.MINUTE);
        horaInicio = MainBack.horaAnterior(horaFin, 24);
        minutoInicio = minutoFin;
    }

    private void agregarSelectorHorarios(LinearLayout container){
        // lavel
        agregarTitulo(container,"Seleccoinar Horarios");

        //table
        TableLayout tableLayout = new TableLayout(requireContext());
        tableLayout.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
        ));
        tableLayout.setStretchAllColumns(true);  // Importante para distribución igual

        // linea titulos
        TableRow rowTitulos = new TableRow(requireContext());
        for (int i = 0; i < 2; i++) {
            String titulo = (i == 0) ? "Inicial" : "Final";
            TextView textView = nuevoTexto(titulo,tamanoSubTitulo,true,true);
            rowTitulos.addView(textView);
        }
        // linea Botones
        TableRow rowBotones = new TableRow(requireContext());

        for (int i = 0; i < 2; i++) {

            // setFechaActual
            boolean actual = i != 0;
            String horaActual;
            String tituloScroller;
            if (actual){
                tituloScroller = "Hora de Fin";
                horaActual = formatHora(horaFin, minutoFin);
            }
            else {
                tituloScroller = "Hora de Inicio";
                horaActual = formatHora(horaInicio, minutoInicio);
            }

            // crear boton
            Button btnHora = nuevoBoton(horaActual, tamanoDatoGrande, azul,false);

            // setear accion Boton
            btnHora.setOnClickListener(v -> mostrarDialogHoraScrolleable(tituloScroller, actual, btnHora));

            //añadir al RAW
            rowBotones.addView(btnHora);
            agregarmargenboton(btnHora,separacionBotones);
        }

        tableLayout.addView(rowTitulos);
        tableLayout.addView(rowBotones);

        container.addView(tableLayout);
    }

    private void mostrarDialogHoraScrolleable(String nombre, boolean actual, Button btnHora) {
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
            pickerHora.setValue(horaFin);
            pickerMinuto.setValue(minutoFin);
        }
        else {
            pickerHora.setValue(horaInicio);
            pickerMinuto.setValue(minutoInicio);
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
                horaFin = hora;
                minutoFin = minuto;
                Logger.logMain("salida: " + formatHora(horaFin, minutoFin));
            }
            else {
                horaInicio = hora;
                minutoInicio = minuto;
                Logger.logMain("ingreso: " + formatHora(horaInicio, minutoInicio));
            }
            btnHora.setText(formatHora(hora,minuto));
            updateFormulario();
        });
        builder.setNegativeButton("Cancelar", null);

        builder.create().show();
    }

    private void agregarSpiner(LinearLayout container, String nombre){
        // lavel
        agregarTitulo(container,nombre);
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

    private void agregarEditorTarifa(LinearLayout container){
        // lavel
        agregarTitulo(container,"Tarifa Hora");
        // editor
        EditText editTextNumber = new EditText(getContext());
        editTextNumber.setId(View.generateViewId());
        editTextNumber.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        editTextNumber.setBackgroundColor(grisClaro);
        editTextNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextNumber.setText(enPesos("" + tarifaActual));
        editTextNumber.setTextSize(tamanoDatoGrande);
        editTextNumber.setGravity(Gravity.CENTER);
        // agregar editor
        container.addView(editTextNumber);
        // incrementadores
        agregarIncrementadores(container,editTextNumber);
    }

    private void agregarIncrementadores(LinearLayout container, EditText numerico)   {

        TableLayout tableLayout = new TableLayout(requireContext());
        tableLayout.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
        ));
        tableLayout.setStretchAllColumns(true);  // Importante para distribución igual

        int [] inc = MainBack.incrementdores;
        for (int i = 0; i < 2; i++) {
            TableRow rowBotones = new TableRow(requireContext());

            for (int j = 0; j < inc.length; j++) {

                // armar valor
                String signo;
                int multiplicadorSigno;
                int colorBoton;
                if (i == 0){
                    signo = "+";
                    multiplicadorSigno = 1;
                    colorBoton = azul;
                } else {
                    signo = "-";
                    multiplicadorSigno = -1;
                    colorBoton = rosa;
                }

                // crear boton
                String valorVoton = signo + inc[j];
                Button btnIncrementador = nuevoBoton(valorVoton,tamanoSubTitulo,colorBoton,true);

                // setear accion Boton
                int tarifaBoton = inc[j] * multiplicadorSigno;
                btnIncrementador.setOnClickListener(v -> {
                    tarifaActual =  MainBack.incrementar(tarifaActual, tarifaBoton);
                    Logger.logMain("Tarifa: " + tarifaActual);
                    numerico.setText(enPesos("" + tarifaActual));
                    updateFormulario();
                });

                // añadir cada boton
                rowBotones.addView(btnIncrementador);
                agregarmargenboton(btnIncrementador,separacionBotones);
            }

            tableLayout.addView(rowBotones);
        }
        container.addView(tableLayout);
    }

    private void agregarTitulo(LinearLayout container, String texto) {

        TextView txt = nuevoTexto(
                texto + ":",
                tamanoTitulo,
                false,
                false);
        txt.setBackgroundColor(azul);
        txt.setPadding(12,4,12,4);
        // agregar al layout
        container.addView(txt);
    }

    LinearLayout nuevoModulo(){
        // modulo
        LinearLayout modulo = new LinearLayout(requireContext());
        modulo.setOrientation(LinearLayout.VERTICAL);
        modulo.setBackgroundColor(grisMedio);
        // borde y fondo
        GradientDrawable border = new GradientDrawable();
        border.setColor(grisClaro); // Color de fondo
        border.setStroke(1, Color.BLACK); // Ancho y color del borde
        border.setCornerRadius(10); // Radio de esquinas (opcional)
        modulo.setBackground(border);
        // padding y separacion
        modulo.setPadding(paddingH, paddingV, paddingH, paddingV);
        // separacion
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0,0,0,16);
        modulo.setLayoutParams(params);
        //
        return modulo;
    }

    TextView nuevoTexto(String texto, int tamano, boolean bold, boolean centrado){
        TextView txt = new TextView(requireContext());

        // atrib
        txt.setText(texto);
        txt.setTextSize(tamano == 0 ? 16 : tamano);
        txt.setTextColor(Color.BLACK);
        if (bold) {
            txt.setTypeface(null, Typeface.BOLD);
        }
        if (centrado){
            txt.setGravity(Gravity.CENTER);
        }
        txt.setPadding(12,4,12,4);

        //return
        return txt;
    }

    Button nuevoBoton (String text, int tamanoTexto, int colorFondo,  boolean bold){
        Button btn = new Button(requireContext());
        btn.setTextSize(tamanoTexto);
        btn.setText(text);
        if (bold){btn.setTypeface(null, Typeface.BOLD);}
        else {btn.setTypeface(null, Typeface.NORMAL);}

        // borde y fondo
        GradientDrawable border = new GradientDrawable();
        border.setColor(colorFondo); // Color de fondo
        //border.setStroke(1, Color.BLACK); // Ancho y color del borde
        border.setCornerRadius(20); // Radio de esquinas (opcional)
        btn.setBackground(border);

        return btn;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void agregarmargenboton(Button btn, int margen){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btn.getLayoutParams();
        params.setMargins(margen,margen,margen,margen);
        btn.setLayoutParams(params);
    }

    String enPesos(String pesos){
        return "$ " + pesos;
    }

    String formatHora(int hora, int minuto){
        return String.format("%02d", hora) + ":" + String.format("%02d", minuto);
    }
}
