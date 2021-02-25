/*
 * My Direction Android Application
 * @author   Eric(Jaewon) Lee
 * Copyright (C) 2021 Eric(Jaewon) Lee <allwiz@gmail.com>
 * This program is free software: you can redistribute it and/or modify it.
 */
package net.allwiz.mydirection.place;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import net.allwiz.mydirection.R;
import net.allwiz.mydirection.base.BaseNoActionBarActivity;
import net.allwiz.mydirection.database.DirectionItem;
import net.allwiz.mydirection.define.Action;
import net.allwiz.mydirection.define.Category;
import net.allwiz.mydirection.define.Command;
import net.allwiz.mydirection.define.Direction;
import net.allwiz.mydirection.define.Value;
import net.allwiz.mydirection.util.LogEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * This Activity adds the place
 */
public class AddPlaceActivity extends BaseNoActionBarActivity {
    private static final String TAG = AddPlaceActivity.class.getSimpleName();

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private Spinner         mCategorySpinner;

    private Spinner         mLabelSpinner;
    private RadioGroup      mTransportationRadioGroup;


    private EditText        mChooseDestinationEdit;
    private EditText        mNameDestinationEdit;


    private CheckBox        mAvoidFerriesCheckBox;
    private CheckBox        mAvoidHighwaysCheckBox;
    private CheckBox        mAvoidTollsCheckBox;

