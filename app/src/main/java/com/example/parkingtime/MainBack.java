package com.example.parkingtime;

import java.util.Date;

public class MainBack {

    public static int fraccionDefault = 2;
    public static int toleranciaDefault = 5;
    public static int tarifaDefault = 3000;
    public static boolean primerHoraCompletaDefault = true;
    public static int[] incrementdores = {100,500,1000};

    public static int incrementar(int valor, int sumando){
        if (sumando > 0){
            return valor + sumando;
        }
        else if (valor > (-sumando)) {
            return valor + sumando;
        }
        return 0;
    }

    public static String tiempoTranscurrido(Date inicio, Date fin){
        return minutoAHora(calcularMinutosTotales(inicio,fin));
    }

    public static int calcularMinutosTotales(Date inicio, Date fin){
        return 210;
    }

    public static int calcularFracciones(int minutosTotales, int tolerancia){
        return 3;
    }

    public static Double calcularPrecioTotal(int cantidad, int denominadorFraccion, int precioHora){
        return (double) (cantidad / denominadorFraccion * precioHora);
    }

    public static String minutoAHora(int minutos){
        String r = "";
        r += (minutos / 60);
        r += ":";
        r += (minutos % 60);
        return r;
    }

}
