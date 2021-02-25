/*
 * My Direction Android Application
 * @author   Eric(Jaewon) Lee
 * Copyright (C) 2021 Eric(Jaewon) Lee <allwiz@gmail.com>
 * This program is free software: you can redistribute it and/or modify it.
 */
package net.allwiz.mydirection.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DirectionDbHelper extends SQLiteOpenHelper {

    public DirectionDbHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }


    public interface Columns {
        public static final String ITEM_INDEX = "ITEM_INDEX";    // auto increment
        public static final String CATEGORY = "CATEGORY";    // CATEGORY
        public static final String LABEL_ITEM_INDEX = "LABEL_ITEM_INDEX";    // LABEL
        public static final String NAME = "NAME";    // NAME
        public static final String ADDRESS = "ADDRESS";    // ADDRESS
        public static final String LATITUDE = "LATITUDE";    // LATITUDE
        public static final String LONGITUDE = "LONGITUDE";    // LONGITUDE
        public static final String MODE = "MODE";    // MODE
        public static final String AVOID = "AVOID";    // AVOID
        public static final String SHARED = "SHARED";    // SHARED
        public static final String HIT_COUNT = "HIT_COUNT";    // HIT_COUNT
        public static final String ACCESS_DATE = "ACCESS_DATE";    // ACCESS_DATE
        public static final String CREATE_DATE = "CREATE_DATE";    // CREATE_DATE

        public static final String ITEM_COUNT = "ITEM_COUNT";    // ITEM_COUNT
        public static final String NAMES = "NAMES";    // NAME
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createTables(SQLiteDatabase db) {
        createCategoryLabelTable(db);
        createDirectionTable(db);
    }


    private void createCategoryLabelTable(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS CATEGORY_LABEL (ITEM_INDEX INTEGER PRIMARY KEY AUTOINCREMENT, CATEGORY INTEGER NOT NULL, NAME TEXT DEFAULT '', CREATE_DATE TEXT NOT NULL);");
        sql.append("CREATE INDEX IDX_CATEGORY_LABEL_NAME ON CATEGORY_LABEL (NAME);");
        db.execSQL(sql.toString());
    }


    private void createDirectionTable(SQLiteDatabase db) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS DIRECTION (ITEM_INDEX INTEGER PRIMARY KEY AUTOINCREMENT, CATEGORY INTEGER NOT NULL, LABEL_ITEM_INDEX INTEGER NOT NULL, ADDRESS TEXT NOT NULL, NAME TEXT NOT NULL, LATITUDE TEXT DEFAULT '', LONGITUDE TEXT DEFAULT '', MODE TEXT DEFAULT 'd', AVOID TEXT DEFAULT '', HIT_COUNT INTEGER DEFAULT 0, PRIORITY INTEGER DEFAULT 0, SHARED INTEGER DEFAULT 0, ORDER_NO INTEGER DEFAULT 0, ACCESS_DATE TEXT NOT NULL, CREATE_DATE TEXT NOT NULL);");
        sql.append("CREATE INDEX IDX_DIRECTION_CATEGORY ON DIRECTION (CATEGORY);");
        sql.append("CREATE INDEX IDX_DIRECTION_CATEGORY_HIT_COUNT ON DIRECTION (CATEGORY, HIT_COUNT);");
        sql.append("CREATE INDEX IDX_DIRECTION_DATE ON DIRECTION (ACCESS_DATE);");
        db.execSQL(sql.toString());
    }
}

