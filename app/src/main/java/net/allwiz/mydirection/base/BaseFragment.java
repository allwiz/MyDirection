/*
 * My Direction Android Application
 * @author   Eric(Jaewon) Lee
 * Copyright (C) 2021 Eric(Jaewon) Lee <allwiz@gmail.com>
 * This program is free software: you can redistribute it and/or modify it.
 */
package net.allwiz.mydirection.base;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import net.allwiz.mydirection.R;
import net.allwiz.mydirection.database.DirectionDb;
import net.allwiz.mydirection.database.DirectionItem;
import net.allwiz.mydirection.database.LabelDescItem;
import net.allwiz.mydirection.define.Action;
import net.allwiz.mydirection.shared.SharedApplication;
import net.allwiz.mydirection.util.LogEx;

import static android.content.Context.CLIPBOARD_SERVICE;

public class BaseFragment extends Fragment {
    private static final String TAG = BaseFragment.class.getSimpleName();

    protected SharedApplication             mSharedApplication;

    protected FragmentItemControlListener   mListener;

    protected DirectionItem                 mDirectionItem = null;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        setSharedApplication();
        setControlListener();
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        setSharedApplication();
        setControlListener();
    }


    private void setSharedApplication() {
        mSharedApplication = (SharedApplication)getActivity().getApplicationContext();
    }


    protected DirectionDb getDirectionDb() {
        return mSharedApplication.getDirectionDb();
    }


    private void setControlListener() {
        try {
            mListener = (FragmentItemControlListener) getActivity();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }


    public interface FragmentItemControlListener {
        public void onFragmentLabelItemControlListener(int category, String labelName, long labelIndex);
        public void onFragmentDirectionItemControlListener(int action, DirectionItem item);
    }


    public void addDirectionItem(DirectionItem item) {

        if (item != null) {
            getDirectionDb().insertDirectionItem(item);
        }
    }

    public void refreshItems() {
    }


    protected void showErrorAlertDialog(String message) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        // Create the AlertDialog object and return it
        builder.create().show();
    }


    protected void showConfirmDeletePlaceAlertDialog(DirectionItem item) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        //builder.setMessage(getContext().getString(R.string.message_delete_place))
        builder.setMessage(String.format(getContext().getString(R.string.message_delete_item), item.name))
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDirectionDb().deleteDirectionItem(item.itemIndex);
                        refreshItems();
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


    protected void showConfirmDeleteLabelAlertDialog(LabelDescItem item) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(String.format(getContext().getString(R.string.message_delete_item), item.name))
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        //mListener.onFragmentDirectionItemControlListener(Action.Place.DELETE_LABEL, item);
                        getDirectionDb().deleteLabelAllItems(item.labelIndex);
                        refreshItems();
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



    protected String getDirection(DirectionItem item) {
        String nav = String.format("google.navigation:q=%s&%s", item.address.replaceAll(" ", "+"), item.mode);
        if (!item.avoid.isEmpty()) {
            nav += String.format("&avoid=%s", item.avoid);
        }

        LogEx.d(TAG, nav);
        return nav;
    }


    protected void sendAddressToOtherApps() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, mDirectionItem.address);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }


    protected void copyAddress() {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(getContext().getString(R.string.app_name), String.format("%s\n%s", mDirectionItem.name, mDirectionItem.address));
        clipboard.setPrimaryClip(clip);
    }
}
