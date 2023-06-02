package com.projectteam.newsapp.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.projectteam.newsapp.R;
import com.projectteam.newsapp.adapter.OfflineAdapter;

import java.io.File;

//This is for Android 11 and later because it will request this permission from user: MANAGER_EXTERNAL_STORAGE
public class Android11Offline extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    View view;
    TextView requesttext1, requesttext2, requesttext3, requesttext4, requesttext5;
    Button requestbtn;
    ImageView androidlogo;
    SwipeRefreshLayout swipeRefreshLayout;
    FloatingActionButton info;
    OfflineAdapter adapter;
    RecyclerView recyclerView;
    String base;
    File[] filesAndFolders;
    File root;
    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.android11_offline, container, false);
        recyclerView = view.findViewById(R.id.recycler_view11);
        swipeRefreshLayout = view.findViewById(R.id.offlineAndroid11);
        ////////////////////////////////////////////////////
        //text information
        androidlogo = view.findViewById(R.id.imageAndroid);
        requesttext1 = view.findViewById(R.id.requesttext1);
        requesttext2 = view.findViewById(R.id.requesttext2);
        requesttext3 = view.findViewById(R.id.requesttext3);
        requesttext4 = view.findViewById(R.id.requesttext4);
        requesttext5 = view.findViewById(R.id.requesttext5);
        requestbtn = view.findViewById(R.id.requestbtn);
        ////////////////////////////////////////////////////
        info = view.findViewById(R.id.InfoButton);
        swipeRefreshLayout.setOnRefreshListener(this);
        //Replace with new method for android 11 and higher.
        //check permission is granted or not
        if (Environment.isExternalStorageManager())
        {
            //when permission is granted
            showandhide_request();
            base = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "//RSS";
            root = new File(base);
            filesAndFolders = root.listFiles();
            adapter = new OfflineAdapter(getContext(),filesAndFolders);
        } else {
            //request for the permission
            showandhide_request();
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        showandhide_request();
        hidewhenscroll();
        //for user read
        loadInformation();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void showandhide_request() {
        if (Environment.isExternalStorageManager())
        {
            androidlogo.setVisibility(View.GONE);
            requesttext1.setVisibility(View.GONE);
            requesttext2.setVisibility(View.GONE);
            requesttext3.setVisibility(View.GONE);
            requesttext4.setVisibility(View.GONE);
            requesttext5.setVisibility(View.GONE);
            requestbtn.setVisibility(View.GONE);
        }
        else
        {
            androidlogo.setVisibility(View.VISIBLE);
            requesttext1.setVisibility(View.VISIBLE);
            requesttext2.setVisibility(View.VISIBLE);
            requesttext3.setVisibility(View.VISIBLE);
            requesttext4.setVisibility(View.VISIBLE);
            requesttext5.setVisibility(View.VISIBLE);
            requestbtn.setVisibility(View.VISIBLE);
            requsetPermission();
        }
    }

    private void hidewhenscroll() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 0 ||dy<0 && info.isShown())
                {
                   info.hide();
                }
            }
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
            {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    info.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void loadInformation() {
        info.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder0 = new MaterialAlertDialogBuilder(requireActivity(), R.style.MaterialAlertDialog);
            builder0.setTitle(R.string.Notify_offline_information);
            String info1 = getString(R.string.question1) + "\n";
            String answer1 = "--> " + getString(R.string.offline_path) + "\n";
            String info2 = getString(R.string.question2) + "\n";
            String answer2 = "--> " + getString(R.string.anwser2) + "\n";
            String info3 = getString(R.string.question3)+ "\n";
            String answer3 = "--> " + getString(R.string.answer3);
            builder0.setMessage(info1 + answer1 + info2 + answer2 + info3 + answer3);
            builder0.setNegativeButton("OK", (dialog, which) -> dialog.dismiss());
            builder0.show();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requsetPermission() {
        requestbtn.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity(), R.style.MaterialAlertDialog);
            builder.setTitle("Android 11+");
            String info6 = getString(R.string.request_info6) +"\n";
            builder.setMessage(info6);
            builder.setPositiveButton("OK", (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            });
            builder.show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRefresh() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Your Code
            recyclerView.setAdapter(adapter);
            swipeRefreshLayout.setRefreshing(false);
        }, 2000);
    }
}