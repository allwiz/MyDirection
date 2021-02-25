/*
 * My Direction Android Application
 * @author   Eric(Jaewon) Lee
 * Copyright (C) 2021 Eric(Jaewon) Lee <allwiz@gmail.com>
 * This program is free software: you can redistribute it and/or modify it.
 */
package net.allwiz.mydirection.tab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import net.allwiz.mydirection.R;
import net.allwiz.mydirection.base.BaseFragment;
import net.allwiz.mydirection.database.DirectionItem;
import net.allwiz.mydirection.define.Category;
import net.allwiz.mydirection.favorite.FavoriteFragment;
import net.allwiz.mydirection.travel.TravelFragment;
import net.allwiz.mydirection.recent.RecentFragment;
import net.allwiz.mydirection.util.LogEx;

import java.util.ArrayList;
import java.util.List;


/**
 * This Fragment manages TabLayout with RecentFragment, FavoriteFragment, and TravelFragment.
 */
public class HostFragment extends BaseFragment {
    private static final String TAG = HostFragment.class.getSimpleName();

    private HostFragmentControlListener     mListener;

    private ViewPager           mViewPager;
    private HostPagerAdapter    mPagerAdapter;
    private TabLayout           mTabLayout;

    private List<Fragment>      mFragments;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_host, container, false);

        setControlListener();

        createFragments(view);
        setViewPager(view);
        createTabLayout(view);

        return view;
    }


    private void setControlListener() {
        try {
            mListener = (HostFragmentControlListener) getActivity();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }


    public interface HostFragmentControlListener {
        public void onHostFragmentControlListener(int screen);
    }


    private void createFragments(View view) {
        mFragments = new ArrayList<Fragment>();

        mFragments.add(new RecentFragment());
        mFragments.add(new FavoriteFragment());
        mFragments.add(new TravelFragment());
    }


    private void setViewPager(View view) {
        mViewPager = (ViewPager) view.findViewById(R.id.fragment_host_view_pager);
        mPagerAdapter = new HostPagerAdapter(getContext(), getChildFragmentManager(), getFragments());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

    }


    public void setViewPager(int position) {
        mViewPager.setCurrentItem(position);
    }

    //https://stackoverflow.com/questions/34959298/android-material-design-click-event-on-tabs?lq=1
    private void createTabLayout(View view) {
        mTabLayout = (TabLayout) view.findViewById(R.id.fragment_host_tab_layout);

        mTabLayout.setupWithViewPager(mViewPager);


        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mListener.onHostFragmentControlListener(tab.getPosition());

                int position = tab.getPosition();
                mViewPager.setCurrentItem(position);
                LogEx.d(TAG, "TAB CLICK: " + position);
                if (position == Category.RECENT) {
                    refreshItems(Category.RECENT);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }


    private List<Fragment> getFragments() {
        return mFragments;
    }


    public RecentFragment getRecentFragment() {
        return (RecentFragment)mPagerAdapter.getItem(Category.RECENT);
    }

    public FavoriteFragment getFavoriteFragment() {
        return (FavoriteFragment)mPagerAdapter.getItem(Category.FAVORITE);
    }

    public TravelFragment getTravelFragment() {
        return (TravelFragment)mPagerAdapter.getItem(Category.TRAVEL);
    }

    public void refreshRecent() {
        getRecentFragment().refreshItems();
    }

    public void refreshFavorite() {
        getFavoriteFragment().refreshItems();
    }


    public void refreshTravel() {
        getTravelFragment().refreshItems();
    }


    public void refreshItems(int category) {
        if (category == Category.RECENT) {
            refreshRecent();
        } else if (category == Category.FAVORITE) {
            refreshFavorite();
        } else if (category == Category.TRAVEL) {
            refreshTravel();
        }
    }


    public int getCurrentFragmentItem() {
        return mViewPager.getCurrentItem();
    }


    /**
     * Add Direction
     * @param item      Direction information
     */
    @Override
    public void addDirectionItem(DirectionItem item) {
        super.addDirectionItem(item);

        if (item.category == Category.FAVORITE) {
            getFavoriteFragment().refreshItems();
        } else if (item.category == Category.TRAVEL) {
            getTravelFragment().refreshItems();
        }
    }
}
