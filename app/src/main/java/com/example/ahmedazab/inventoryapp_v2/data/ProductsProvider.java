package com.example.ahmedazab.inventoryapp_v2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.ahmedazab.inventoryapp_v2.data.ProductsContract.ProductsEntry;


public class ProductsProvider extends ContentProvider {

    private static final int PRODUCTS = 10;
    private static final int PRODUCT_ID = 20;
    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI(ProductsContract.CONTENT_AUTHORITY, ProductsContract.PATH_PRODUCT, PRODUCTS);
        matcher.addURI(ProductsContract.CONTENT_AUTHORITY, ProductsContract.PATH_PRODUCT + "/#", PRODUCT_ID);
    }

    DbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        int match = matcher.match(uri);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor;
        switch (match) {
            case PRODUCTS:
                cursor = sqLiteDatabase.query(ProductsEntry.TABLE_NAME, selectionArgs, selection,
                        null, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = sqLiteDatabase.query(ProductsEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, null);
                break;
            default:
                throw new IllegalArgumentException("Unknown query " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = matcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductsEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = matcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProducts(uri, contentValues);
            default:
                throw new IllegalArgumentException("Unknown query " + uri);
        }
    }

    private Uri insertProducts(Uri uri, ContentValues contentValues) {

        String name = contentValues.getAsString(ProductsEntry.COLUMN_PRODUCT_NAME);
        if (name == null || name.length() == 0)
            throw new IllegalArgumentException("Enter product name");

        Integer quantity = contentValues.getAsInteger(ProductsEntry.COLUMN_QUANTITY);
        if (quantity == null || quantity < 1)
            throw new IllegalArgumentException("quantity cant be less than or equal zero");

        Integer price = contentValues.getAsInteger(ProductsEntry.COLUMN_PRICE);
        if (price == null || price < 1)
            throw new IllegalArgumentException("price cant be less than or equal Zero");

        String supplier = contentValues.getAsString(ProductsEntry.COLUMN_SUPPLIER);
        if (supplier == null || supplier.length() == 0)
            throw new IllegalArgumentException("Enter supplier name");

        String supplierMail = contentValues.getAsString(ProductsEntry.COLUMN_SUPPLIER_EMAIL);
        if (supplierMail == null || supplierMail.length() == 0)
            throw new IllegalArgumentException("Supplier requires Email");

        String supplierPhone = contentValues.getAsString(ProductsEntry.COLUMN_SUPPLIER_PHONE);
        if (supplierPhone == null || supplierPhone.length() == 0)
            throw new IllegalArgumentException("Enter Supplier Phone Number");

        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        long resultID = sqLiteDatabase.insert(ProductsEntry.TABLE_NAME, null, contentValues);
        if (resultID == -1) {
            Log.e(getContext().toString(), "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, resultID);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        int match = matcher.match(uri);
        int dbResult;
        switch (match) {
            case PRODUCTS:
                dbResult = updateProducts(uri, contentValues, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                dbResult = updateProducts(uri, contentValues, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown query " + uri);
        }
        return dbResult;
    }

    private int updateProducts(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        if (contentValues.containsKey(ProductsEntry.COLUMN_PRODUCT_NAME)) {
            String name = contentValues.getAsString(ProductsEntry.COLUMN_PRODUCT_NAME);
            if (name == null || name.length() == 0)
                throw new IllegalArgumentException("Enter Product name");
        }

        if (contentValues.containsKey(ProductsEntry.COLUMN_QUANTITY)) {
            Integer quantity = contentValues.getAsInteger(ProductsEntry.COLUMN_QUANTITY);
            if (quantity == null || quantity < 0)
                throw new IllegalArgumentException("cant update product with quantity less than or equal Zero");
        }

        if (contentValues.containsKey(ProductsEntry.COLUMN_PRICE)) {
            Integer price = contentValues.getAsInteger(ProductsEntry.COLUMN_PRICE);
            if (price == null || price < 1)
                throw new IllegalArgumentException("Price cannot be less than or equal Zero");
        }

        if (contentValues.containsKey(ProductsEntry.COLUMN_SUPPLIER)) {
            String supplier = contentValues.getAsString(ProductsEntry.COLUMN_SUPPLIER);
            if (supplier == null || supplier.length() == 0)
                throw new IllegalArgumentException("Enter Supplier name");
        }

        if (contentValues.containsKey(ProductsEntry.COLUMN_SUPPLIER_EMAIL)) {
            String supplierMail = contentValues.getAsString(ProductsEntry.COLUMN_SUPPLIER_EMAIL);
            if (supplierMail == null || supplierMail.length() == 0)
                throw new IllegalArgumentException("Enter Supplier Email");
        }

        if (contentValues.containsKey(ProductsEntry.COLUMN_SUPPLIER_PHONE)) {
            String supplierPhone = contentValues.getAsString(ProductsEntry.COLUMN_SUPPLIER_PHONE);
            if (supplierPhone == null || supplierPhone.length() == 0)
                throw new IllegalArgumentException("Enter Supplier Phone Number");
        }

        if (contentValues.size() == 0)
            return 0;

        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        int updatedRows = sqLiteDatabase.update(ProductsEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        if (updatedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updatedRows;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int deletedRows;
        final int match = matcher.match(uri);
        switch (match) {
            case PRODUCTS:
                deletedRows = database.delete(ProductsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deletedRows = database.delete(ProductsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedRows;
    }

}
