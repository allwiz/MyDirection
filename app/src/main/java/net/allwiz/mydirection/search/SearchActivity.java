/*
 * My Direction Android Application
 * @author   Eric(Jaewon) Lee
 * Copyright (C) 2021 Eric(Jaewon) Lee <allwiz@gmail.com>
 * This program is free software: you can redistribute it and/or modify it.
 */
package net.allwiz.mydirection.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.allwiz.mydirection.R;
import net.allwiz.mydirection.base.BaseNoActionBarActivity;
import net.allwiz.mydirection.define.Category;
import net.allwiz.mydirection.define.Command;

/**
 * This Activity searches the keyword and shows them
 */
public class SearchActivity extends BaseNoActionBarActivity {
    private static final String TAG = SearchActivity.class.getSimpleName();

    private SearchFragment              mSearchFragment;
    private String                      mQuery;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        setArguments();

        createActionbarBackButton();
        createSearchFragment();
    }


    private void createSearchFragment() {
        mSearchFragment = SearchFragment.newInstance(mQuery);
        getSupportFragmentManager().beginTransaction().replace(R.id.activity_search_content, mSearchFragment, Category.Tag.SEARCH).commitAllowingStateLoss();
    }


    @Override
    protected void setArguments() {
        super.setArguments();

        Intent intent = getIntent();
        mQuery = intent.getStringExtra(Command.Name.QUERY);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_OK);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
