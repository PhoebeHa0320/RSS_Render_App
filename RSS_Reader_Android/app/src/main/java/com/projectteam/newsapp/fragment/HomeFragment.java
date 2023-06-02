package com.projectteam.newsapp.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.gson.Gson;
import com.projectteam.newsapp.R;
import com.projectteam.newsapp.RSSFeed.HTTPDataHandler;
import com.projectteam.newsapp.RSSFeed.RSSObject;
import com.projectteam.newsapp.adapter.FeedAdapter;
import com.projectteam.newsapp.helper.RssDbHelper;
import com.projectteam.newsapp.prefmanager.CheckNetworkConnection;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    FloatingActionButton sourceOpt;
    TextView internetError, requesttxt;
    private SwipeRefreshLayout refreshLayout;
    MaterialAutoCompleteTextView spinner_title, spinner_rss;
    ImageView errorImg;
    Button retrybtn;
    View view;
    ///////////////////////////////////////////////////////////////////////////////
    boolean isALLFloatbtn;
    ///////////////////////////////////////////////////////////////////////////////
    RecyclerView recyclerView;
    RSSObject rssObject ;
    RssDbHelper rssDbHelper;
    ///////////////////////////////////////////////////////////////////////////////
    Button okbtn;
    ArrayAdapter<String> rsstitle;
    ArrayAdapter<String> rssname;
    ///////////////////////////////////////////////////////////////////////////////
    private String RSS_link;

    ///////////////////////////////////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ///////////////////////////////////////////////////////////////////////////////
        recyclerView = view.findViewById(R.id.recycleview);
        LinearLayoutManager linearLayoutManager  = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        ///////////////////////////////////////////////////////////////////////////////
        //Extended Float Button
        sourceOpt = view.findViewById(R.id.extendedBtn);
        ///////////////////////////////////////////////////////////////////////////////
        //Internet Error Message
        errorImg = view.findViewById(R.id.errorImage);
        internetError = view.findViewById(R.id.ErrorMesseage);
        requesttxt = view.findViewById(R.id.RequestText);
        retrybtn = view.findViewById(R.id.RetryButton);
        ///////////////////////////////////////////////////////////////////////////////
        //Swipe Refresh layout
        refreshLayout = view.findViewById(R.id.refreshLayout);
        ///////////////////////////////////////////////////////////////////////////////
        //Hide all float button and Extended Float Button
        isALLFloatbtn = false;
        ///////////////////////////////////////////////////////////////////////////////
        //Checking Internet connection
        rssDbHelper = new RssDbHelper(view.getContext());
        ///////////////////////////////////////////////////////////////////////////////
        hidewhenscroll();
        loadRSSDefault();
        checkConnection();
        refreshLayout.setOnRefreshListener(this);
        showOptionSelect();
        ///////////////////////////////////////////////////////////////////////////////
        return view;
    }
    ///////////////////////////////////////////////////////////////////////////////
    private void hidewhenscroll() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 0 ||dy<0 && sourceOpt.isShown())
                {
                    sourceOpt.hide();
                }
            }
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
            {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    sourceOpt.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }
    ///////////////////////////////////////////////////////////////////////////////
    private void loadRSSDefault() {
        //set URL by default with id = 1
        Cursor result = rssDbHelper.firstRSSList();
        if (result.moveToFirst())
        {
            RSS_link = result.getString(3);
        }
        result.close();
    }
    ///////////////////////////////////////////////////////////////////////////////
    //load rss
    private void loadRSS() {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<String,String,String> loadRSSAsync = new AsyncTask<String, String, String>() {
            final ProgressDialog mDialog = new ProgressDialog(getActivity());
            @Override
            protected void onPreExecute() {
                mDialog.setMessage("Vui lòng chờ...");
                mDialog.show();
            }
            @Override
            protected String doInBackground(String... params) {
                String result;
                HTTPDataHandler http = new HTTPDataHandler();
                result = http.GetHTTPData(params[0]);
                return  result;
            }
            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void onPostExecute(String s) {
                mDialog.dismiss();
                rssObject = new Gson().fromJson(s, RSSObject.class);
                FeedAdapter adapter = new FeedAdapter(rssObject,getContext());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                Activity activity = mDialog.getOwnerActivity();
                if( activity!=null && !activity.isFinishing()) {
                    mDialog.dismiss();
                }
            }

        };
        ///////////////////////////////////////////////////////////////////////////////
        try {
            String RSS_to_Json_API = "https://api.rss2json.com/v1/api.json?rss_url=";
            loadRSSAsync.execute(RSS_to_Json_API + RSS_link);
        } catch (NullPointerException e) {
            e.printStackTrace();
            recyclerView.setVisibility(View.GONE);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////
    private void loaddefaultbtn() {
        sourceOpt.setVisibility(View.VISIBLE);
    }
    ///////////////////////////////////////////////////////////////////////////////
    private void checkConnection() {
        if (CheckNetworkConnection.CheckConnection(requireActivity()))
        {
            errorImg.setVisibility(View.GONE);
            internetError.setVisibility(View.GONE);
            requesttxt.setVisibility(View.GONE);
            retrybtn.setVisibility(View.GONE);
            loaddefaultbtn();
            loadRSS();
        }
        else
        {
            errorImg.setVisibility(View.VISIBLE);
            internetError.setVisibility(View.VISIBLE);
            requesttxt.setVisibility(View.VISIBLE);
            retrybtn.setVisibility(View.VISIBLE);
            sourceOpt.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            retryConnectNetwork();
        }

    }
    ///////////////////////////////////////////////////////////////////////////////
    private void retryConnectNetwork() {
        Handler handler2 = new Handler(Looper.getMainLooper());
        retrybtn.setOnClickListener(v -> new Thread(() -> {
            try
            {
                Thread.sleep(2000);

                    handler2.post(() -> {
                        if (CheckNetworkConnection.CheckConnection(requireActivity()))
                        {
                            errorImg.setVisibility(View.GONE);
                            internetError.setVisibility(View.GONE);
                            requesttxt.setVisibility(View.GONE);
                            retrybtn.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            sourceOpt.setVisibility(View.VISIBLE);
                            loaddefaultbtn();
                            loadRSS();
                        }
                        else
                        {
                            errorImg.setVisibility(View.VISIBLE);
                            internetError.setVisibility(View.VISIBLE);
                            requesttxt.setVisibility(View.VISIBLE);
                            retrybtn.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }
                    });
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }).start());
    }
    ///////////////////////////////////////////////////////////////////////////////
    private void showOptionSelect() {
        sourceOpt.setOnClickListener(v -> {
            BottomSheetDialog dialog = new BottomSheetDialog(requireActivity());
            dialog.setContentView(R.layout.choose_rss);
            dialog.show();
            spinner_title = dialog.findViewById(R.id.title_spinner);
            spinner_rss = dialog.findViewById(R.id.spinner_rss);
            String[] rss_type = getResources().getStringArray(R.array.title_list);
            rsstitle = new ArrayAdapter<>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,rss_type);
            spinner_title.setAdapter(rsstitle);
            okbtn = dialog.findViewById(R.id.btnLoad);
            spinner_title.setOnItemClickListener((parent, view, position, id) -> {
                String getTitle = spinner_title.getText().toString();
                Toast.makeText(getActivity(), getTitle, Toast.LENGTH_SHORT).show();
                ArrayList<String> listname = rssDbHelper.getRecords(getTitle);
                rssname = new ArrayAdapter<>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, listname);
                spinner_rss.setAdapter(rssname);
            });
            spinner_rss.setOnItemClickListener((parent, view, position, id) -> {
                String getTitle = spinner_title.getText().toString();
                String getURL = spinner_rss.getText().toString();
                Cursor result = rssDbHelper.getURL(getTitle, getURL);
                if (result.moveToFirst())
                {
                    RSS_link = result.getString(3);
                }
                result.close();
            });
            okbtn.setOnClickListener(v1 -> {
                loadRSS();
                dialog.dismiss();
            });
        });

    }
    ///////////////////////////////////////////////////////////////////////////////
    @Override
    public void onRefresh() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Your Code
            loaddefaultbtn();
            loadRSSDefault();
            loadRSS();
            checkConnection();
            refreshLayout.setRefreshing(false);
        }, 2000);
    }
}