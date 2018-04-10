package com.example.ahmedazab.inventoryapp_v2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ahmedazab.inventoryapp_v2.data.ProductsContract.ProductsEntry;


public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "product.db";
    private static final int DB_VERSION = 1;

    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " + ProductsEntry.TABLE_NAME + " ("
                + ProductsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductsEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductsEntry.COLUMN_PRICE + " INTEGER NOT NULL, "
                + ProductsEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, "
                + ProductsEntry.COLUMN_IMAGE + " TEXT ,"
                + ProductsEntry.COLUMN_SUPPLIER + " TEXT NOT NULL, "
                + ProductsEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL, "
                + ProductsEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL);";

        // Execute the SQL statement
        sqLiteDatabase.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        String SQL_DELETE_PETS_TABLE = "DROOP TABLE IF EXIST " + ProductsEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_PETS_TABLE);
        onCreate(db);
    }

}
