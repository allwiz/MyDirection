/*
 * My Direction Android Application
 * @author   Eric(Jaewon) Lee
 * Copyright (C) 2021 Eric(Jaewon) Lee <allwiz@gmail.com>
 * This program is free software: you can redistribute it and/or modify it.
 */
package net.allwiz.mydirection.base;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.allwiz.mydirection.R;
import net.allwiz.mydirection.database.DirectionDb;
import net.allwiz.mydirection.shared.SharedApplication;

/**
 * This Activity is a base activity
 */
public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();

    protected SharedApplication         mSharedApplication;

    protected FloatingActionButton      mFab;
    protected FloatingActionButton      mAddLabelFab;
    protected FloatingActionButton      mAddPlaceFab;
    protected LinearLayout              mAddLabelBox;
    protected LinearLayout              mAddPlaceBox;
    protected boolean                   mFabMenuOpen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSharedApplication();
    }


    protected void setSharedApplication() {
        mSharedApplication = (SharedApplication)getApplicationContext();
    }


    protected DirectionDb getDirectionDb() {
        return mSharedApplication.getDirectionDb();
    }

    protected void exitApp() {
        finishAffinity();
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    protected void showErrorAlertDialog(String message) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        // Create the AlertDialog object and return it
        builder.create().show();
    }


    protected void createFloatingActionButtons() {
    }


    protected void createMainFloatingActionButton() {
    }


    protected void createLabelFloatingActionButton() {
        mAddLabelBox = (LinearLayout) findViewById(R.id.app_bar_label_fab_box);
        mAddLabelBox.setVisibility(View.GONE);
        mAddLabelFab = (FloatingActionButton) findViewById(R.id.app_bar_add_label_fab);
    }


    protected void createPlaceFloatingActionButton() {
        mAddPlaceBox = (LinearLayout) findViewById(R.id.app_bar_place_fab_box);
        mAddPlaceBox.setVisibility(View.GONE);
        mAddPlaceFab = (FloatingActionButton) findViewById(R.id.app_bar_add_place_fab);
    }


    protected void showFabMenu() {
        mFabMenuOpen = true;
        mAddLabelBox.setVisibility(View.VISIBLE);
        mAddPlaceBox.setVisibility(View.VISIBLE);

        mFab.animate().rotation(180);
        mAddPlaceBox.animate().translationY(-getResources().getDimension(R.dimen.margin_100));
        mAddLabelBox.animate().translationY(-getResources().getDimension(R.dimen.margin_160));
    }


    protected void closeFabMenu() {
        mFabMenuOpen = false;
        mAddLabelBox.setVisibility(View.GONE);
        mAddPlaceBox.setVisibility(View.GONE);
    }


    protected boolean isFabMenuOpen() {
        return mFabMenuOpen;
    }


}