    private Button          mCancelButton;
    private Button          mSaveButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_place);

        createActionbarBackButton();
        createDirectionItem();

        setArguments();

        initPlaceApi();
        createCategorySpinner();
        createLabelSpinner();
        createTransportationRadioGroup();

        createChooseDestination();
        createNameDestination();

        createAvoidFerriesCheckBox();
        createAvoidHighwaysCheckBox();
        createAvoidTollsCheckBox();

        createCancelButton();
        createSaveButton();

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void setArguments() {
        super.setArguments();

        Intent intent = getIntent();
        mAction = intent.getIntExtra(Command.Name.ACTION, Action.Place.ADD);
        String title = getString(R.string.title_create_place);

        switch (mAction) {
            case Action.Place.ADD:
                break;
            case Action.Place.ADD_WITH_LABEL:
                mLabelName = intent.getStringExtra(Command.Name.LABEL_NAME);
                mLabelIndex = intent.getLongExtra(Command.Name.LABEL_INDEX, -1);
                break;
            case Action.Place.MODIFY:
            case Action.Place.MODIFY_ADDRESS:
                title = getString(R.string.title_modify_place);
                mDirectionItem = intent.getParcelableExtra(Command.Name.DIRECTION_ITEM);
                break;
        }
        setTitle(title);
    }


    /**
     * Init Place API
     * API Key
     * 1. https://developers.google.com/places/android-sdk/signup
     * 2. https://stackoverflow.com/questions/27609442/how-to-get-the-sha-1-fingerprint-certificate-in-android-studio-for-debug-mode
     * SHA-1 fingerprint: Right pane in Android Studio > Gradle > My Direction > Tasks > android > signingReport
     */
    private void initPlaceApi() {
        if (!Places.isInitialized()) {
            String api_key = getString(R.string.place_api_key);
            if (api_key.equalsIgnoreCase("needAPI")) {
                // If you get the api, please remove this code/
                // Please paste your api key to 'R.string.place_api_key'
                showPlaceApiAlertMessage();
            } else {
                Places.initialize(getApplicationContext(), api_key);
            }
        }
    }


    // reference: https://developer.android.com/guide/topics/ui/controls/spinner
    private void createCategorySpinner() {
        mCategorySpinner = (Spinner) findViewById(R.id.activity_place_category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(adapter);
        mCategorySpinner.setSelection(mCategory-1);

        if (mAction == Action.Place.MODIFY_ADDRESS) {
            mCategorySpinner.setVisibility(View.GONE);
            TextView textView = (TextView) findViewById(R.id.activity_place_category_text);
            textView.setVisibility(View.GONE);
        }

        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    mCategory = Category.FAVORITE;
                } else {
                    mCategory = Category.TRAVEL;
                }

                LogEx.d(TAG, String.format("CATEGORY: %d - %s", mCategory, mCategorySpinner.getItemAtPosition(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void createLabelSpinner() {
        mLabelSpinner = (Spinner) findViewById(R.id.activity_place_label_spinner);

        ArrayList<String> items = getDirectionDb().fetchLabelNameItems(mCategory);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLabelSpinner.setAdapter(adapter);
        mLabelSpinner.setSelection(0);

        switch (mAction) {
            case Action.Place.ADD:
                break;
            case Action.Place.ADD_WITH_LABEL:
                if (!mLabelName.isEmpty()) {
                    int position = items.indexOf(mLabelName);
                    if (position != -1) {
                        mLabelSpinner.setSelection(position);
                    }
                }
                break;
            case Action.Place.MODIFY:
                String labelName = getDirectionDb().getLabelItemName(mDirectionItem.labelIndex);
                if (!labelName.isEmpty()) {
                    int position = items.indexOf(labelName);
                    if (position != -1) {
                        mLabelSpinner.setSelection(position);
                    }
                }
                break;
            case Action.Place.MODIFY_ADDRESS:
                mLabelSpinner.setVisibility(View.GONE);
                TextView textView = (TextView) findViewById(R.id.activity_place_label_text);
                textView.setVisibility(View.GONE);

                View line = (View) findViewById(R.id.activity_place_label_divider);
                line.setVisibility(View.GONE);
                return;
        }

        mLabelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                LogEx.d(TAG, String.format("LABEL: %s", mLabelSpinner.getItemAtPosition(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void createDirectionItem() {
        if (mDirectionItem == null) {
            mDirectionItem = new DirectionItem();
        }
    }


    private void createTransportationRadioGroup() {
        mTransportationRadioGroup = (RadioGroup) findViewById(R.id.activity_place_transportation_mode_group);

        if (mAction == Action.Place.MODIFY) {
            mTransportationRadioGroup.check(getTransportationModeId(mDirectionItem.mode));
        } else {
            mTransportationRadioGroup.check(R.id.activity_place_transportation_mode_drive);
        }

        mTransportationRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                showTransportationModeCheckBox(checkedId);
            }
        });

    }




    private void createChooseDestination() {
        mChooseDestinationEdit = (EditText) findViewById(R.id.activity_place_choose_destination_edit);

        if (mAction == Action.Place.MODIFY) {
            mChooseDestinationEdit.setText(mDirectionItem.address);
        }

        mChooseDestinationEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getString(R.string.place_api_key).equalsIgnoreCase("needAPI")) {
                    // If you get your api key, please remove this code.
                    showPlaceApiAlertMessage();
                } else {
                    showPlaceAutoComplete();
                }
            }
        });

    }


    private void createNameDestination() {
        mNameDestinationEdit = (EditText) findViewById(R.id.activity_place_name_edit);

        if (mAction == Action.Place.MODIFY_ADDRESS) {
            mNameDestinationEdit.setText(mDirectionItem.name);
            mNameDestinationEdit.setEnabled(false);
        } else if (mAction == Action.Place.MODIFY) {
            mNameDestinationEdit.setText(mDirectionItem.name);
        }
    }


    private void createAvoidFerriesCheckBox() {
        mAvoidFerriesCheckBox = (CheckBox) findViewById(R.id.activity_place_checkbox_avoid_ferries);

        if (mAction == Action.Place.MODIFY) {
            boolean checked = true;
            if (mDirectionItem.avoid.indexOf(Direction.Avoid.FERRIES) == Value.Data.NOT_FOUND) {
                checked = false;
            }
            mAvoidFerriesCheckBox.setChecked(checked);
        } else {
            mAvoidFerriesCheckBox.setChecked(true);
        }
    }


    private void createAvoidHighwaysCheckBox() {
        mAvoidHighwaysCheckBox = (CheckBox) findViewById(R.id.activity_place_checkbox_avoid_highways);
        if (mAction == Action.Place.MODIFY) {
            boolean checked = true;
            if (mDirectionItem.avoid.indexOf(Direction.Avoid.HIGHWAYS) == Value.Data.NOT_FOUND) {
                checked = false;
            }
            mAvoidHighwaysCheckBox.setChecked(checked);
        } else {
            mAvoidHighwaysCheckBox.setChecked(false);
        }
    }

    private void createAvoidTollsCheckBox() {
        mAvoidTollsCheckBox = (CheckBox) findViewById(R.id.activity_place_checkbox_avoid_tolls);
        if (mAction == Action.Place.MODIFY) {
            boolean checked = true;
            if (mDirectionItem.avoid.indexOf(Direction.Avoid.TOLLS) == Value.Data.NOT_FOUND) {
                checked = false;
            }
            mAvoidTollsCheckBox.setChecked(checked);
        } else {
            mAvoidTollsCheckBox.setChecked(true);
        }
    }



    private void createCancelButton() {
        mCancelButton = (Button) findViewById(R.id.activity_place_cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void createSaveButton() {
        mSaveButton = (Button) findViewById(R.id.activity_place_save_button);
        mSaveButton.setEnabled(false);
        if (mAction == Action.Place.MODIFY) {
            mSaveButton.setEnabled(true);
        }

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Address
                String address = mChooseDestinationEdit.getText().toString();
                if (address.isEmpty()) {
                    showErrorAlertDialog(getString(R.string.message_empty_place_address));
                    return;
                }

                // Name
                String name = mNameDestinationEdit.getText().toString();
                if (name.isEmpty()) {
                    showErrorAlertDialog(getString(R.string.message_empty_place_name));
                    return;
                }

                if (mAction == Action.Place.MODIFY_ADDRESS) {

                } else if (mAction == Action.Place.MODIFY) {
                    mDirectionItem.address = "";
                    mDirectionItem.name = "";
                    mDirectionItem.mode = "";
                    mDirectionItem.avoid = "";
                } else {
                    if (getDirectionDb().existPlaceNameItem(name)) {
                        showErrorAlertDialog(getString(R.string.message_exist_place_name));
                        return;
                    }

                    // Label
                    String label = mLabelSpinner.getSelectedItem().toString();
                    long labelIndex = getDirectionDb().getLabelItemIndex(label);
                    if (labelIndex == Value.Database.INVALID_ROW_ID) {
                        return;
                    }
                    mDirectionItem.labelIndex = labelIndex;
                }


                mDirectionItem.address = address;
                mDirectionItem.name = name;
                mDirectionItem.mode = getTransportationMode();
                // Checkbox: Avoid
                mDirectionItem.avoid = getTransportationAvoid();

                savePlaceItem();
            }

        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Actionbar Back button
        if (android.R.id.home == item.getItemId()) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);

                LogEx.i(TAG, "-------------------------------------------------------------");
                LogEx.i(TAG, " PLACE INFORMATION");
                LogEx.i(TAG, "-------------------------------------------------------------");
                LogEx.i(TAG, "1. ADDRESS   : " + place.getAddress());
                LogEx.i(TAG, "2. NAME      : " + place.getName());
                LogEx.i(TAG, "3. LATITUDE  : " + place.getLatLng().latitude);
                LogEx.i(TAG, "4. LONGITUDE : " + place.getLatLng().longitude);
                LogEx.i(TAG, "-------------------------------------------------------------");


                mDirectionItem.set(mCategory, place.getAddress().toString(), place.getName().toString(), String.valueOf(place.getLatLng().latitude), String.valueOf(place.getLatLng().longitude));

                mChooseDestinationEdit.setText(place.getAddress().toString());

                if (mAction != Action.Place.MODIFY_ADDRESS) {
                    String placeName = place.getName().toString();
                    if (getDirectionDb().existPlaceNameItem(placeName)) {
                        String address = place.getAddress().toString();
                        int street = address.indexOf(",");
                        if (street != -1) {
                            mNameDestinationEdit.setText(String.format("%s (%s)", placeName, address.substring(0, street)));
                        } else {
                            showErrorAlertDialog(getString(R.string.message_exist_place_name));
                        }
                    } else {
                        mNameDestinationEdit.setText(placeName);
                    }
                }

                mSaveButton.setEnabled(true);

            }
            /* else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } */else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }



    // reference: https://developers.google.com/places/android-sdk/autocomplete
    private void showPlaceAutoComplete() {
        try {
            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            //List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (Exception e) {
            // TODO: Handle the error.
            LogEx.d(TAG, e.getMessage());
        }



/*
        PlaceAutocompleteFragment fragment = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.d(TAG, "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {

            }
        });
*/
    }


    private String getTransportationMode() {
        String mode = Direction.Mode.DRIVING;
        switch (mTransportationRadioGroup.getCheckedRadioButtonId()) {
            case R.id.activity_place_transportation_mode_drive:
                mode = Direction.Mode.DRIVING;
                break;
            case R.id.activity_place_transportation_mode_transit:
                mode = Direction.Mode.TRANSIT;
                break;
            case R.id.activity_place_transportation_mode_bike:
                mode = Direction.Mode.BICYCLING;
                break;
            case R.id.activity_place_transportation_mode_walk:
                mode = Direction.Mode.WALKING;
                break;
        }
        return mode;
    }


    private int getTransportationModeId(String mode) {
        int id = R.id.activity_place_transportation_mode_drive;
        switch (mode) {
            case Direction.Mode.DRIVING:
                id = R.id.activity_place_transportation_mode_drive;
                break;
            case Direction.Mode.TRANSIT:
                id = R.id.activity_place_transportation_mode_transit;
                break;
            case Direction.Mode.BICYCLING:
                id = R.id.activity_place_transportation_mode_bike;
                break;
            case Direction.Mode.WALKING:
                id = R.id.activity_place_transportation_mode_walk;
                break;
        }
        return id;
    }


    private String getTransportationAvoid() {
        String avoid = Direction.Mode.DRIVING;
        switch (mTransportationRadioGroup.getCheckedRadioButtonId()) {
            case R.id.activity_place_transportation_mode_drive:
                if (mAvoidFerriesCheckBox.isChecked()) {
                    avoid += Direction.Avoid.FERRIES;
                }
                if (mAvoidHighwaysCheckBox.isChecked()) {
                    avoid += Direction.Avoid.HIGHWAYS;
                }
                if (mAvoidTollsCheckBox.isChecked()) {
                    avoid += Direction.Avoid.TOLLS;
                }
                break;
            case R.id.activity_place_transportation_mode_transit:
                avoid = "";
                break;
            case R.id.activity_place_transportation_mode_bike:
            case R.id.activity_place_transportation_mode_walk:
                if (mAvoidFerriesCheckBox.isChecked()) {
                    avoid += Direction.Avoid.FERRIES;
                }
                break;
        }
        return avoid;
    }


    private void showTransportationModeCheckBox(int radioGroupId) {
        switch (radioGroupId) {
            case R.id.activity_place_transportation_mode_drive:
                mAvoidFerriesCheckBox.setVisibility(View.VISIBLE);
                mAvoidHighwaysCheckBox.setVisibility(View.VISIBLE);
                mAvoidTollsCheckBox.setVisibility(View.VISIBLE);
                break;
            case R.id.activity_place_transportation_mode_transit:
                mAvoidFerriesCheckBox.setVisibility(View.GONE);
                mAvoidHighwaysCheckBox.setVisibility(View.GONE);
                mAvoidTollsCheckBox.setVisibility(View.GONE);
                break;
            case R.id.activity_place_transportation_mode_bike:
            case R.id.activity_place_transportation_mode_walk:
                mAvoidFerriesCheckBox.setVisibility(View.VISIBLE);
                mAvoidHighwaysCheckBox.setVisibility(View.GONE);
                mAvoidTollsCheckBox.setVisibility(View.GONE);
                break;
        }

    }


    // https://developers.google.com/maps/documentation/android-sdk/intents
    private void startNavigation() {

        //String nav = mDirectionItem.address.replaceAll(" ", "+");
        String nav = String.format("google.navigation:q=%s&%s", mDirectionItem.address.replaceAll(" ", "+"), mDirectionItem.mode);


        if (!mDirectionItem.avoid.isEmpty()) {
            nav += String.format("&avoid=%s", mDirectionItem.avoid);
        }

        LogEx.d(TAG, nav);

        Uri gmmIntentUri = Uri.parse(nav);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }


    private void savePlaceItem() {
        String nav = String.format("google.navigation:q=%s&%s", mDirectionItem.address.replaceAll(" ", "+"), mDirectionItem.mode);

        if (!mDirectionItem.avoid.isEmpty()) {
            nav += String.format("&avoid=%s", mDirectionItem.avoid);
        }

        LogEx.d(TAG, nav);

        Intent i = new Intent();
        i.putExtra(Command.Name.DIRECTION_ITEM, mDirectionItem);
        setResult(RESULT_OK, i);
        finish();
    }


    private void showPlaceApiAlertMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.message_place_api_key)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
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
