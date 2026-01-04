package com.example.parkingtime;

public class MainBack {

    public static int[] incrementdores = {100,500,1000};
    public static int[] fracciones = {5,10,15,30,60};
    public static int[] tolerancias = {5,10,15,20,25};

    // calculos

    public static int getIndice(int[] vector, int elemento){
        for (int i = 0; i < vector.length; i++) {
            if (vector[i] == elemento){
                return i;
            }
        }
        return 0;
    }

    public static String calcularPrecioTotal(int mins, int fraccion, int precioHora, int tolerancia){
        // cantidad de fracciones completas
        int fracciones = calcularFracciones(mins, fraccion, tolerancia);
        //  factor que se multiplica por el precio de hora completa
        float factor =  (float) fracciones * fraccion / 60;

        Logger.logBack("calcular frcs: " + fracciones);
        Logger.logBack("calcular fraccion " + fraccion);
        Logger.logBack("ftr: " + factor);
        return  "" + (factor * precioHora);
    }

    public static int calcularMinutosTotales(int hIngreso, int mIngreso, int hSalida, int mSalida){
        int restaHoras = restarHorario(hIngreso, hSalida, 24) * 60;
        int restaMinutos = restarHorario(mIngreso,mSalida, 60);
        return restaHoras + restaMinutos;
    }

    public static String calcularTiempoCobrado(int minutos, int tolerancia, int fraccion){
        int fracciones = calcularFracciones(minutos, fraccion, tolerancia);
        int tiempoCobrado = fracciones * fraccion;
        return minutoAHora(tiempoCobrado);
    }

    public static String minutoAHora(int minutos){
        String r = "";
        r += (minutos / 60);
        r += ":";
        r += String.format("%02d", (minutos % 60));
        return r;
    }

    public static String enPesos(String pesos){
        return "$ " + pesos;
    }

    public static String formatHora(int hora, int minuto){
        return String.format("%02d", hora) + ":" + String.format("%02d", minuto);
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

    // internos

    private static int restarHorario (int ingreso, int salida, int limite){
        if (ingreso > salida){
            return salida + limite - ingreso;
        }
        return salida - ingreso;
    }

    public static int horaAnterior(int hora, int limite){
        if (hora == 0){
            return limite - 1;
        }
        return hora - 1;
    }

    private static int calcularFracciones(int minutosTotales, int fraccion, int tolerancia){
        int fraccionesCompletas = minutosTotales / fraccion;
        int resto = minutosTotales % fraccion;
        if (resto >= tolerancia){
            fraccionesCompletas++;
        }
        Logger.logBack("frc: " + fraccionesCompletas);
        Logger.logBack("rst: " + resto);
        Logger.logBack("tol: " + tolerancia);

        return fraccionesCompletas;
    }

}
