package com.example.parkingtime;

import android.util.Log;

public class Logger {

    public static void logMain(String msj ) {
        Log.d("EE",msj);
    }
    public static void logBack(String msj ) {
        Log.d("EE","Back: " + msj);
    }
    public static void logModulo(String msj ) {
        Log.d("EE","Modulo: " + msj);
    }
}
