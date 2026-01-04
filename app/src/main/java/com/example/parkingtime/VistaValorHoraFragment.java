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
import android.view.Window;
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
    int paddingV = 8;
    int paddingH = 2;

    // ENV
    private static int fraccionActual;
    private static int toleranciaActual;
    private static int tarifaActual;
    private static int horaInicio;
    private static int minutoInicio;
    private static int horaFin;
    private static int minutoFin;
    private static TextView txt_fraccion;
    private static TextView txt_tolerancia;
    private static TextView txt_tiempoCobrado;
    private static TextView txt_tiempoReal;
    private static TextView txt_montoTotal;
    private static PreferenceManager preferenceManager;

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

        // preferenceManager
        preferenceManager = PreferenceManager.getInstance(requireContext());

        // generar
        generarFormulario(containerFormulario);

        return view;
    }

    private void generarFormulario(LinearLayout container) {
        // data
        getDatosIniciales();

        // presConfig
        agregarPresConfig(container);

        // presHora
        agregarPresHorarios(container);

        // presTarifa
        agregarPresTarifa(container);

        // presCalculo
        agregarPresCalculo(container);

        // primer update
        updateFormulario();
        status("generarFormulario");
    }

    private void updateFormulario(){
        // actualizar los los datos calculados

        //config
        txt_fraccion.setText("Fraccion: " + fraccionActual + " min");
        txt_tolerancia.setText("Tolerancia: " + toleranciaActual + " min");

        // tiempo real
        int mins = MainBack.calcularMinutosTotales(horaInicio, minutoInicio, horaFin, minutoFin);
        txt_tiempoReal.setText(MainBack.minutoAHora(mins));

        //tiempoCobrado
        String tiempoCobrado = MainBack.calcularTiempoCobrado(mins,toleranciaActual,fraccionActual);
        txt_tiempoCobrado.setText(tiempoCobrado);

        // precioCobrado
        String precioTotal = MainBack.calcularPrecioTotal(mins,fraccionActual,tarifaActual, toleranciaActual);
        txt_montoTotal.setText(MainBack.enPesos(precioTotal));
    }

    private void getDatosIniciales(){
        fraccionActual = preferenceManager.getFraccion();
        toleranciaActual = preferenceManager.getTolerancia();
        tarifaActual = preferenceManager.getTarifa();

        horaFin = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        minutoFin = Calendar.getInstance().get(Calendar.MINUTE);
        horaInicio = MainBack.horaAnterior(horaFin, 24);
        minutoInicio = minutoFin;
        status("getDatosIniciales");
    }

    private void agregarPresConfig(LinearLayout container){
        LinearLayout moduloConfig = nuevoModulo();
        txt_fraccion = nuevoTexto("fraccion?",tamanoSubTitulo,false,true);
        moduloConfig.addView(txt_fraccion);
        txt_tolerancia = nuevoTexto("tolerancia?",tamanoSubTitulo,false,true);
        moduloConfig.addView(txt_tolerancia);
        // accion config
        moduloConfig.setOnClickListener(v -> mostrarDialogoConfig());
        // modulo -> container
        container.addView(moduloConfig);
    }

    private void agregarPresHorarios(LinearLayout container){
        LinearLayout moduloHora = nuevoModulo();
        // titulo
        agregarTitulo(moduloHora,"Seleccoinar Horarios");
        // table
        TableLayout tableLayout = new TableLayout(requireContext());
        tableLayout.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
        ));
        tableLayout.setStretchAllColumns(true);  // Importante para distribución igual
        // crear las 2 lineas
        TableRow rowTitulos = new TableRow(requireContext());
        TableRow rowBotones = new TableRow(requireContext());
        // contenido
        for (int i = 0; i < 2; i++) {
            boolean actual = i != 0;
            String titulo;
            String horaActual;
            // definicion inicial-final
            if (actual){
                titulo = "Final";
                horaActual = MainBack.formatHora(horaFin, minutoFin);
            }
            else {
                titulo = "Inicial";
                horaActual = MainBack.formatHora(horaInicio, minutoInicio);
            }
            // crear titulo y boton
            TextView tituloHorario = nuevoTexto(titulo,tamanoSubTitulo,true,true);
            Button btnHora = nuevoBoton(horaActual, tamanoDatoGrande, azul,false);
            // setear accion Boton
            btnHora.setOnClickListener(v -> mostrarDialogoHorario("Hora " + titulo, actual, btnHora));
            // añadir titulo y boton al RAW correspondiente
            rowTitulos.addView(tituloHorario);
            rowBotones.addView(btnHora);
            // separacion de botones
            agregarMargenBoton(btnHora,separacionBotones);
        }
        // lineas -> tabla
        tableLayout.addView(rowTitulos);
        tableLayout.addView(rowBotones);
        // tabla -> modulo
        moduloHora.addView(tableLayout);
        // tiempoReal -> modulo
        txt_tiempoReal = nuevoTexto("treal?",tamanoSubTitulo,false,true);
        moduloHora.addView(txt_tiempoReal);
        // modulo -> container
        container.addView(moduloHora);
        // log
    }

    private void mostrarDialogoHorario(String nombre, boolean actual, Button btnHora) {
        AlertDialog dialog1 = nuevoDialogo(nombre);

        // Layout para los NumberPickers
        LinearLayout layoutDialogoHorario = nuevoLinar(false,0,8,4);

        // NumberPickers y separador
        final NumberPicker pickerHora = nuevoNumberPicker(0,23,true);
        TextView separador = nuevoTexto(" : ",30,false,true);
        final NumberPicker pickerMinuto = nuevoNumberPicker(0,59,true);

        // setear valores
        if (actual){
            pickerHora.setValue(horaFin);
            pickerMinuto.setValue(minutoFin);
        }
        else {
            pickerHora.setValue(horaInicio);
            pickerMinuto.setValue(minutoInicio);
        }

        layoutDialogoHorario.addView(pickerHora);
        layoutDialogoHorario.addView(separador);
        layoutDialogoHorario.addView(pickerMinuto);

        dialog1.setView(layoutDialogoHorario);
        dialog1.setButton(AlertDialog.BUTTON_POSITIVE,"Aceptar", (dialog, which) -> {
            accionAceptarHorario(pickerHora,pickerMinuto,actual,btnHora);
        });
        dialog1.setButton(AlertDialog.BUTTON_NEGATIVE,"Cancelar", (dialog, which) -> {});

        dialog1.show();
    }

    private void accionAceptarHorario(NumberPicker pickerHora,NumberPicker pickerMinuto, boolean actual, Button btnHora){
        int hora = pickerHora.getValue();
        int minuto = pickerMinuto.getValue();
        // Usar la hora seleccionada
        if (actual){
            horaFin = hora;
            minutoFin = minuto;
            Logger.logMain("salida: " + MainBack.formatHora(horaFin, minutoFin));
        }
        else {
            horaInicio = hora;
            minutoInicio = minuto;
            Logger.logMain("ingreso: " + MainBack.formatHora(horaInicio, minutoInicio));
        }
        btnHora.setText(MainBack.formatHora(hora,minuto));
        updateFormulario();
        status("accionAceptarHorario");
    }

    private void mostrarDialogoConfig() {
        AlertDialog dialog1 = nuevoDialogo("Config");

        // Layout para los NumberPickers
        LinearLayout layoutDialogo = nuevoLinar(true,0,8,4);

        // config fraccion
        agregarTitulo(layoutDialogo, "Fraccion");
        Spinner spnFraccion = nuevoSpiner(MainBack.fracciones);
        spnFraccion.setSelection(MainBack.getIndice(MainBack.fracciones,fraccionActual));
        layoutDialogo.addView(spnFraccion);
        // config tolerancia
        agregarTitulo(layoutDialogo, "Tolerancia");
        Spinner spnTolerancia = nuevoSpiner(MainBack.tolerancias);
        spnTolerancia.setSelection(MainBack.getIndice(MainBack.tolerancias,toleranciaActual));
        layoutDialogo.addView(spnTolerancia);
        // setear dialog1
        dialog1.setView(layoutDialogo);

        dialog1.setButton(AlertDialog.BUTTON_POSITIVE, "Aceptar", (dialog, which) -> {
            accionAceptarConfig(spnFraccion,spnTolerancia);
        });
        dialog1.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar", (dialog, which) -> {});

        dialog1.show();
    }

    private void accionAceptarConfig(Spinner spnFraccion, Spinner spnTolerancia){
        // actualizar variables ENV
        fraccionActual = Integer.parseInt(spnFraccion.getSelectedItem().toString());
        toleranciaActual = Integer.parseInt(spnTolerancia.getSelectedItem().toString());
        // actualizar preferenceManager - persistencia
        preferenceManager.setFraccion(fraccionActual);
        preferenceManager.setTolerancia(toleranciaActual);
        //update
        updateFormulario();
        status("accionAceptarConfig");
    }

    private void agregarPresTarifa(LinearLayout container){
        LinearLayout moduloTarifa = nuevoModulo();
        // titulo
        agregarTitulo(moduloTarifa,"Tarifa Hora");
        // editor
        EditText editTextNumber = new EditText(getContext());
        editTextNumber.setId(View.generateViewId());
        editTextNumber.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        editTextNumber.setBackgroundColor(grisClaro);
        editTextNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextNumber.setText(MainBack.enPesos("" + tarifaActual));
        editTextNumber.setTextSize(tamanoDatoGrande);
        editTextNumber.setGravity(Gravity.CENTER);
        // agregar editor
        moduloTarifa.addView(editTextNumber);
        // incrementadores
        agregarIncrementadores(moduloTarifa,editTextNumber);
        // modulo -> container
        container.addView(moduloTarifa);
    }

    private void agregarPresCalculo(LinearLayout container){
        LinearLayout moduloCalculo = nuevoModulo();
        agregarTitulo(moduloCalculo,"Tiempo Cobrado");
        txt_tiempoCobrado = nuevoTexto("tCobrado?",tamanoDatoGrande,false,true);
        moduloCalculo.addView(txt_tiempoCobrado);
        agregarTitulo(moduloCalculo,"Monto Total");
        txt_montoTotal = nuevoTexto("mTotal?",tamanoDatoGrande,false,true);
        moduloCalculo.addView(txt_montoTotal);
        container.addView(moduloCalculo);
    }

    private void agregarIncrementadores(LinearLayout container, EditText numerico)   {
        // tabla
        TableLayout tableLayout = new TableLayout(requireContext());
        tableLayout.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
        ));
        tableLayout.setStretchAllColumns(true);
        // filas de botones
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
                String textoBoton = signo + inc[j];
                Button btnIncrementador = nuevoBoton(textoBoton,tamanoSubTitulo,colorBoton,true);

                // setear accion Boton
                int valorBoton = inc[j] * multiplicadorSigno;
                btnIncrementador.setOnClickListener(v -> {
                    accionIncrementador(valorBoton,numerico);
                });

                // añadir cada boton
                rowBotones.addView(btnIncrementador);
                agregarMargenBoton(btnIncrementador,separacionBotones);
            }
            tableLayout.addView(rowBotones);
        }
        container.addView(tableLayout);
    }

    private void accionIncrementador(int valorBoton, EditText editable){
        tarifaActual =  MainBack.incrementar(tarifaActual, valorBoton);
        editable.setText(MainBack.enPesos("" + tarifaActual));
        updateFormulario();
        status("accionIncrementador");
    }

    private void agregarTitulo(LinearLayout container, String texto) {
        TextView txt = nuevoTexto(texto + ":",tamanoTitulo,false,false);
        txt.setBackgroundColor(azul);
        // agregar al layout
        container.addView(txt);
    }

    AlertDialog nuevoDialogo(String titulo){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(titulo);
        AlertDialog dialog = builder.create();

        // estilo
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(nuevoFondo(grisClaro,1,Color.BLACK,20));
        }

        return dialog;
    }

    LinearLayout nuevoModulo(){
        // modulo
        LinearLayout modulo = nuevoLinar(true,0,paddingH,paddingV);
        // borde y fondo
        modulo.setBackground(nuevoFondo(grisMedio,1,Color.BLACK,10));
        // separacion
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0,0,0,8);
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
        btn.setBackground(nuevoFondo(colorFondo,1,Color.LTGRAY,30));

        return btn;
    }

    NumberPicker nuevoNumberPicker(int min, int max, boolean formaterHora){
        NumberPicker picker = new NumberPicker(requireContext());
        picker.setMinValue(min);
        picker.setMaxValue(max);
        if (formaterHora){
            picker.setFormatter(i -> String.format("%02d", i));
        }
        return picker;
    }

    private Spinner nuevoSpiner(int[] valores){

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
        String[] valoresString = new String[valores.length];
        for (int i = 0; i < valores.length; i++) {
            valoresString[i] = String.valueOf(valores[i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, valoresString);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);
        // accion
        return spinner;
    }

    GradientDrawable nuevoFondo (int colorFondo, int borde, int colorBorde,int radio){
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(colorFondo);
        drawable.setCornerRadius(radio);
        drawable.setStroke(borde, colorBorde);
        return drawable;
    }

    LinearLayout nuevoLinar (boolean vertical, int colorFondo, int paddingH, int paddingV) {
        LinearLayout linear = new LinearLayout(requireContext());

        if (vertical) {linear.setOrientation(LinearLayout.VERTICAL);}
        else {linear.setOrientation(LinearLayout.HORIZONTAL);}

        if (colorFondo != 0){
            linear.setBackgroundColor(colorFondo);
        }
        linear.setGravity(Gravity.CENTER);
        linear.setPadding(dpToPx(paddingH), dpToPx(paddingV), dpToPx(paddingH), dpToPx(paddingV));
        return linear;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void agregarMargenBoton(Button btn, int margen){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) btn.getLayoutParams();
        params.setMargins(margen,margen,margen,margen);
        btn.setLayoutParams(params);
    }

    void status(String nonmbre){
        /*
        Logger.logMain("status." + nonmbre);
        Logger.logMain("| frc | tol | tar |");
        Logger.logMain("| " + fraccionActual + " | " + toleranciaActual + " | " + tarifaActual + " |");
        */
    }

}
