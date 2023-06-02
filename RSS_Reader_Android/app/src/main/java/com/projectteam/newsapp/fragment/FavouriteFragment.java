package com.projectteam.newsapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.projectteam.newsapp.R;
import com.projectteam.newsapp.adapter.FavouriteNewsAdpater;
import com.projectteam.newsapp.helper.RssDbHelper;


public class FavouriteFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    View view;
    RecyclerView recyclerView;
    SwipeRefreshLayout refreshLayout;
    FavouriteNewsAdpater adpater;
    FloatingActionButton deleteforever;
    ImageView empty_data;
    TextView note01, note02;
    RssDbHelper db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_favourite, container, false);
        recyclerView = view.findViewById(R.id.fav_item);
        refreshLayout = view.findViewById(R.id.refreshFavourite);
        deleteforever = view.findViewById(R.id.Deleteallfavourite);
        empty_data = view.findViewById(R.id.imageView3);
        note01 = view.findViewById(R.id.Note03);
        note02 = view.findViewById(R.id.Note04);
        db =new RssDbHelper(requireActivity().getApplicationContext());
        adpater = new FavouriteNewsAdpater(getContext(), getActivity(),db.getFavourite());
        refreshLayout.setOnRefreshListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adpater);
        checkfavouriteavail();
        hidewhenscroll();
        deleteallfavourite();
        return view;
    }

    private void checkfavouriteavail() {
        if (adpater.getItemCount() == 0)
        {
            empty_data.setVisibility(View.VISIBLE);
            note01.setVisibility(View.VISIBLE);
            note02.setVisibility(View.VISIBLE);
        }
        else
        {
            empty_data.setVisibility(View.GONE);
            note01.setVisibility(View.GONE);
            note02.setVisibility(View.GONE);
        }
    }

    //this function will good bye all favourite records
    private void deleteallfavourite() {
        deleteforever.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity(), R.style.MaterialAlertDialog);
            builder.setTitle(R.string.confirm_title);
            String mesg1 = getString(R.string.confirm_del_all);
            builder.setMessage(mesg1);
            builder.setPositiveButton(R.string.yes_btn, (dialog, i) -> {
                //Xoa data
                MaterialAlertDialogBuilder builder2 = new MaterialAlertDialogBuilder(requireActivity());
                builder2.setTitle(R.string.confirm_title);
                if (adpater.getItemCount() == 0)
                {
                    String mesg2 = getString(R.string.No_database);
                    builder2.setMessage(mesg2);
                }
                else
                {
                    String mesg3 = getString(R.string.delete_all_fav_success);
                    builder2.setMessage(mesg3);
                }
                builder2.setPositiveButton("OK",null);
                builder2.show();
                db.truncate_fav();
                adpater.updateArray(db.getFavourite());
                recyclerView.setAdapter(adpater);
                empty_data.setVisibility(View.VISIBLE);
                note01.setVisibility(View.VISIBLE);
                note02.setVisibility(View.VISIBLE);
            });
            builder.setNegativeButton(R.string.dont_del_me, (dialog, i) -> {
                //just close dialog
                dialog.dismiss();
            });

            builder.show();
        });
    }

    private void hidewhenscroll() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
            {
                if (dy > 0 ||dy<0 && deleteforever.isShown())
                {
                    deleteforever.hide();
                }
            }
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState)
            {
                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    deleteforever.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        adpater.updateArray(db.getFavourite());
        recyclerView.setAdapter(adpater);
        checkfavouriteavail();
    }

    @Override
    public void onRefresh() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Your Code
            adpater.updateArray(db.getFavourite());
            recyclerView.setAdapter(adpater);
            checkfavouriteavail();
            refreshLayout.setRefreshing(false);
        }, 2000);
    }
}