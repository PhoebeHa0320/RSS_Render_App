package com.projectteam.newsapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.projectteam.newsapp.R;
import com.projectteam.newsapp.adapter.ViewFragmentAdpater;
import com.projectteam.newsapp.helper.RssDbHelper;
import com.projectteam.newsapp.prefmanager.CheckNetworkConnection;
import com.projectteam.newsapp.prefmanager.DarkModePrefManager;
import com.projectteam.newsapp.prefmanager.LanguagePrefManager;
import com.projectteam.newsapp.transfomer.DepthPageTransformer;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {
    ///////////////////////////////////////////////////////////////////////////////
    BottomNavigationView bottomNavigationView;
    MaterialToolbar topAppBar;
    ViewPager2 viewPager2;
    ViewFragmentAdpater adpater ;
    private static final int PERMISSION_REQUEST_CODE = 7;
    DepthPageTransformer depthPageTransformer = new DepthPageTransformer();
    LanguagePrefManager languagePrefManager;
    ///////////////////////////////////////////////////////////////////////////////
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        languagePrefManager = new LanguagePrefManager(getBaseContext());
        super.onCreate(savedInstanceState);
        languagePrefManager.setLocal(languagePrefManager.getLang());
        languagePrefManager.loadLocal();
        ///////////////////////////////////////////////////////////////////////////////
        /*
        Giai thich:
        Mac dinh khi khoi dong, app se chay dang light theme
        Nhung khi thay doi trong setting, app se sang che do toi va nguoc lai
        Thiet lap se dc luu cho cac lan chay tiep theo
        */
        ///////////////////////////////////////////////////////////////////////////////
        /*Test Multi Language Support*/
        Log.d("System","getDisplayLanguage = " + Locale.getDefault().getDisplayLanguage());
        Log.d("System","getLanguage = " + Locale.getDefault().getLanguage());
        Log.d("System","Resources.getLanguage = " + Resources.getSystem().getConfiguration().locale.getLanguage());
        Log.d("System","getResources.getLanguage = " + getResources().getConfiguration().locale);
        //////////////////////////////////////////////////////////////////////////////
        /*update database follow language*/
        updateDatabase();
        /////////////////////////////////////////////////////////////////////////////
        if (new DarkModePrefManager(this).isNightMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        ///////////////////////////////////////////////////////////////////////////////
        setContentView(R.layout.activity_main);
        ///////////////////////////////////////////////////////////////////////////////
        /*Create folder News in download folder at internal storage */
        /*First, asking to request permission form user*/
        /*This is for Android 10+*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            //in Android 10+, we do not need WRITE_EXTERNAL_STORAGE permission due security policy from Google
            createDirectory();
        }
        /*for old API*/
        else
        {
            /*have permission, create folder*/
            /*bad news, we can't use WRITE_EXTERNAL_STORAGE to create folder directory in internal storage on API 29+ */
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                createfolder();
            }
            else
            {
                requestpermission();
            }
        }

        ///////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////////////////
        checkConnection();
        ///////////////////////////////////////////////////////////////////////////////
        viewPager2 = findViewById(R.id.viewpage);
        bottomNavigationView = findViewById(R.id.BottonNavView);
        topAppBar = findViewById(R.id.topAppBar);
        adpater = new ViewFragmentAdpater(this);
        viewPager2.setOffscreenPageLimit(1);
        viewPager2.setUserInputEnabled(false);
        viewPager2.setPageTransformer(depthPageTransformer);
        viewPager2.setAdapter(adpater);
        ///////////////////////////////////////////////////////////////////////////////
        topAppBar.setOnMenuItemClickListener(mitem -> {
            int id = mitem.getItemId();
            if (id == R.id.settings)
            {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
            return true;
        });
        ///////////////////////////////////////////////////////////////////////////////
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home)
            {
                viewPager2.setCurrentItem(0);
            }
            else if (id == R.id.favourite)
            {
                viewPager2.setCurrentItem(1);
            }
            else if (id == R.id.source)
            {
                viewPager2.setCurrentItem(2);
            }
            else if (id == R.id.offline)
            {
                viewPager2.setCurrentItem(3);
            }
            return true;
        });
        ///////////////////////////////////////////////////////////////////////////////
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position)
                {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.favourite).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.source).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.offline).setChecked(true);
                        break;
                }
            }
        });
    }

    private void updateDatabase() {
        RssDbHelper db = new RssDbHelper(getBaseContext());
        String[] rss_type = getResources().getStringArray(R.array.title_list);
        db.updateTranslateTitle(rss_type);
    }
    ///////////////////////////////////////////////////////////////////////////////
    private void createfolder() {
        File file = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"//RSS");
        boolean success = true;
        if (!file.exists())
        {
            success = file.mkdir();
            Log.i("Create folder download offline", "Folder not available, create it");
        }
        if (success)
        {
            Log.i("Create folder download offline", "Success");
        }
        else
        {
            Log.e("Create folder download offline", "Failed");
        }
    }
    ///////////////////////////////////////////////////////////////////////////////
    /*for android 10+*/
    private void createDirectory()
    {
        File file = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"//RSS");
        boolean sucess = true;
        if (!file.exists())
        {
            sucess = file.mkdirs();
            Log.i("Create folder download offline", "Folder not available, create it");
        }
        else
        {
            Log.i("Create folder download offline", "Folder availabled");
        }
        if (sucess)
        {
            Log.i("Create folder download offline", "Success");
        }
        else
        {
            Log.e("Create folder download offline", "Failed");
        }
    }
    ///////////////////////////////////////////////////////////////////////////////
    private void requestpermission()
    {
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
    }
    ///////////////////////////////////////////////////////////////////////////////

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                createfolder();
            }else
            {
                Toast.makeText(MainActivity.this, R.string.permission_denied_by_user,Toast.LENGTH_SHORT).show();
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    ///////////////////////////////////////////////////////////////////////////////
    private void checkConnection() {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            //Background work here
            handler.post(() -> {
                //UI Thread work here
                if (CheckNetworkConnection.CheckConnection(this))
                {
                    Toast.makeText(this, R.string.InternetSuccess, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this, R.string.InternetError, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    //exit application
    @Override
    public void onBackPressed() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog);
        builder.setTitle(R.string.ExitTitle);
        builder.setMessage(R.string.Confirm_exit);
        builder.setPositiveButton(R.string.yes_btn, (dialog, which) -> System.exit(0));
        builder.setNegativeButton(R.string.dont_del_me, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

}




    
