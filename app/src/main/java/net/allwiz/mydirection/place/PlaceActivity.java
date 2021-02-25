/*
 * My Direction Android Application
 * @author   Eric(Jaewon) Lee
 * Copyright (C) 2021 Eric(Jaewon) Lee <allwiz@gmail.com>
 * This program is free software: you can redistribute it and/or modify it.
 */
package net.allwiz.mydirection.place;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.allwiz.mydirection.R;
import net.allwiz.mydirection.base.BaseFragment;
import net.allwiz.mydirection.base.BaseNoActionBarActivity;
import net.allwiz.mydirection.database.DirectionItem;
import net.allwiz.mydirection.define.Action;
import net.allwiz.mydirection.define.Category;
import net.allwiz.mydirection.define.Command;
import net.allwiz.mydirection.define.Value;
import net.allwiz.mydirection.util.LogEx;


/**
 * This Activity shows the places with PlaceFragment
 */
public class PlaceActivity extends BaseNoActionBarActivity implements
        BaseFragment.FragmentItemControlListener {
    private static final String TAG = PlaceActivity.class.getSimpleName();

    private PlaceFragment               mPlaceFragment = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_place);

        setArguments();

        createActionbarBackButton();
        createFloatingActionButtons();
        createPlaceFragment();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_OK);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    protected void setArguments() {
        super.setArguments();

        Intent intent = getIntent();
        mLabelName = intent.getStringExtra(Command.Name.LABEL_NAME);
        if (mLabelName.isEmpty()) {
            mLabelName = getString(R.string.page_title_places);
        }
        setTitle(mLabelName);

        mLabelIndex = intent.getLongExtra(Command.Name.LABEL_INDEX, 0);

        LogEx.d(TAG, String.format("[PLACE] LABEL NAME: %s, LABEL INDEX: %d", mLabelName, mLabelIndex));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Command.Code.REQUEST_ADD_PLACE) {

            if (resultCode == RESULT_OK) {
                DirectionItem item = data.getParcelableExtra(Command.Name.DIRECTION_ITEM);
                if (item != null) {

                    LogEx.i(TAG, "-------------------------------------------------------------");
                    LogEx.i(TAG, " (ADD)PLACE INFORMATION");
                    LogEx.i(TAG, "-------------------------------------------------------------");
                    LogEx.i(TAG, "1. ADDRESS   : " + item.address);
                    LogEx.i(TAG, "2. NAME      : " + item.name);
                    LogEx.i(TAG, "3. LATITUDE  : " + item.latitude);
                    LogEx.i(TAG, "4. LONGITUDE : " + item.longitude);
                    LogEx.i(TAG, "5. LABEL INDEX : " + String.valueOf(item.labelIndex));
                    LogEx.i(TAG, "-------------------------------------------------------------");

                    addDirectionItem(item);
                }
            }
        } else if (requestCode == Command.Code.REQUEST_MOD_PLACE) {

            if (resultCode == RESULT_OK) {
                DirectionItem item = data.getParcelableExtra(Command.Name.DIRECTION_ITEM);
                if (item != null) {

                    LogEx.i(TAG, "-------------------------------------------------------------");
                    LogEx.i(TAG, " (ADD)PLACE INFORMATION");
                    LogEx.i(TAG, "-------------------------------------------------------------");
                    LogEx.i(TAG, "1. ADDRESS   : " + item.address);
                    LogEx.i(TAG, "2. NAME      : " + item.name);
                    LogEx.i(TAG, "3. LATITUDE  : " + item.latitude);
                    LogEx.i(TAG, "4. LONGITUDE : " + item.longitude);
                    LogEx.i(TAG, "5. LABEL INDEX : " + String.valueOf(item.labelIndex));
                    LogEx.i(TAG, "-------------------------------------------------------------");

                    updateDirectionItem(item);
                    Toast.makeText(this, getString(R.string.message_complete), Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == Command.Code.REQUEST_MOD_PLACE_ADDRESS) {
            if (resultCode == RESULT_OK) {
                DirectionItem item = data.getParcelableExtra(Command.Name.DIRECTION_ITEM);
                if (item != null) {

                    LogEx.i(TAG, "-------------------------------------------------------------");
                    LogEx.i(TAG, " (MOD-ADDRESS)PLACE INFORMATION");
                    LogEx.i(TAG, "-------------------------------------------------------------");
                    LogEx.i(TAG, "1. ADDRESS   : " + item.address);
                    LogEx.i(TAG, "2. NAME      : " + item.name);
                    LogEx.i(TAG, "3. LATITUDE  : " + item.latitude);
                    LogEx.i(TAG, "4. LONGITUDE : " + item.longitude);
                    LogEx.i(TAG, "5. LABEL INDEX : " + String.valueOf(item.labelIndex));
                    LogEx.i(TAG, "-------------------------------------------------------------");

                    //updateDirectionAddressItem(item);

                    Toast.makeText(this, getString(R.string.message_complete), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    protected void createFloatingActionButtons() {
        createMainFloatingActionButton();
    }


    @Override
    protected void createMainFloatingActionButton() {
        mFab = (FloatingActionButton) findViewById(R.id.activity_place_fab);
        //mFab.setVisibility(View.GONE);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(getApplicationContext(), getString(R.string.message_complete), Toast.LENGTH_SHORT).show();
                showAddPlaceActivity(mCategory, mLabelName, mLabelIndex);

            }
        });
    }


    private void createPlaceFragment() {
        mPlaceFragment = PlaceFragment.newInstance(mCategory, mLabelIndex);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_place_content, mPlaceFragment, Category.Tag.PLACE).commitAllowingStateLoss();
    }


    private void showAddPlaceActivity(int action, DirectionItem item) {
        Intent intent = new Intent(PlaceActivity.this, AddPlaceActivity.class);
        intent.putExtra(Command.Name.CALL, Value.Parameter.CALL);
        intent.putExtra(Command.Name.ACTION, action);
        intent.putExtra(Command.Name.CATEGORY, item.category);
        intent.putExtra(Command.Name.DIRECTION_ITEM, item);

        int command = Command.Code.REQUEST_MOD_PLACE;
        if (action == Action.Place.MODIFY_ADDRESS) {
            command = Command.Code.REQUEST_MOD_PLACE_ADDRESS;
        }
        startActivityForResult(intent, command);
    }


    private void addDirectionItem(DirectionItem item) {
        mPlaceFragment.addDirectionItem(item);
        mPlaceFragment.refreshItems();
    }


    private void updateDirectionItem(DirectionItem item) {
        if (getDirectionDb().updateDirectionItem(item) == Value.Database.QUERY_SUCCESS) {
            mPlaceFragment.refreshItems();
        }
    }

    @Override
    public void onFragmentDirectionItemControlListener(int action, DirectionItem item) {
        showAddPlaceActivity(action, item);
    }

    @Override
    public void onFragmentLabelItemControlListener(int category, String labelName, long labelIndex) {

    }


    private void showAddPlaceActivity(int category, String labelName, long labelIndex) {
        Intent intent = new Intent(PlaceActivity.this, AddPlaceActivity.class);
        intent.putExtra(Command.Name.CALL, Value.Parameter.CALL);
        intent.putExtra(Command.Name.ACTION, Action.Place.ADD_WITH_LABEL);
        intent.putExtra(Command.Name.CATEGORY, category);
        intent.putExtra(Command.Name.LABEL_NAME, labelName);
        intent.putExtra(Command.Name.LABEL_INDEX, labelIndex);

        startActivityForResult(intent, Command.Code.REQUEST_ADD_PLACE);
    }
}
