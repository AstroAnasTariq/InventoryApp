package com.example.android.bookstoreapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bookstoreapp.data.InventoryContract.InventoryEntry;


public class InventoryDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "products.db";
    private static final int DATABASE_VERSION = 1;

    InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String INTEGER_TYPE = " INTEGER";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ", ";
    private static final String SQL_CREATE_PRODUCT_INFO_TABLE =
            "CREATE TABLE " + InventoryEntry.TABLE_NAME + " (" +
                    InventoryEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    InventoryEntry.COLUMN_PRODUCT_NAME + TEXT_TYPE + " NOT NULL" + COMMA_SEP +
                    InventoryEntry.COLUMN_PRODUCT_IMAGE + TEXT_TYPE + COMMA_SEP +
                    InventoryEntry.COLUMN_PRODUCT_PRICE + INTEGER_TYPE + " NOT NULL" + COMMA_SEP +
                    InventoryEntry.COLUMN_PRODUCT_QUANTITY + INTEGER_TYPE + " NOT NULL DEFAULT 0" + COMMA_SEP +
                    InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME + TEXT_TYPE + " NOT NULL" + COMMA_SEP +
                    InventoryEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL + TEXT_TYPE + " NOT NULL" + COMMA_SEP +
                    InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER + TEXT_TYPE + " );";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PRODUCT_INFO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
