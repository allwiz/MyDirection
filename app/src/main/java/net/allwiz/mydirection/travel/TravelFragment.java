/*
 * My Direction Android Application
 * @author   Eric(Jaewon) Lee
 * Copyright (C) 2021 Eric(Jaewon) Lee <allwiz@gmail.com>
 * This program is free software: you can redistribute it and/or modify it.
 */
package net.allwiz.mydirection.travel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.allwiz.mydirection.BuildConfig;
import net.allwiz.mydirection.R;
import net.allwiz.mydirection.base.BaseFragment;
import net.allwiz.mydirection.database.LabelDescItem;
import net.allwiz.mydirection.define.Category;
import net.allwiz.mydirection.define.Sort;
import net.allwiz.mydirection.util.LogEx;

import java.util.ArrayList;

public class TravelFragment extends BaseFragment implements TravelAdapter.ItemClickListener {
    private static final String TAG = TravelFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager  mLayoutManager;
    private TravelAdapter mLabelAdapter;
    private ArrayList<LabelDescItem> mItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_travel, container, false);

        createRecyclerView(view);
        //createLayoutManager(view);
        createLabelItems();
        createLabelAdapter(view);

        return view;
    }


    @Override
    public void refreshItems() {
        mItems = getDirectionDb().fetchLabelDescNameItems(Category.TRAVEL, Sort.NAME);

        if (BuildConfig.DEBUG) {
            for (LabelDescItem i : mItems) {
                LogEx.d(TAG, String.format("CATEGORY: %d, LABEL NAME: %s, PLACE NAMES: %s, COUNT: %d", i.category, i.name, i.itemNames, i.itemCount));
            }
        }

        mLabelAdapter.refresh(mItems);
    }


    private void createRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_travel_recycler_view);
        //mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
    }

/*
    private void createLayoutManager(View view) {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }
*/
    private void createLabelItems() {
        mItems = new ArrayList<LabelDescItem>();
    }

    private void createLabelAdapter(View view) {

        //mItems = getDirectionDb().fetchLabelDescNameItems(Category.TRAVEL);
        mItems = getDirectionDb().fetchLabelDescNameItems(Category.TRAVEL, Sort.NAME);
        mLabelAdapter = new TravelAdapter(getContext(), mItems);
        //mLabelAdapter = new TravelLabelAdapter(getContext(), mItems, Category.TRAVEL);

        mRecyclerView.setAdapter(mLabelAdapter);

        mLabelAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        LabelDescItem item = mItems.get(position);
        if (item != null) {
            LogEx.d(TAG, String.format("[SELECTED] CATEGORY: %d, LABEL INDEX: %d, LABEL NAME: %s", item.category, item.labelIndex, item.name));
            mListener.onFragmentLabelItemControlListener(item.category, item.name, item.labelIndex);
        }
    }

    @Override
    public void onItemLongClick(View view, int position) {
        LabelDescItem item = mItems.get(position);
        if (item != null) {
            LogEx.d(TAG, String.format("[SELECTED] CATEGORY: %d, LABEL INDEX: %d, LABEL NAME: %s", item.category, item.labelIndex, item.name));
        }
    }
}

