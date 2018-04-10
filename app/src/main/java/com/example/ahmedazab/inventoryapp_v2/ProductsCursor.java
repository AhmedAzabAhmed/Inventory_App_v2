package com.example.ahmedazab.inventoryapp_v2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmedazab.inventoryapp_v2.data.ProductsContract.ProductsEntry;


public class ProductsCursor extends CursorAdapter {

    public ProductsCursor(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.list_items, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        TextView tvProductName = view.findViewById(R.id.product_item);
        TextView tvProductQuatity = view.findViewById(R.id.itemQuantity);
        TextView tvProductPrice = view.findViewById(R.id.itemPrice);

        int price = cursor.getInt(cursor.getColumnIndex(ProductsEntry.COLUMN_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndex(ProductsEntry.COLUMN_QUANTITY));

        final int position = cursor.getPosition();

        tvProductName.setText(cursor.getString(cursor.getColumnIndexOrThrow(ProductsEntry.COLUMN_PRODUCT_NAME)));
        tvProductQuatity.setText(context.getString(R.string.quantity) + String.valueOf(quantity));
        tvProductPrice.setText(context.getString(R.string.price) + String.valueOf(price));

        Button btn_Sale = view.findViewById(R.id.btn_saleItem);
        btn_Sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPosition(position);
                int quantity = cursor.getInt(cursor.getColumnIndex(ProductsEntry.COLUMN_QUANTITY));
                int productID = cursor.getInt(cursor.getColumnIndex(ProductsEntry._ID));
                quantity--;
                try {
                    updatesData(productID, quantity, context);
                } catch (IllegalArgumentException ex) {
                    quantity++;
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cursor.moveToPosition(position);
                int id = cursor.getInt(cursor.getColumnIndex(ProductsEntry._ID));
                Uri productUri = Uri.withAppendedPath(ProductsEntry.CONTENT_URI, "/" + id);
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.setData(productUri);
                context.startActivity(intent);
            }
        });
    }

    private void updatesData(int id, int quantity, Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductsEntry.COLUMN_QUANTITY, quantity);
        Uri uri = Uri.withAppendedPath(ProductsEntry.CONTENT_URI, "/" + id);
        context.getContentResolver().update(uri, contentValues, null, null);
    }


}
