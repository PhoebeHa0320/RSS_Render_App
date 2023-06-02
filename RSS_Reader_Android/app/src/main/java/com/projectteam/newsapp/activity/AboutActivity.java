package com.projectteam.newsapp.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.projectteam.newsapp.R;
import com.projectteam.newsapp.prefmanager.DarkModePrefManager;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(new DarkModePrefManager(this).isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        ImageButton backbutton = findViewById(R.id.backbutton2);
        backbutton.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });

        setName();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @SuppressLint("SetTextI18n")
    private void setName() {

        TextView teamname = findViewById(R.id.teamname);
        TextView member = findViewById(R.id.MemberName);
        teamname.setText(R.string.Team_name);
        member.setText("1. Vũ Quốc Hung \n\n" +
                "2. Nguyễn Ngọc Hoàng Hà \n\n" +
                "3. Đỗ Bá Sơn \n\n" +
                "4. Ngô Thành Long \n\n" +
                "5. Trịnh Tiến Anh \n\n");
    }
}