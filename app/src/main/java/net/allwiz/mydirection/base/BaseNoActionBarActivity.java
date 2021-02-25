/*
 * My Direction Android Application
 * @author   Eric(Jaewon) Lee
 * Copyright (C) 2021 Eric(Jaewon) Lee <allwiz@gmail.com>
 * This program is free software: you can redistribute it and/or modify it.
 */
package net.allwiz.mydirection.base;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import net.allwiz.mydirection.R;
import net.allwiz.mydirection.database.DirectionItem;
import net.allwiz.mydirection.define.Category;
import net.allwiz.mydirection.define.Command;
import net.allwiz.mydirection.define.Value;

public class BaseNoActionBarActivity extends BaseActivity {
    private static final String TAG = BaseNoActionBarActivity.class.getSimpleName();

    protected int               mAction;
    protected int               mCategory;
    protected String            mLabelName;
    protected long              mLabelIndex = -1;
    protected DirectionItem     mDirectionItem = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove shadow below actionbar
        getSupportActionBar().setElevation(0);

        setSharedApplication();
    }


    protected void createActionbarBackButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    protected void setArguments() {
        Intent intent = getIntent();
        if (intent != null) {
            String caller = intent.getStringExtra(Command.Name.CALL);
            if (caller == null || caller.isEmpty()) {
                exitApp();
                return;
            }

            if (caller.equals(Value.Parameter.CALL)) {
                mCategory = intent.getIntExtra(Command.Name.CATEGORY, Category.FAVORITE);
                return;
            }
        }

        exitApp();
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
}

