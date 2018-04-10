package com.example.ahmedazab.inventoryapp_v2;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ahmedazab.inventoryapp_v2.data.ProductsContract.ProductsEntry;

public class EditorActivity extends AppCompatActivity {

    private Uri productUri = null;
    private EditText et_ProductName;
    private EditText et_ProductQuantity;
    private EditText et_productPrice;
    private EditText et_SupplierName;
    private EditText et_SupplierEmail;
    private EditText et_SupplierPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        et_ProductName = findViewById(R.id.et_ProductName);
        et_ProductQuantity = findViewById(R.id.et_ProductQuantity);
        et_productPrice = findViewById(R.id.et_ProductPrice);
        et_SupplierName = findViewById(R.id.et_SupplierName);
        et_SupplierEmail = findViewById(R.id.et_SupplierEmail);
        et_SupplierPhone = findViewById(R.id.et_SupplierNumber);

        try {
            if (getIntent().getData() != null) {
                productUri = getIntent().getData();
                setTitle(R.string.updateProducts);
                getProductsDetails();
            }
        } catch (NullPointerException ex) {
            Log.e("EditorActivity", "productUri is Empty");
            setTitle(R.string.addProducts);
        } catch (CursorIndexOutOfBoundsException e) {
            Log.e("EditorActivity", e.getMessage());
            setTitle(R.string.addProducts);
        }
    }

    private ContentValues getData() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductsEntry.COLUMN_PRODUCT_NAME, et_ProductName.getText().toString().trim());
        contentValues.put(ProductsEntry.COLUMN_QUANTITY, et_ProductQuantity.getText().toString().trim());
        contentValues.put(ProductsEntry.COLUMN_PRICE, et_productPrice.getText().toString().trim());
        contentValues.put(ProductsEntry.COLUMN_SUPPLIER, et_SupplierName.getText().toString().trim());
        contentValues.put(ProductsEntry.COLUMN_SUPPLIER_EMAIL, et_SupplierEmail.getText().toString().trim());
        contentValues.put(ProductsEntry.COLUMN_SUPPLIER_PHONE, et_SupplierPhone.getText().toString().trim());
        return contentValues;
    }

    private void clearInputViews() {
        et_ProductName.setText("");
        et_ProductQuantity.setText("");
        et_productPrice.setText("");
        et_SupplierName.setText("");
        et_SupplierEmail.setText("");
        et_SupplierPhone.setText("");
    }


    private boolean checkForData() {

        if (!et_ProductName.getText().toString().trim().equals(""))
            return true;
        else if (!et_ProductQuantity.getText().toString().trim().equals(""))
            return true;
        else if (!et_productPrice.getText().toString().trim().equals(""))
            return true;
        else if (!et_SupplierName.getText().toString().trim().equals(""))
            return true;
        else if (!et_SupplierEmail.getText().toString().trim().equals(""))
            return true;
        else return !et_SupplierPhone.getText().toString().trim().equals("");
    }

    private void getProductsDetails() {

        String[] projection = {ProductsEntry._ID,
                ProductsEntry.COLUMN_PRODUCT_NAME,
                ProductsEntry.COLUMN_QUANTITY,
                ProductsEntry.COLUMN_PRICE,
                ProductsEntry.COLUMN_SUPPLIER,
                ProductsEntry.COLUMN_SUPPLIER_EMAIL,
                ProductsEntry.COLUMN_SUPPLIER_PHONE};
        Cursor cursor = null;

        try {
            cursor = getContentResolver().query(ProductsEntry.CONTENT_URI, projection, null, null, null);
        } catch (Exception ex) {
            Log.e("EditorActivity", ex.getMessage());
        }
        cursor.moveToFirst();

        et_ProductName.setText(cursor.getString(cursor.getColumnIndex(ProductsEntry.COLUMN_PRODUCT_NAME)));
        et_ProductQuantity.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(ProductsEntry.COLUMN_QUANTITY))));
        et_productPrice.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex(ProductsEntry.COLUMN_PRICE))));
        et_SupplierName.setText(cursor.getString(cursor.getColumnIndex(ProductsEntry.COLUMN_SUPPLIER)));
        et_SupplierEmail.setText(cursor.getString(cursor.getColumnIndex(ProductsEntry.COLUMN_SUPPLIER_EMAIL)));
        et_SupplierPhone.setText(cursor.getString(cursor.getColumnIndex(ProductsEntry.COLUMN_SUPPLIER_PHONE)));
    }


    private void updateProductData() {
        try {
            int dbResult = getContentResolver().update(productUri, getData(), null, null);
            clearInputViews();
            Toast.makeText(this, "" + dbResult + " Data Updated", Toast.LENGTH_LONG).show();
        } catch (IllegalArgumentException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_add:

                if (productUri != null) {
                    updateProductData();
                } else {

                    try {
                        getContentResolver().insert(ProductsEntry.CONTENT_URI, getData());
                        clearInputViews();
                        Toast.makeText(this, getString(R.string.successful), Toast.LENGTH_LONG).show();
                    } catch (IllegalArgumentException ex) {
                        Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            default:

                if (checkForData()) {
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            };
                    showUnsavedChangesDialog(discardButtonClickListener);
                } else
                    finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        if (checkForData()) {
            DialogInterface.OnClickListener discardButtonClickListener =
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    };
            showUnsavedChangesDialog(discardButtonClickListener);
        } else
            finish();
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.cancelDeletion));
        builder.setPositiveButton(getString(R.string.agree), discardButtonClickListener);
        builder.setNegativeButton(getString(R.string.disagree), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
