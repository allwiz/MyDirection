/*
 * My Direction Android Application
 * @author   Eric(Jaewon) Lee
 * Copyright (C) 2021 Eric(Jaewon) Lee <allwiz@gmail.com>
 * This program is free software: you can redistribute it and/or modify it.
 */
package net.allwiz.mydirection;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import net.allwiz.mydirection.base.BaseActivity;
import net.allwiz.mydirection.base.BaseFragment;
import net.allwiz.mydirection.database.DirectionItem;
import net.allwiz.mydirection.database.LabelDescItem;
import net.allwiz.mydirection.define.Action;
import net.allwiz.mydirection.define.Category;
import net.allwiz.mydirection.define.Command;
import net.allwiz.mydirection.define.Value;
import net.allwiz.mydirection.dialog.CreateLabelDialogFragment;
import net.allwiz.mydirection.place.AddPlaceActivity;
import net.allwiz.mydirection.place.PlaceActivity;
import net.allwiz.mydirection.search.SearchActivity;
import net.allwiz.mydirection.tab.HostFragment;
import net.allwiz.mydirection.util.LogEx;


/**
 * Main Application of the project
 */
public class MainActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        CreateLabelDialogFragment.CreateLabelDialogFragmentControlListener,
        HostFragment.HostFragmentControlListener,
        BaseFragment.FragmentItemControlListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private AppBarConfiguration     mAppBarConfiguration;

    private Toolbar                 mToolbar;
    private NavigationView          mNavigationView;
    private HostFragment            mHostFragment;

    private int                     mCategory = Category.RECENT;

    private MenuItem                mSearchLabelMenuItem;
    private SearchView              mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isTaskRoot()) {
            LogEx.d(TAG, "=============================================");
            LogEx.d(TAG, "(STATE) The other screen is showing");
            LogEx.d(TAG, "=============================================");
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

/*
        if (BuildConfig.DEBUG) {
            getDirectionDb().recreate();
        }
*/

        createToolbar();
        createFloatingActionButtons();
        createDrawer();
        createNavigationView();
        createHostFragment();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // search menu in the action bar
        setSearchMenuItem(menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
/*
        if (id == R.id.action_settings) {
            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if (isFabMenuOpen()) {
            closeFabMenu();
            return;
        }

        super.onBackPressed();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        //return false;
        int id = item.getItemId();
        if (id == R.id.nav_recent) {
            mHostFragment.setViewPager(Category.RECENT);
        } else if (id == R.id.nav_favorite) {
            mHostFragment.setViewPager(Category.FAVORITE);
        } else if (id == R.id.nav_travel) {
            mHostFragment.setViewPager(Category.TRAVEL);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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

                    updateDirectionAddressItem(item);

                    Toast.makeText(this, getString(R.string.message_complete), Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == Command.Code.REQUEST_SHOW_PLACE) {
            if (mCategory == Category.FAVORITE) {
                mHostFragment.refreshFavorite();
            } else if (mCategory == Category.TRAVEL) {
                mHostFragment.refreshTravel();
            }
        }
    }


    private void createToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }


    private void createDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }


    private void createNavigationView() {
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    protected void createFloatingActionButtons() {
        createMainFloatingActionButton();
        createLabelFloatingActionButton();
        createPlaceFloatingActionButton();
    }


    @Override
    protected void createMainFloatingActionButton() {
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setVisibility(View.GONE);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isFabMenuOpen()) {
                    showFabMenu();
                } else {
                    closeFabMenu();
                }
            }
        });
    }


    @Override
    protected void createLabelFloatingActionButton() {
        super.createLabelFloatingActionButton();

        mAddLabelBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                closeFabMenu();

                int category = mHostFragment.getCurrentFragmentItem();
                switch (category) {
                    case Category.RECENT:
                        break;
                    case Category.FAVORITE:
                    case Category.TRAVEL:
                        showCreateLabelDialogFragment(category);
                        break;
                }
            }
        });
    }


    @Override
    protected void createPlaceFloatingActionButton() {
        super.createPlaceFloatingActionButton();

        mAddPlaceBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                closeFabMenu();

                int category = mHostFragment.getCurrentFragmentItem();
                switch (category) {
                    case Category.RECENT:
                        break;
                    case Category.FAVORITE:
                    case Category.TRAVEL:
                        // If there is no label, create label
                        if (getDirectionDb().getLabelItemCount(category) == 0) {
                            //showCreateLabelDialogFragment(category);
                            showCreateLabelAlertDialog();
                        } else {
                            showAddPlaceActivity(category);
                        }
                        break;
                }
            }
        });
    }


    /**
     * Create Host Fragment for Tab that has RecentFragment, FavoriteFragment, and TravelFragment.
     */
    private void createHostFragment() {
        mHostFragment = new HostFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_main, mHostFragment, Category.Tag.HOST).commitAllowingStateLoss();
    }


    /**
     * When user selects one of the tabs(onTabSelected in HostFragment), this function is called.
     * @param category  RECENT, FAVORITES, TRAVELS
     */
    @Override
    public void onHostFragmentControlListener(int category) {
        mCategory = category;

        switch (category) {
            case Category.RECENT:
                mFab.setVisibility(View.GONE);
                break;
            case Category.FAVORITE:
            case Category.TRAVEL:
                mFab.setVisibility(View.VISIBLE);
                break;
        }
    }


    /**
     * Add place
     * @param category  RECENT, FAVORITES, TRAVELS
     */
    private void showAddPlaceActivity(int category) {
        Intent intent = new Intent(MainActivity.this, AddPlaceActivity.class);
        intent.putExtra(Command.Name.CALL, Value.Parameter.CALL);
        intent.putExtra(Command.Name.ACTION, Action.Place.ADD);
        intent.putExtra(Command.Name.CATEGORY, category);

        startActivityForResult(intent, Command.Code.REQUEST_ADD_PLACE);
    }


    /**
     * Add place with label
     * @param category      RECENT, FAVORITES, TRAVELS
     * @param labelName     label name
     * @param labelIndex    label index
     */
    private void showAddPlaceActivity(int category, String labelName, long labelIndex) {
        Intent intent = new Intent(MainActivity.this, AddPlaceActivity.class);
        intent.putExtra(Command.Name.CALL, Value.Parameter.CALL);
        intent.putExtra(Command.Name.ACTION, Action.Place.ADD_WITH_LABEL);
        intent.putExtra(Command.Name.CATEGORY, category);
        intent.putExtra(Command.Name.LABEL_NAME, labelName);
        intent.putExtra(Command.Name.LABEL_INDEX, labelIndex);

        startActivityForResult(intent, Command.Code.REQUEST_ADD_PLACE);
    }


    /**
     * Modify place information
     * @param action    Action.Place.MODIFY_ADDRESS
     * @param item      Direction item
     */
    private void showAddPlaceActivity(int action, DirectionItem item) {
        Intent intent = new Intent(MainActivity.this, AddPlaceActivity.class);
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


    /**
     * Show create label dialog
     * @param category      RECENT, FAVORITES, TRAVELS
     */
    private void showCreateLabelDialogFragment(int category) {
        CreateLabelDialogFragment c = CreateLabelDialogFragment.newInstance(category);
        c.show(getFragmentManager(), null);
    }


    /**
     * Add Direciton item
     * @param item  Direction information
     */
    private void addDirectionItem(DirectionItem item) {
        mHostFragment.addDirectionItem(item);
    }


    /**
     * Update Direction item
     * @param item  Direction information
     */
    private void updateDirectionItem(DirectionItem item) {
        getDirectionDb().updateDirectionItem(item);
    }


    /**
     * Update Address
     * @param item  Direction information
     */
    private void updateDirectionAddressItem(DirectionItem item) {
        getDirectionDb().updateDirectionItem(item);
    }


    /**
     * When user clicks OK button in CreateLabelDialogFragment, this function shows AddPlaceActivity or a warning message
     * @param action    Action.Check.Label.*
     */
    @Override
    public void onCreateLabelDialogFragmentControlListener(int action) {

        switch (action) {
            case Action.Check.Label.OK:
                mHostFragment.refreshItems(mCategory);
                showAddPlaceActivity(mCategory);
                break;
            case Action.Check.Label.EMPTY_LABEL_NAME:
                showErrorAlertDialog(getString(R.string.message_empty_label));
                break;
            case Action.Check.Label.EXIST_LABEL_NAME:
                showErrorAlertDialog(getString(R.string.message_exist_label_name));
                break;
            case Action.Check.Label.FAILED_INSERT_LABEL_NAME:
                showErrorAlertDialog(getString(R.string.message_failed_insert_label));
                break;
        }
    }


    /**
     * When user clicks OK button in CreateLabelDialogFragment, this function shows AddPlaceActivity in order to add a place
     * @param action        Action.Check.Label.*
     * @param labelName     label name
     * @param labelIndex    label index
     */
    @Override
    public void onCreateLabelDialogFragmentControlListener(int action, String labelName, long labelIndex) {
        if (action == Action.Check.Label.OK) {
            mHostFragment.refreshItems(mCategory);
            showAddPlaceActivity(mCategory, labelName, labelIndex);
        }
    }

    /**
     * Show Alert Dialog in order to create Label
     * https://developer.android.com/guide/topics/ui/dialogs#java
     */
    private void showCreateLabelAlertDialog() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.message_create_label)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showCreateLabelDialogFragment(mCategory);
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


    /**
     * If there is no Label, it shows showCreateLabelAlertDialog to create a Label.
     * If there is a Label, it shows AddPlaceActivity to add a place.
     * @param category      RECENT, FAVORITES, TRAVELS
     * @param labelName     label name
     * @param labelIndex    label index
     */
    @Override
    public void onFragmentLabelItemControlListener(int category, String labelName, long labelIndex) {
        if (getDirectionDb().getDirectionItemCount(labelIndex) == 0) {
            showCreateAddressAlertDialog();
        } else {
            showPlaceActivity(category, labelName, labelIndex);
        }
    }


    /**
     * Modify a place information
     * @param action        Action.Place.MODIFY
     * @param item          Direction information
     */
    @Override
    public void onFragmentDirectionItemControlListener(int action, DirectionItem item) {
        showAddPlaceActivity(action, item);
    }


    /**
     * Show Places in the list
     * @param category      RECENT, FAVORITES, TRAVELS
     * @param name          label name
     * @param labelIndex    label index
     */
    private void showPlaceActivity(int category, String name, long labelIndex) {

        LogEx.d(TAG, String.format("[PLACE] CATEGORY: %d, LABEL NAME: %s, LABEL INDEX: %d", category, name, labelIndex));

        Intent intent = new Intent(MainActivity.this, PlaceActivity.class);
        intent.putExtra(Command.Name.CALL, Value.Parameter.CALL);
        intent.putExtra(Command.Name.CATEGORY, category);
        intent.putExtra(Command.Name.LABEL_NAME, name);
        intent.putExtra(Command.Name.LABEL_INDEX, labelIndex);

        startActivityForResult(intent, Command.Code.REQUEST_SHOW_PLACE);
    }


    /**
     * Show create place message
     */
    private void showCreateAddressAlertDialog() {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage(R.string.message_create_place_address)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showAddPlaceActivity(mCategory);
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


    /**
     * Show search icon in the Action bar in order to search a query
     * @param menu      search menu
     */
    private void setSearchMenuItem(Menu menu) {
        mSearchLabelMenuItem = menu.findItem(R.id.action_search_labels);
        mSearchLabelMenuItem.setVisible(true);
        mSearchView = (SearchView) mSearchLabelMenuItem.getActionView();
        mSearchView.setIconifiedByDefault(false);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        mSearchView.setLayoutParams(params);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                LogEx.d(TAG, "SEARCH: " + query);
                showSearchActivity(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


    /**
     * Show SearchActivity
     * @param query     search keyword
     */
    private void showSearchActivity(String query) {
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        intent.putExtra(Command.Name.CALL, Value.Parameter.CALL);
        intent.putExtra(Command.Name.QUERY, query);
        startActivity(intent);
     }
}