/*
* My Direction Android Application
* @author   Eric(Jaewon) Lee
* Copyright (C) 2021 Eric(Jaewon) Lee <allwiz@gmail.com>
* This program is free software: you can redistribute it and/or modify it.
*/
package net.allwiz.mydirection.recent;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.allwiz.mydirection.R;
import net.allwiz.mydirection.base.BaseFragment;
import net.allwiz.mydirection.database.DirectionItem;
import net.allwiz.mydirection.define.Action;
import net.allwiz.mydirection.define.Sort;

import java.util.ArrayList;

/**
 * This Fragment presents recent places
 */
public class RecentFragment extends BaseFragment implements RecentAdapter.ItemClickListener {
    private static final String TAG = RecentFragment.class.getSimpleName();

    private Button                      mHomeButton;
    private Button                      mWorkButton;
    private Button                      mStarredButton;

    private RecyclerView                mRecyclerView;
    private RecyclerView.LayoutManager  mLayoutManager;
    private RecentAdapter               mRecentAdapter;

    private ArrayList<DirectionItem>    mItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recent, container, false);

        createHomeButton(view);
        createWorkButton(view);
        createStarredButton(view);

        createRecyclerView(view);
        createPlaceItems();
        createRecentAdapter();

        return view;
    }
   

    private void createHomeButton(View view) {
        mHomeButton = (Button) view.findViewById(R.id.fragment_header_home_button);
        mHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDirectionItem = getDirectionDb().getDirectionItem(getString(R.string.home));
                if (mDirectionItem.address.isEmpty()) {
                    showAddressAlertDialog(true);
                } else {
                    startNavigator(mDirectionItem);
                }
            }
        });


        mHomeButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mDirectionItem = getDirectionDb().getDirectionItem(getString(R.string.home));
                if (!mDirectionItem.address.isEmpty()) {
                    showAddressAlertDialog(false);
                }
                return false;
            }
        });
    }


    private void createWorkButton(View view) {
        mWorkButton = (Button) view.findViewById(R.id.fragment_header_work_button);
        mWorkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDirectionItem = getDirectionDb().getDirectionItem(getString(R.string.work));
                if (mDirectionItem.address.isEmpty()) {
                    showAddressAlertDialog(true);
                } else {
                    startNavigator(mDirectionItem);
                }
            }
        });

        mWorkButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mDirectionItem = getDirectionDb().getDirectionItem(getString(R.string.work));
                if (!mDirectionItem.address.isEmpty()) {
                    showAddressAlertDialog(false);
                }
                return false;
            }
        });
    }


    private void createStarredButton(View view) {
        mStarredButton = (Button) view.findViewById(R.id.fragment_header_starred_button);
        mStarredButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDirectionItem = getDirectionDb().getDirectionItem(getString(R.string.starred));
                if (mDirectionItem.address.isEmpty()) {
                    showAddressAlertDialog(true);
                } else {
                    startNavigator(mDirectionItem);
                }
            }
        });

        mStarredButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mDirectionItem = getDirectionDb().getDirectionItem(getString(R.string.starred));
                if (!mDirectionItem.address.isEmpty()) {
                    showAddressAlertDialog(false);
                }
                return false;
            }
        });
    }


    private void createRecyclerView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_recent_content_recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    private void createPlaceItems() {
        mItems = new ArrayList<DirectionItem>();
    }


    private void createRecentAdapter() {
        mItems = getDirectionDb().fetchDirectionItems(Sort.ACCESS_DATE);

        mRecentAdapter = new RecentAdapter(getContext(), mItems);
        mRecyclerView.setAdapter(mRecentAdapter);
        mRecentAdapter.setOnItemClickListener(this);
    }


    @Override
    public void refreshItems() {
        if (mRecentAdapter != null) {
            mItems = getDirectionDb().fetchDirectionItems(Sort.ACCESS_DATE);
            mRecentAdapter.refresh(mItems);
        }
    }


    @Override
    public void onItemClick(View view, int position) {
        DirectionItem item = mItems.get(position);
        if (item != null) {
            getDirectionDb().increaseDirectionItemHitCount(item.itemIndex);

            startNavigator(item);
            refreshItems();
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
                if (itemId == R.id.menu_place_share) {
                    sendAddressToOtherApps();
                } else if (itemId == R.id.menu_place_delete) {
                    copyAddress();
                    Toast.makeText(getContext(),getContext().getString(R.string.message_copy_clipboard), Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

    }

    /**
     * Open Google Maps
     * @param item
     */
    private void startNavigator(DirectionItem item) {
        Uri gmmIntentUri = Uri.parse(getDirection(item));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }


    private void showAddressAlertDialog(boolean newAddress) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        String message = getString(R.string.message_modify_place_address);
        if (newAddress) {
            message = getString(R.string.message_create_place_address);
        }
        builder.setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onFragmentDirectionItemControlListener(Action.Place.MODIFY_ADDRESS, mDirectionItem);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        builder.create().show();
    }
}
