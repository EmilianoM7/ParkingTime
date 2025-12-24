package com.example.parkingtime;


public class MainBack {

    public static int fraccionDefault = 2;
    public static int toleranciaDefault = 5;
    public static int tarifaDefault = 3000;
    public static boolean primerHoraCompletaDefault = true;
    public static int[] incrementdores = {100,500,1000};

    public static String calcularPrecioTotal(int mins, int divisor, int precioHora, int tolerancia){
        int fracciones = calcularFracciones(mins, divisor, tolerancia);
        float factor =  (float) fracciones / divisor;
        Logger.logBack("calcular frc: " + fracciones);
        Logger.logBack("calcular divisor " + divisor);
        Logger.logBack("ftr: " + factor);
        return  "" + (factor * precioHora);
    }

    public static int calcularMinutosTotales(int hIngreso, int mIngreso, int hSalida, int mSalida){
        int restaHoras = restarHorario(hIngreso, hSalida, 24) * 60;
        int restaMinutos = restarHorario(mIngreso,mSalida, 60);
        return restaHoras + restaMinutos;
    }

    public static String minutoAHora(int minutos){
        String r = "";
        r += (minutos / 60);
        r += ":";
        r += (minutos % 60);
        return r;
    }

    public static int incrementar(int valor, int sumando){
        if (sumando > 0){
            return valor + sumando;
        }
        else if (valor > (-sumando)) {
            return valor + sumando;
        }
        return 0;
    }

    private static int restarHorario (int ingreso, int salida, int limite){
        if (ingreso > salida){
            return salida + limite - ingreso;
        }
        return salida - ingreso;
    }

    private static int calcularFracciones(int minutosTotales, int divisor, int tolerancia){
        int unidadFraccion = 60 / divisor;
        int fraccionesCompletas = minutosTotales / unidadFraccion;
        int resto = minutosTotales % unidadFraccion;
        if (resto >= tolerancia){
            fraccionesCompletas++;
        }
        Logger.logBack("frc: " + fraccionesCompletas);
        Logger.logBack("rst: " + resto);
        Logger.logBack("tol: " + tolerancia);

        return fraccionesCompletas;
    }

}
