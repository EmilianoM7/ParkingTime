package com.example.parkingtime;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferenceManager {

    // Nombre del archivo de preferencias
    private static final String PREFS_NAME = "MisPreferencias";

    // Claves para las preferencias
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_FRACCION = "fraccion";
    private static final String KEY_TOLERANCIA = "tolerancia";
    private static final String KEY_TARIFA = "tarifa";

    // Instancia única (patrón Singleton)
    private static PreferenceManager instance;
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;

    // dafaults
    private static int fraccionDefault = 30;
    private static int toleranciaDefault = 5;
    private static int tarifaDefault = 3000;

    private PreferenceManager(Context context) {
        // Constructor privado para implementar Singleton
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized PreferenceManager getInstance(Context context) {
        // Obtiene la instancia única de PreferencesManager
        if (instance == null) {
            instance = new PreferenceManager(context.getApplicationContext());
        }
        return instance;
    }

    // ========== USUARIO-SESIÓN ==========

    // nombre de usuario
    public void setUsername(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public String getUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    // estado sesion
    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    // ========== CONFIG ==========

    // fracicon
    public void setFraccion(int fraccion) {
        editor.putInt(KEY_FRACCION, fraccion);
        editor.apply();
    }

    public int getFraccion() {
        int fraccion = sharedPreferences.getInt(KEY_FRACCION, -1);
        if (fraccion == -1) {
            setFraccion(fraccionDefault);
            return sharedPreferences.getInt(KEY_FRACCION, -1);
        }
        return fraccion;
    }

    // tolerancia
    public void setTolerancia(int tolerancia) {
        editor.putInt(KEY_TOLERANCIA, tolerancia);
        editor.apply();
    }

    public int getTolerancia() {
        int tolerancia = sharedPreferences.getInt(KEY_TOLERANCIA, -1);
        if (tolerancia == -1) {
            setTolerancia(toleranciaDefault);
            return sharedPreferences.getInt(KEY_TOLERANCIA, -1);
        }
        return tolerancia;
    }

    // tarifa
    public void setTarifa(int tarifa) {
        editor.putInt(KEY_TARIFA, tarifa);
        editor.apply();
    }

    public int getTarifa() {
        int tarifa = sharedPreferences.getInt(KEY_TARIFA, -1);
        if (tarifa == -1) {
            setTarifa(tarifaDefault);
            return sharedPreferences.getInt(KEY_TARIFA, -1);
        }
        return tarifa;
    }

    // ========== ADICIONAL ==========

    public void clearAll() {
        editor.clear();
        editor.apply();
    }

    public void remove(String key) {
        editor.remove(key);
        editor.apply();
    }

    public void logout() {
        setLoggedIn(false);
        setUsername("");
    }
}