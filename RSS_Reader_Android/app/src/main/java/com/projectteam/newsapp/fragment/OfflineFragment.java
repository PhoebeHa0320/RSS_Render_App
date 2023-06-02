package com.projectteam.newsapp.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.projectteam.newsapp.R;
import com.projectteam.newsapp.adapter.OfflineAdapter;

import java.io.File;


public class OfflineFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    View view;
    SwipeRefreshLayout swipeRefreshLayoutOffline;
    FloatingActionButton infobtn;
    OfflineAdapter adapter;
    RecyclerView recyclerView;
    String base;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_offline, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        swipeRefreshLayoutOffline = view.findViewById(R.id.swipeOffline);
        infobtn = view.findViewById(R.id.infoButton0);
        swipeRefreshLayoutOffline.setOnRefreshListener(this);
        //test result, this line only work with API 29 and lower.
        base = Environment.getExternalStorageDirectory().getAbsolutePath() +"/Download/RSS";
        File root = new File(base);
        File[] filesAndFolders = root.listFiles();

        adapter = new OfflineAdapter(getContext(),filesAndFolders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        hidewhenscroll();
        //for user read
        loadInformation();
        return view;
    }

    private void hidewhenscroll() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 0 ||dy<0 && infobtn.isShown())
                {
                    infobtn.hide();
                }
            }
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
            {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    infobtn.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void loadInformation() {
        infobtn.setOnClickListener(v -> {
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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onRefresh() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Your Code
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            swipeRefreshLayoutOffline.setRefreshing(false);
        }, 2000);
    }
}