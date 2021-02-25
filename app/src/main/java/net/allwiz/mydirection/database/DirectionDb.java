/*
 * My Direction Android Application
 * @author   Eric(Jaewon) Lee
 * Copyright (C) 2021 Eric(Jaewon) Lee <allwiz@gmail.com>
 * This program is free software: you can redistribute it and/or modify it.
 */
package net.allwiz.mydirection.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;

import net.allwiz.mydirection.R;
import net.allwiz.mydirection.define.Category;
import net.allwiz.mydirection.define.Sort;
import net.allwiz.mydirection.define.Value;
import net.allwiz.mydirection.util.FileUtil;
import net.allwiz.mydirection.util.LogEx;
import net.allwiz.mydirection.util.TimeUtil;

import java.util.ArrayList;


/**
 * Database access and query
 */
public class DirectionDb {
    private static final String TAG = DirectionDb.class.getSimpleName();
    private static final String DATABASE_NAME = "MY_DIRECTION.db";
    private static final int DATABASE_VERSION = 1;

    private static DirectionDbHelper     mDatabaseHelper = null;
    private static DirectionDb           mDatabase = null;
    private static Context mContext = null;

    public static DirectionDb getInstance(Context context) {
        if (mDatabase == null) {
            mContext = context;
            if (mDatabaseHelper == null) {
                mDatabaseHelper = new DirectionDbHelper(context, DATABASE_NAME, DATABASE_VERSION);
            }
            mDatabase = new DirectionDb();
        }
        return mDatabase;
    }


    private void open() {
        if (mDatabaseHelper == null) {
            mDatabaseHelper = new DirectionDbHelper(mContext, DATABASE_NAME, DATABASE_VERSION);
        }
    }


