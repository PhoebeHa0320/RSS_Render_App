package com.projectteam.newsapp.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.projectteam.newsapp.R;
import com.projectteam.newsapp.helper.RssDbHelper;
import com.projectteam.newsapp.prefmanager.DarkModePrefManager;
import com.projectteam.newsapp.prefmanager.LanguagePrefManager;

public class SettingsActivity extends AppCompatActivity {

    SwitchCompat darkModeSwitch;
    LanguagePrefManager languagePrefManager;
    TextView language;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        languagePrefManager = new LanguagePrefManager(getBaseContext());

        if(new DarkModePrefManager(this).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }

        super.onCreate(savedInstanceState);
        languagePrefManager.loadLocal();
        setContentView(R.layout.settings_activity);

        language = findViewById(R.id.language);
        ImageButton backbutton = findViewById(R.id.backbutton);
        backbutton.setOnClickListener(v -> {
            onBackPressed();
            ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            this.finish();
        });

        TextView aboutbtn = findViewById(R.id.aboutbtn);
        Intent intent = new Intent(SettingsActivity.this, AboutActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        aboutbtn.setOnClickListener(v ->
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle()));
        //function for enabling dark mode
        setDarkModeSwitch();
        //change language application
        changeLanguageApplication();
    }

    private void changeLanguageApplication() {
        final String[] list_language = getResources().getStringArray(R.array.language_list);
        language.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(SettingsActivity.this, R.style.MaterialAlertDialog);
            String title_dialog = getString(R.string.Choose_language);
            builder.setTitle(title_dialog);
            builder.setSingleChoiceItems(list_language, -1, (dialog, i) -> {
                LanguagePrefManager languagePrefManager = new LanguagePrefManager(SettingsActivity.this);
                if (i == 0)
                {
                    String default_local = Resources.getSystem().getConfiguration().locale.getLanguage();
                    languagePrefManager.setLocal(default_local);
                }
                else if (i == 1)
                {
                    //English
                    languagePrefManager.setLocal("en");
                }
                else
                {
                    //Vietnamese
                    languagePrefManager.setLocal("vi");
                }
                updateDatabase();
                recreate();
                dialog.dismiss();
                RestartApplication();
            });
            builder.show();
        });
    }

    private void RestartApplication() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    private void updateDatabase() {
        RssDbHelper db = new RssDbHelper(getBaseContext());
        String[] rss_type = getResources().getStringArray(R.array.title_list);
        db.updateTranslateTitle(rss_type);
    }

    private void setDarkModeSwitch(){
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        darkModeSwitch.setChecked(new DarkModePrefManager(this).isNightMode());
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            DarkModePrefManager darkModePrefManager = new DarkModePrefManager(SettingsActivity.this);
            darkModePrefManager.setDarkMode(!darkModePrefManager.isNightMode());
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            recreate();
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

}
