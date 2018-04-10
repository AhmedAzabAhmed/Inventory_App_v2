package com.example.ahmedazab.inventoryapp_v2;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmedazab.inventoryapp_v2.data.ProductsContract.ProductsEntry;

public class DetailsActivity extends AppCompatActivity {

    private int ProductQuantity;
    private String supplierEmail;
    private String supplierPhone;
    private TextView tv_ProductName;
    private TextView tv_ProductQuantity;
    private TextView tv_ProductPrice;
    private TextView tv_SupplierName;
    private TextView tv_SupplierEmail;
    private TextView tv_SupplierPhone;
    private Button btn_AddOne;
    private Button btn_MinusOne;
    private Button btn_DeleteProduct;
    private Button btn_Call;
    private Button btn_SendEmail;
    private Cursor productCursor;
    private Uri productUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //select views
        tv_ProductName = findViewById(R.id.tv_DetailsProductName);
        tv_ProductQuantity = findViewById(R.id.tv_DetailsProductQuantity);
        tv_ProductPrice = findViewById(R.id.tv_DetailsProductPrice);
        tv_SupplierName = findViewById(R.id.tv_DetailsSupplierName);
        tv_SupplierEmail = findViewById(R.id.tv_DetailsSupplierMail);
        tv_SupplierPhone = findViewById(R.id.tv_DetailsSupplierPhone);
        btn_AddOne = findViewById(R.id.btn_DetailsAdd);
        btn_MinusOne = findViewById(R.id.btn_DetailsMinus);
        btn_DeleteProduct = findViewById(R.id.btn_DetailsDelete);
        btn_Call = findViewById(R.id.bin_DetailsCall);
        btn_SendEmail = findViewById(R.id.btn_DetailsEmail);

        final Uri uri = getIntent().getData();
        productUri = uri;
        updateViews(uri);

        btn_DeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getContentResolver().delete(uri, null, null);
                                finish();
                            }
                        };
                showingUnsavedDialog(discardButtonClickListener);
            }
        });

        btn_MinusOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductQuantity--;
                ContentValues contentValues = new ContentValues();
                contentValues.put(ProductsEntry.COLUMN_QUANTITY, ProductQuantity);
                try {
                    getContentResolver().update(uri, contentValues, null, null);
                    tv_ProductQuantity.setText(String.valueOf(ProductQuantity));
                } catch (IllegalArgumentException ex) {
                    ProductQuantity++;
                    Toast.makeText(DetailsActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

        btn_AddOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductQuantity++;
                ContentValues values = new ContentValues();
                values.put(ProductsEntry.COLUMN_QUANTITY, ProductQuantity);
                try {
                    getContentResolver().update(uri, values, null, null);
                    tv_ProductQuantity.setText(String.valueOf(ProductQuantity));
                } catch (IllegalArgumentException ex) {
                    Toast.makeText(DetailsActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeCall(supplierPhone);
            }
        });

        // sends Email to the provider
        btn_SendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String args[] = {supplierEmail};
                sendEmail(args);
            }
        });
    }

    private Cursor query(Uri uri) {

        String[] projection = {ProductsEntry._ID,
                ProductsEntry.COLUMN_PRODUCT_NAME,
                ProductsEntry.COLUMN_PRICE,
                ProductsEntry.COLUMN_QUANTITY,
                ProductsEntry.COLUMN_SUPPLIER,
                ProductsEntry.COLUMN_SUPPLIER_EMAIL,
                ProductsEntry.COLUMN_SUPPLIER_PHONE};
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, projection,
                    null, null, null);
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
        return cursor;
    }

    private void updateViews(Uri uri) {

        productCursor = query(uri);
        productCursor.moveToFirst();
        tv_ProductName.setText(productCursor.getString(productCursor.getColumnIndex(ProductsEntry.COLUMN_PRODUCT_NAME)));
        ProductQuantity = productCursor.getInt(productCursor.getColumnIndex(ProductsEntry.COLUMN_QUANTITY));
        tv_ProductQuantity.setText(String.valueOf(ProductQuantity));
        tv_ProductPrice.setText(productCursor.getString(productCursor.getColumnIndex(ProductsEntry.COLUMN_PRICE)));
        tv_SupplierName.setText(productCursor.getString(productCursor.getColumnIndex(ProductsEntry.COLUMN_SUPPLIER)));
        supplierEmail = productCursor.getString(productCursor.getColumnIndex(ProductsEntry.COLUMN_SUPPLIER_EMAIL));
        tv_SupplierEmail.setText(supplierEmail);
        supplierPhone = productCursor.getString(productCursor.getColumnIndex(ProductsEntry.COLUMN_SUPPLIER_PHONE));
        tv_SupplierPhone.setText(supplierPhone);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateViews(productUri);
    }

    private void showingUnsavedDialog(

            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.check_delete));
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


    private void makeCall(String phone) {

        String phone_Number = "tel:" + phone;
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone_Number));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void sendEmail(String[] addresses) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.update, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_item_add) {
            Intent intent = new Intent(this, EditorActivity.class);
            intent.setData(productUri);
            startActivity(intent);
        } else
            onBackPressed();
        return true;
    }
}