    public void close() {
        try {
            if (mDatabaseHelper != null) {
                mDatabaseHelper.close();
            }
            mDatabaseHelper = null;
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }


    public boolean isOpen() {
        return mDatabaseHelper.getWritableDatabase().isOpen();
    }


    public boolean delete() {
        LogEx.d(TAG, "- [DATABASE] DELETE - MY DIRECTION DB");
        if (isOpen()) {
            close();
        }

        return FileUtil.deleteFile(FileUtil.getDbFilePath(mContext, DATABASE_NAME));
    }


    public void recreate() {
        LogEx.d(TAG, "- [DATABASE] RECREATE - MY DIRECTION DB");
        delete();
        open();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Insert default direction like Home, Work, and Starred.
     */
    public void insertDefaultDirection() {

        long labelIndex = getLabelItemIndex(Category.Label.DEFAULT);
        if (labelIndex == Value.Database.INVALID_ROW_ID) {
            labelIndex = insertLabelItem(Category.DEFAULT, Category.Label.DEFAULT);
        }

        // Home
        if (!existPlaceNameItem(mContext.getString(R.string.home))) {
            insertDirectionItem(new DirectionItem(Category.DEFAULT, labelIndex, mContext.getString(R.string.home)));
        }

        // Work
        if (!existPlaceNameItem(mContext.getString(R.string.work))) {
            insertDirectionItem(new DirectionItem(Category.DEFAULT, labelIndex, mContext.getString(R.string.work)));
        }

        // Starred
        if (!existPlaceNameItem(mContext.getString(R.string.starred))) {
            insertDirectionItem(new DirectionItem(Category.DEFAULT, labelIndex, mContext.getString(R.string.starred)));
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // TABLE: CATEGORY_LABEL

    /**
     * Insert a label
     * @param category      RECENT, FAVORITES, TRAVELS
     * @param name          label name
     * @return              rowId
     */
    public long insertLabelItem(int category, String name) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        long rowId = Value.Database.INVALID_ROW_ID;

        try {
            db.beginTransaction();

            String sql = "INSERT INTO CATEGORY_LABEL (CATEGORY, NAME, CREATE_DATE) VALUES(?, ?, ?)";
            LogEx.d(TAG, String.format("[SQL] INSERT INTO CATEGORY_LABEL (CATEGORY, NAME, CREATE_DATE) VALUES(%d, %s, %s)", category, name, TimeUtil.getLocalTime()));

            SQLiteStatement stmt = db.compileStatement(sql);
            stmt.bindLong(1, category);
            stmt.bindString(2, name);
            stmt.bindString(3, TimeUtil.getLocalTime());
            rowId = stmt.executeInsert();
            db.setTransactionSuccessful();

        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return rowId;
    }


    public String getLabelItemName(long labelIndex) {
        String name = "";
        try {
            String sql = "SELECT NAME FROM CATEGORY_LABEL WHERE ITEM_INDEX = ?";
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            Cursor c = db.rawQuery(sql, new String[] {String.valueOf(labelIndex)});
            if (c != null) {
                if (c.moveToFirst()) {
                    name = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.NAME));
                }
                c.close();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return name;
    }


    public long getLabelItemIndex(String name) {
        long itemIndex = Value.Database.INVALID_ROW_ID;
        try {
            String sql = "SELECT ITEM_INDEX FROM CATEGORY_LABEL WHERE NAME = ?";

            LogEx.d(TAG, String.format("[SQL] SELECT ITEM_INDEX FROM CATEGORY_LABEL WHERE NAME = %s", name));
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            Cursor c = db.rawQuery(sql, new String[]{name});
            if (c != null) {
                if (c.moveToFirst()) {
                    itemIndex = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.ITEM_INDEX));
                }
                c.close();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return itemIndex;
    }


    /**
     * Fetch label names with category
     * @param category      RECENT, FAVORITES, TRAVELS
     * @return              label names
     */
    public ArrayList<String> fetchLabelNameItems(int category) {
        try {
            String sql = "SELECT NAME FROM CATEGORY_LABEL WHERE CATEGORY = ?";
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            Cursor c = db.rawQuery(sql, new String[] {String.valueOf(category)});
            if (c != null) {
                ArrayList<String> items = new ArrayList<String>();
                if (c.moveToFirst()) {
                    do {
                        String item = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.NAME));
                        items.add(item);
                    } while (c.moveToNext());
                }
                c.close();
                return items;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        return null;
    }


    public ArrayList<LabelItem> fetchLabelItems() {
        try {
            String sql = "SELECT ITEM_INDEX, CATEGORY, NAME FROM CATEGORY_LABEL";
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            Cursor c = db.rawQuery(sql, null);
            if (c != null) {
                ArrayList<LabelItem> items = new ArrayList<LabelItem>();
                if (c.moveToFirst()) {
                    do {
                        LabelItem item = new LabelItem();
                        item.category = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.CATEGORY));
                        item.labelIndex = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.ITEM_INDEX));
                        item.name = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.NAME));
                        items.add(item);
                    } while (c.moveToNext());
                }
                c.close();
                return items;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Fetch label items with category
     * @param category      RECENT, FAVORITES, TRAVELS
     * @return              label items
     */
    public ArrayList<LabelDescItem> fetchLabelDescNameItems(int category) {

        try {
            String sql = "SELECT C.ITEM_INDEX AS ITEM_INDEX, C.NAME AS NAME, COUNT(D.ITEM_INDEX) AS ITEM_COUNT, SUM(D.HIT_COUNT) AS HIT_COUNT, GROUP_CONCAT(D.NAME, ', ') AS NAMES FROM DIRECTION D INNER JOIN CATEGORY_LABEL C ON C.ITEM_INDEX = D.LABEL_ITEM_INDEX WHERE C.CATEGORY = ? GROUP BY D.LABEL_ITEM_INDEX ORDER BY HIT_COUNT DESC";
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            Cursor c = db.rawQuery(sql, new String[] {String.valueOf(category)});
            if (c != null) {
                ArrayList<LabelDescItem> items = new ArrayList<LabelDescItem>();
                if (c.moveToFirst()) {
                    do {
                        LabelDescItem item = new LabelDescItem();
                        item.category = category;
                        item.labelIndex = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.ITEM_INDEX));
                        item.name = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.NAME));
                        item.itemCount = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.ITEM_COUNT));
                        item.itemTotalHitCount = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.HIT_COUNT));
                        item.itemNames = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.NAMES));
                        if (!item.itemNames.isEmpty()) {
                            if (item.itemNames.length() > Value.Max.LENGTH) {
                                item.itemNames = item.itemNames.substring(0, Value.Max.LENGTH);
                            }
                        }

                        LogEx.d(TAG, String.format("LABEL NAME: %s", item.name));
                        items.add(item);
                    } while (c.moveToNext());
                }
                c.close();
                return items;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Fetch label items with category and sort
     * @param category      RECENT, FAVORITES, TRAVELS
     * @return              label items
     */
    public ArrayList<LabelDescItem> fetchLabelDescNameItems(int category, int sortOrder) {

        try {
            String sql = "SELECT C.ITEM_INDEX AS ITEM_INDEX, C.NAME AS NAME, COUNT(D.ITEM_INDEX) AS ITEM_COUNT, SUM(D.HIT_COUNT) AS HIT_COUNT, GROUP_CONCAT(D.NAME, ', ') AS NAMES FROM DIRECTION D INNER JOIN CATEGORY_LABEL C ON C.ITEM_INDEX = D.LABEL_ITEM_INDEX WHERE C.CATEGORY = ? GROUP BY D.LABEL_ITEM_INDEX " + toStringSortOrder(sortOrder);
            LogEx.d(TAG, String.format("[SQL] SELECT C.ITEM_INDEX AS ITEM_INDEX, C.NAME AS NAME, COUNT(D.ITEM_INDEX) AS ITEM_COUNT, SUM(D.HIT_COUNT) AS HIT_COUNT, GROUP_CONCAT(D.NAME, ', ') AS NAMES FROM DIRECTION D INNER JOIN CATEGORY_LABEL C ON C.ITEM_INDEX = D.LABEL_ITEM_INDEX WHERE C.CATEGORY = %d GROUP BY D.LABEL_ITEM_INDEX %s", category, toStringSortOrder(sortOrder)));
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            Cursor c = db.rawQuery(sql, new String[] {String.valueOf(category)});
            if (c != null) {
                ArrayList<LabelDescItem> items = new ArrayList<LabelDescItem>();
                if (c.moveToFirst()) {
                    do {
                        LabelDescItem item = new LabelDescItem();
                        item.category = category;
                        item.labelIndex = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.ITEM_INDEX));
                        item.name = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.NAME));
                        item.itemCount = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.ITEM_COUNT));
                        item.itemTotalHitCount = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.HIT_COUNT));
                        item.itemNames = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.NAMES));
                        if (!item.itemNames.isEmpty()) {
                            if (item.itemNames.length() > Value.Max.LENGTH) {
                                item.itemNames = item.itemNames.substring(0, Value.Max.LENGTH);
                            }
                        }

                        LogEx.d(TAG, String.format("LABEL NAME: %s", item.name));
                        items.add(item);
                    } while (c.moveToNext());
                }
                c.close();
                return items;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        return null;
    }


    public boolean existLabelNameItem(String name) {
        int count = 0;
        try {
            String sql = "SELECT * FROM CATEGORY_LABEL WHERE NAME = ?";
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            Cursor c = db.rawQuery(sql, new String[] {name});
            if (c != null) {
                if (c.moveToFirst()) {
                    count = c.getCount();
                }
                c.close();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return (count > 0 ? true : false);
    }


    public int updateLabelItemName(int labelIndex, String name) {
        int ret = Value.Database.QUERY_SUCCESS;
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            String sql = "UPDATE CATEGORY_LABEL SET NAME = ? WHERE ITEM_INDEX = ?";
            SQLiteStatement stmt = db.compileStatement(sql);
            stmt.bindString(1, name);
            stmt.bindLong(2, labelIndex);
            stmt.execute();
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
            ret = Value.Database.QUERY_FAIL;
        } finally {
            db.endTransaction();
        }
        return ret;
    }


    /**
     * Delete all places items with label index
     * @param labelIndex        label index
     */
    public void deleteLabelAllItems(long labelIndex) {
        deleteDirectionLabelAllItem(labelIndex);
        deleteLabelItem(labelIndex);
    }


    public int deleteLabelItem(long labelIndex) {
        int ret = Value.Database.QUERY_SUCCESS;
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            String sql = "DELETE FROM CATEGORY_LABEL WHERE ITEM_INDEX = ?";

            SQLiteStatement stmt = db.compileStatement(sql);
            stmt.bindLong(1, labelIndex);
            stmt.execute();
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
            ret = Value.Database.QUERY_FAIL;
        } finally {
            db.endTransaction();
        }
        return ret;
    }


    public int getLabelItemCount(int category) {
        int count = 0;
        try {
            String sql = "SELECT * FROM CATEGORY_LABEL WHERE CATEGORY = ?";
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            Cursor c = db.rawQuery(sql, new String[] {String.valueOf(category)});
            if (c != null) {
                if (c.moveToFirst()) {
                    count = c.getCount();
                }
                c.close();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return count;
    }




    ////////////////////////////////////////////////////////////////////////////////////////////////
    // TABLE: DIRECTION
    public long insertDirectionItem(DirectionItem item) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        long rowId = Value.Database.INVALID_ROW_ID;

        try {
            db.beginTransaction();

            String date = TimeUtil.getLocalTime();
            String sql = "INSERT INTO DIRECTION (CATEGORY, LABEL_ITEM_INDEX, ADDRESS, NAME, LATITUDE, LONGITUDE, MODE, AVOID, ACCESS_DATE, CREATE_DATE) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            LogEx.d(TAG, String.format("INSERT INTO DIRECTION (CATEGORY, LABEL_ITEM_INDEX, ADDRESS, NAME, LATITUDE, LONGITUDE, MODE, AVOID, ACCESS_DATE, CREATE_DATE) VALUES(%d, %d, %s, %s, %s, %s, %s, %s, %s, %s)", item.category, item.labelIndex, item.address, item.name, item.latitude, item.longitude, item.mode, item.avoid, date, date));

            SQLiteStatement stmt = db.compileStatement(sql);
            stmt.bindLong(1, item.category);
            stmt.bindLong(2, item.labelIndex);
            stmt.bindString(3, item.address);
            stmt.bindString(4, item.name);
            stmt.bindString(5, item.latitude);
            stmt.bindString(6, item.longitude);
            stmt.bindString(7, item.mode);
            stmt.bindString(8, item.avoid);
            stmt.bindString(9, date);
            stmt.bindString(10, date);
            rowId = stmt.executeInsert();
            db.setTransactionSuccessful();

        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return rowId;
    }


    public int updateDirectionItem(DirectionItem item) {
        int ret = Value.Database.QUERY_SUCCESS;
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            String sql = "UPDATE DIRECTION SET CATEGORY = ?, LABEL_ITEM_INDEX = ?, ADDRESS = ?, NAME = ?, LATITUDE = ?, LONGITUDE = ?, MODE = ?, AVOID = ?, ACCESS_DATE = ?  WHERE ITEM_INDEX = ?";
            SQLiteStatement stmt = db.compileStatement(sql);
            stmt.bindLong(1, item.category);
            stmt.bindLong(2, item.labelIndex);
            stmt.bindString(3, item.address);
            stmt.bindString(4, item.name);
            stmt.bindString(5, item.latitude);
            stmt.bindString(6, item.longitude);
            stmt.bindString(7, item.mode);
            stmt.bindString(8, item.avoid);
            stmt.bindString(9, TimeUtil.getLocalTime());
            stmt.bindLong(10, item.itemIndex);
            stmt.execute();
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
            ret = Value.Database.QUERY_FAIL;
        } finally {
            db.endTransaction();
        }
        return ret;
    }


    public int updateDirectionItemLabel(long itemIndex, long newlabelIndex) {
        int ret = Value.Database.QUERY_SUCCESS;
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            String sql = "UPDATE DIRECTION SET LABEL_ITEM_INDEX = ? WHERE ITEM_INDEX = ?";
            SQLiteStatement stmt = db.compileStatement(sql);
            stmt.bindLong(1, newlabelIndex);
            stmt.bindLong(2, itemIndex);
            stmt.execute();
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
            ret = Value.Database.QUERY_FAIL;
        } finally {
            db.endTransaction();
        }
        return ret;
    }


    public int updateDirectionItemSharedStatus(long itemIndex, int shared) {
        int ret = Value.Database.QUERY_SUCCESS;
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            String sql = "UPDATE DIRECTION SET SHARED = ? WHERE ITEM_INDEX = ?";
            SQLiteStatement stmt = db.compileStatement(sql);
            stmt.bindLong(1, shared);
            stmt.bindLong(2, itemIndex);
            stmt.execute();
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
            ret = Value.Database.QUERY_FAIL;
        } finally {
            db.endTransaction();
        }
        return ret;
    }


    public int increaseDirectionItemHitCount(long itemIndex) {
        int ret = Value.Database.QUERY_SUCCESS;
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            String sql = "UPDATE DIRECTION SET HIT_COUNT = HIT_COUNT + 1, ACCESS_DATE = ? WHERE ITEM_INDEX = ?";
            SQLiteStatement stmt = db.compileStatement(sql);
            stmt.bindString(1, TimeUtil.getLocalTime());
            stmt.bindLong(2, itemIndex);
            stmt.execute();
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
            ret = Value.Database.QUERY_FAIL;
        } finally {
            db.endTransaction();
        }
        return ret;
    }


    public DirectionItem getDirectionItem(String name) {
        DirectionItem item = new DirectionItem();
        try {
            String sql = "SELECT ITEM_INDEX, CATEGORY, LABEL_ITEM_INDEX, ADDRESS, LATITUDE, LONGITUDE, MODE, AVOID, HIT_COUNT, ACCESS_DATE FROM DIRECTION WHERE NAME = ?";
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            Cursor c = db.rawQuery(sql, new String[] {name});

            if (c != null) {
                if (c.moveToFirst()) {
                    item.itemIndex = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.ITEM_INDEX));
                    item.category = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.CATEGORY));
                    item.labelIndex = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.LABEL_ITEM_INDEX));
                    item.address = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.ADDRESS));
                    item.name = name;
                    item.latitude = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.LATITUDE));
                    item.longitude = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.LONGITUDE));
                    item.mode = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.MODE));
                    item.avoid = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.AVOID));
                    item.hitCount = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.HIT_COUNT));
                    item.accessDate = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.ACCESS_DATE));
                }
                c.close();
                return item;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return null;
    }


    public DirectionItem getDirectionItem(long itemIndex) {
        DirectionItem item = new DirectionItem();
        try {
            String sql = "SELECT CATEGORY, LABEL_ITEM_INDEX, NAME, ADDRESS, LATITUDE, LONGITUDE, MODE, AVOID, HIT_COUNT, ACCESS_DATE FROM DIRECTION WHERE ITEM_INDEX = ?";
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            Cursor c = db.rawQuery(sql, new String[] {String.valueOf(itemIndex)});

            if (c != null) {
                if (c.moveToFirst()) {
                    item.itemIndex = itemIndex;
                    item.category = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.CATEGORY));
                    item.labelIndex = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.LABEL_ITEM_INDEX));
                    item.address = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.ADDRESS));
                    item.name = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.NAME));
                    item.latitude = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.LATITUDE));
                    item.longitude = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.LONGITUDE));
                    item.mode = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.MODE));
                    item.avoid = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.AVOID));
                    item.hitCount = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.HIT_COUNT));
                    item.accessDate = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.ACCESS_DATE));
                }
                c.close();
                return item;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return null;
    }


    public int getDirectionItemCount(long labelIndex) {
        int count = 0;
        try {
            String sql = "SELECT * FROM DIRECTION WHERE LABEL_ITEM_INDEX = ?";
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            Cursor c = db.rawQuery(sql, new String[] {String.valueOf(labelIndex)});
            if (c != null) {
                if (c.moveToFirst()) {
                    count = c.getCount();
                }
                c.close();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return count;
    }


    public ArrayList<DirectionItem> fetchDirectionItems(int sortOrder) {
        try {
            String sql = "SELECT ITEM_INDEX, LABEL_ITEM_INDEX, ADDRESS, NAME, LATITUDE, LONGITUDE, MODE, AVOID, HIT_COUNT, ACCESS_DATE FROM DIRECTION WHERE HIT_COUNT > 0 " + toStringSortOrder(sortOrder);
            LogEx.d(TAG, sql);
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            Cursor c = db.rawQuery(sql, null);

            if (c != null) {
                ArrayList<DirectionItem> items = new ArrayList<DirectionItem>();
                if (c.moveToFirst()) {
                    do {
                        DirectionItem item = new DirectionItem();
                        item.itemIndex = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.ITEM_INDEX));
                        item.category = Category.RECENT;
                        item.labelIndex = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.LABEL_ITEM_INDEX));
                        item.address = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.ADDRESS));
                        item.name = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.NAME));
                        item.latitude = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.LATITUDE));
                        item.longitude = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.LONGITUDE));
                        item.mode = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.MODE));
                        item.avoid = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.AVOID));
                        item.hitCount = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.HIT_COUNT));
                        String accessDate = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.ACCESS_DATE));
                        item.accessDate = TimeUtil.convertTimeToDate(c.getString(c.getColumnIndex(DirectionDbHelper.Columns.ACCESS_DATE)));

                        if (!item.address.isEmpty()) {
                            items.add(item);
                            LogEx.d(TAG, String.format("ITEM IDX: %d, LABEL IDX: %d, NAME: %s, HIT COUTN: %d, ACCESS DATE: %s", item.itemIndex, item.labelIndex, item.name, item.hitCount, accessDate));
                        }
                    } while (c.moveToNext());
                }
                c.close();
                return items;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return null;
    }


    public ArrayList<DirectionItem> fetchDirectionItems(int category, int sortOrder) {
        try {
            String sql = "SELECT ITEM_INDEX, LABEL_ITEM_INDEX, ADDRESS, NAME, LATITUDE, LONGITUDE, MODE, AVOID, HIT_COUNT, ACCESS_DATE FROM DIRECTION WHERE CATEGORY = ? " + toStringSortOrder(sortOrder);
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            Cursor c = db.rawQuery(sql, new String[] {String.valueOf(category)});

            if (c != null) {
                ArrayList<DirectionItem> items = new ArrayList<DirectionItem>();
                if (c.moveToFirst()) {
                    do {
                        DirectionItem item = new DirectionItem();
                        item.itemIndex = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.ITEM_INDEX));
                        item.category = category;
                        item.labelIndex = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.LABEL_ITEM_INDEX));
                        item.address = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.ADDRESS));
                        item.name = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.NAME));
                        item.latitude = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.LATITUDE));
                        item.longitude = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.LONGITUDE));
                        item.mode = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.MODE));
                        item.avoid = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.AVOID));
                        item.hitCount = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.HIT_COUNT));
                        item.accessDate = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.ACCESS_DATE));

                        items.add(item);
                    } while (c.moveToNext());
                }
                c.close();
                return items;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return null;
    }


    public ArrayList<DirectionItem> fetchDirectionItems(int category, long labelIndex, int sortOrder) {
        try {
            String sql = "SELECT ITEM_INDEX, ADDRESS, NAME, LATITUDE, LONGITUDE, MODE, AVOID, HIT_COUNT, ACCESS_DATE FROM DIRECTION WHERE CATEGORY = ? AND LABEL_ITEM_INDEX = ? " + toStringSortOrder(sortOrder);
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            Cursor c = db.rawQuery(sql, new String[] {String.valueOf(category), String.valueOf(labelIndex)});

            if (c != null) {
                ArrayList<DirectionItem> items = new ArrayList<DirectionItem>();
                if (c.moveToFirst()) {
                    do {
                        DirectionItem item = new DirectionItem();
                        item.itemIndex = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.ITEM_INDEX));
                        item.category = category;
                        item.labelIndex = labelIndex;
                        item.address = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.ADDRESS));
                        item.name = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.NAME));
                        item.latitude = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.LATITUDE));
                        item.longitude = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.LONGITUDE));
                        item.mode = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.MODE));
                        item.avoid = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.AVOID));
                        item.hitCount = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.HIT_COUNT));
                        item.accessDate = TimeUtil.convertTimeToDate(c.getString(c.getColumnIndex(DirectionDbHelper.Columns.ACCESS_DATE)));

                        items.add(item);
                    } while (c.moveToNext());
                }
                c.close();
                return items;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return null;
    }


    public ArrayList<DirectionItem> fetchDirectionItems(String query, int sortOrder) {
        try {
            String sql = "SELECT CATEGORY, LABEL_ITEM_INDEX, ITEM_INDEX, ADDRESS, NAME, LATITUDE, LONGITUDE, MODE, AVOID, HIT_COUNT, ACCESS_DATE FROM DIRECTION WHERE NAME LIKE ?  " + toStringSortOrder(sortOrder);
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            Cursor c = db.rawQuery(sql, new String[]{"%" + query + "%"});

            if (c != null) {
                ArrayList<DirectionItem> items = new ArrayList<DirectionItem>();
                if (c.moveToFirst()) {
                    do {
                        DirectionItem item = new DirectionItem();
                        item.category = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.CATEGORY));
                        item.labelIndex = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.LABEL_ITEM_INDEX));
                        item.itemIndex = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.ITEM_INDEX));
                        item.address = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.ADDRESS));
                        item.name = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.NAME));
                        item.latitude = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.LATITUDE));
                        item.longitude = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.LONGITUDE));
                        item.mode = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.MODE));
                        item.avoid = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.AVOID));
                        item.hitCount = c.getInt(c.getColumnIndex(DirectionDbHelper.Columns.HIT_COUNT));
                        item.accessDate = TimeUtil.convertTimeToDate(c.getString(c.getColumnIndex(DirectionDbHelper.Columns.ACCESS_DATE)));

                        items.add(item);
                    } while (c.moveToNext());
                }
                c.close();
                return items;
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean existPlaceNameItem(String name) {
        int count = 0;
        try {
            String sql = "SELECT * FROM DIRECTION WHERE NAME = ?";
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            Cursor c = db.rawQuery(sql, new String[] {name});
            if (c != null) {
                if (c.moveToFirst()) {
                    count = c.getCount();
                }
                c.close();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return (count > 0 ? true : false);
    }


    public boolean existPlaceAddressItem(String address) {
        int count = 0;
        try {
            String sql = "SELECT * FROM DIRECTION WHERE ADDRESS = ?";
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            Cursor c = db.rawQuery(sql, new String[] {address});
            if (c != null) {
                if (c.moveToFirst()) {
                    count = c.getCount();
                }
                c.close();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return (count > 0 ? true : false);
    }


    public boolean isEmptyPlaceAddressItem(String name) {
        String address = "";
        try {
            String sql = "SELECT ADDRESS FROM DIRECTION WHERE NAME = ?";
            SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
            Cursor c = db.rawQuery(sql, new String[] {name});
            if (c != null) {
                if (c.moveToFirst()) {
                    address = c.getString(c.getColumnIndex(DirectionDbHelper.Columns.ADDRESS));
                }
                c.close();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        return (address.isEmpty() ? true : false);
    }


    public int deleteDirectionItem(long itemIndex) {
        int ret = Value.Database.QUERY_SUCCESS;
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            String sql = "DELETE FROM DIRECTION WHERE ITEM_INDEX = ?";

            SQLiteStatement stmt = db.compileStatement(sql);
            stmt.bindLong(1, itemIndex);
            stmt.execute();
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
            ret = Value.Database.QUERY_FAIL;
        } finally {
            db.endTransaction();
        }
        return ret;
    }


    private int deleteDirectionLabelAllItem(long labelIndex) {
        int ret = Value.Database.QUERY_SUCCESS;
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            String sql = "DELETE FROM DIRECTION WHERE LABEL_ITEM_INDEX = ?";

            SQLiteStatement stmt = db.compileStatement(sql);
            stmt.bindLong(1, labelIndex);
            stmt.execute();
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            e.printStackTrace();
            ret = Value.Database.QUERY_FAIL;
        } finally {
            db.endTransaction();
        }
        return ret;
    }


    private String toStringSortOrder(int sortOrder) {
        String orderby = " ORDER BY ";
        switch (sortOrder) {
            case Sort.HIT_COUNT:
                orderby += DirectionDbHelper.Columns.HIT_COUNT;
                break;
            case Sort.ACCESS_DATE:
                orderby += DirectionDbHelper.Columns.ACCESS_DATE;
                orderby += " DESC";
                break;
            case Sort.CREATE_DATE:
                orderby += DirectionDbHelper.Columns.CREATE_DATE;
                break;
            case Sort.NAME:
                orderby += DirectionDbHelper.Columns.NAME;
                break;
            default:
                orderby += DirectionDbHelper.Columns.HIT_COUNT;
                break;
        }
        return orderby;
    }
}

