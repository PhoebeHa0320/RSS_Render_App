package com.projectteam.newsapp.adapter;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.projectteam.newsapp.fragment.Android11Offline;
import com.projectteam.newsapp.fragment.FavouriteFragment;
import com.projectteam.newsapp.fragment.HomeFragment;
import com.projectteam.newsapp.fragment.OfflineFragment;
import com.projectteam.newsapp.fragment.SourceFragment;

public class ViewFragmentAdpater extends FragmentStateAdapter {

    HomeFragment homeFragment = new HomeFragment();
    FavouriteFragment favouriteFragment = new FavouriteFragment();
    OfflineFragment offlineFragment = new OfflineFragment();
    Android11Offline android11Offline = new Android11Offline();
    SourceFragment sourceFragment = new SourceFragment();

    public ViewFragmentAdpater(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 0:
                return homeFragment;
            case 1:
                return favouriteFragment;
            case 2:
                return sourceFragment;
            case 3:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                {
                    return android11Offline;
                }
                else
                {
                    return offlineFragment;
                }
        }
        return  homeFragment;
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
