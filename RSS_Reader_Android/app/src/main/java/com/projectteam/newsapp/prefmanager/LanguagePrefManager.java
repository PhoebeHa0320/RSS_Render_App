package com.projectteam.newsapp.prefmanager;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;


public class LanguagePrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;



    // Shared preferences file name
    private static final String PREF_NAME = "language";
    private static final String IS_CHANGE_LANGUAGE = "isChangeLanguage";

    public LanguagePrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setLocal(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        _context.getResources().updateConfiguration(configuration, _context.getResources().getDisplayMetrics());
        //Save config by shared preferences
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("My_lang", lang);
        editor.apply();
    }

    public String getLang()
    {
        return pref.getString("My_lang", "");
    }

    public void loadLocal () {
        SharedPreferences preferences = pref;
        String language = preferences.getString("My_lang", "");
        setLocal(language);

    }
}