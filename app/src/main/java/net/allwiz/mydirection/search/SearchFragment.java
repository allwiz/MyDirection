/*
 * My Direction Android Application
 * @author   Eric(Jaewon) Lee
 * Copyright (C) 2021 Eric(Jaewon) Lee <allwiz@gmail.com>
 * This program is free software: you can redistribute it and/or modify it.
 */
package net.allwiz.mydirection.search;

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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.allwiz.mydirection.R;
import net.allwiz.mydirection.base.BaseFragment;
import net.allwiz.mydirection.database.DirectionItem;
import net.allwiz.mydirection.define.Action;
import net.allwiz.mydirection.define.Command;
import net.allwiz.mydirection.define.Sort;
import net.allwiz.mydirection.util.LogEx;

import java.util.ArrayList;

/**
 * This Fragment presents search results
 */
public class SearchFragment extends BaseFragment implements SearchAdapter.ItemClickListener {
    private static final String TAG = SearchFragment.class.getSimpleName();

    private RecyclerView                mRecyclerView;
    private RecyclerView.LayoutManager  mLayoutManager;
    private String                      mQuery;

    private SearchAdapter               mSearchAdapter;
    private ArrayList<DirectionItem>    mItems;

    public static SearchFragment newInstance(String query) {
        SearchFragment searchFragment = new SearchFragment();

        Bundle args = new Bundle();
        args.putString(Command.Name.QUERY, query);
        searchFragment.setArguments(args);

        return searchFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        setArguments();

        createRecyclerView(view);
        createSearchItems();
        createSearchAdapter(view);

        return view;
    }


    private void setArguments() {
        mQuery = getArguments().getString(Command.Name.QUERY);
    }


    private void createRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_search_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    private void createSearchItems() {
        mItems = new ArrayList<DirectionItem>();
    }


    private void createSearchAdapter(View view) {
        mItems = getDirectionDb().fetchDirectionItems(mQuery, Sort.HIT_COUNT);

        mSearchAdapter = new SearchAdapter(getContext(), mItems);
        mRecyclerView.setAdapter(mSearchAdapter);
        mSearchAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        DirectionItem item = mItems.get(position);
        if (item != null) {
            //mControlListener.onPlaceFragmentControlListener(getDirection(item));

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
    }

    @Override
    public void onItemMoreClick(View view, int position) {
        mDirectionItem = mItems.get(position);
        if (mDirectionItem == null) {
            return;
        }

        PopupMenu menu = new PopupMenu(getContext(), view);
        menu.getMenuInflater().inflate(R.menu.menu_place, menu.getMenu());

        MenuItem editItem = menu.getMenu().findItem(R.id.menu_place_edit);
        if (editItem != null) {
            editItem.setVisible(false);
        }
        MenuItem deleteItem = menu.getMenu().findItem(R.id.menu_place_delete);
        if (deleteItem != null) {
            deleteItem.setVisible(false);
        }

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
/*
    private String getDirection(DirectionItem item) {
        String nav = String.format("google.navigation:q=%s&%s", item.address.replaceAll(" ", "+"), item.mode);
        if (!item.avoid.isEmpty()) {
            nav += String.format("&avoid=%s", item.avoid);
        }

        LogEx.d(TAG, nav);
        return nav;
    }
*/
}
