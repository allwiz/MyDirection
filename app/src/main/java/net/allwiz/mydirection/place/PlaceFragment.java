/*
 * My Direction Android Application
 * @author   Eric(Jaewon) Lee
 * Copyright (C) 2021 Eric(Jaewon) Lee <allwiz@gmail.com>
 * This program is free software: you can redistribute it and/or modify it.
 */
package net.allwiz.mydirection.place;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.allwiz.mydirection.R;
import net.allwiz.mydirection.base.BaseFragment;
import net.allwiz.mydirection.database.DirectionItem;
import net.allwiz.mydirection.define.Action;
import net.allwiz.mydirection.define.Category;
import net.allwiz.mydirection.define.Command;
import net.allwiz.mydirection.define.Sort;
import net.allwiz.mydirection.util.LogEx;

import java.util.ArrayList;



/**
 * This Fragment presents places
 */
public class PlaceFragment extends BaseFragment implements PlaceAdapter.ItemClickListener {
    private static final String TAG = PlaceFragment.class.getSimpleName();

    private RecyclerView                mRecyclerView;
    private RecyclerView.LayoutManager  mLayoutManager;
    private PlaceAdapter                mPlaceAdapter;

    private ArrayList<DirectionItem>    mItems;
    private int                         mCategory;
    private long                        mLabelIndex = -1;



    public static PlaceFragment newInstance(int category, long labelIndex) {
        PlaceFragment placeFragment = new PlaceFragment();

        Bundle args = new Bundle();
        args.putInt(Command.Name.CATEGORY, category);
        args.putLong(Command.Name.LABEL_INDEX, labelIndex);
        placeFragment.setArguments(args);

        return placeFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place, container, false);

        setArguments();

        createRecyclerView(view);
        createPlaceItems();
        createPlaceAdapter();

        return view;
    }


    private void setArguments() {
        mCategory = getArguments().getInt(Command.Name.CATEGORY);
        mLabelIndex = getArguments().getLong(Command.Name.LABEL_INDEX);
    }


    private void createRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_place_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
    }


    private void createPlaceItems() {
        mItems = new ArrayList<DirectionItem>();
    }


    private void createPlaceAdapter() {

        int sortOrder = Sort.HIT_COUNT;
        if (mCategory == Category.TRAVEL) {
            sortOrder = Sort.NAME;
        }

        mItems = getDirectionDb().fetchDirectionItems(mCategory, mLabelIndex, sortOrder);

        mPlaceAdapter = new PlaceAdapter(getContext(), mItems);
        mRecyclerView.setAdapter(mPlaceAdapter);
        mPlaceAdapter.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(View view, int position) {
        DirectionItem item = mItems.get(position);
        if (item != null) {
            LogEx.d(TAG, String.format("increaseDirectionItemHitCount: %d", item.itemIndex));
            getDirectionDb().increaseDirectionItemHitCount(item.itemIndex);

            Uri gmmIntentUri = Uri.parse(getDirection(item));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        }
    }


    @Override
    public void onItemLongClick(View view, int position) {
        DirectionItem item = mItems.get(position);
        if (item != null) {
            showConfirmDeletePlaceAlertDialog(item);
        }
    }


    @Override
    public void onItemMoreClick(View view, int position) {
        LogEx.d(TAG, "Click More Item!!");

        mDirectionItem = mItems.get(position);
        if (mDirectionItem == null) {
            return;
        }


        PopupMenu menu = new PopupMenu(getContext(), view);
        menu.getMenuInflater().inflate(R.menu.menu_place, menu.getMenu());
        menu.show();

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item == null) {
                    return false;
                }

                if (mDirectionItem == null) {
                    Toast.makeText(getContext(),getContext().getString(R.string.message_no_item), Toast.LENGTH_SHORT).show();
                    return false;
                }

                int itemId = item.getItemId();
                switch (itemId) {
                    case R.id.menu_place_share:
                        sendAddressToOtherApps();
                        break;
                    case R.id.menu_place_copy:
                        copyAddress();
                        Toast.makeText(getContext(),getContext().getString(R.string.message_copy_clipboard), Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.menu_place_edit:
                        mListener.onFragmentDirectionItemControlListener(Action.Place.MODIFY, mDirectionItem);
                        break;
                    case R.id.menu_place_delete:
                        //Toast.makeText(getContext(),"Click Delete Item!!", Toast.LENGTH_SHORT).show();
                        showConfirmDeletePlaceAlertDialog(mDirectionItem);
                        break;
                }

                return false;
            }
        });

    }


    @Override
    public void refreshItems() {
        mItems = getDirectionDb().fetchDirectionItems(mCategory, mLabelIndex, Sort.HIT_COUNT);
        mPlaceAdapter.refresh(mItems);
    }
}

