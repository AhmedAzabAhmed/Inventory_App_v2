package com.example.ahmedazab.inventoryapp_v2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ahmedazab.inventoryapp_v2.data.ProductsContract.ProductsEntry;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;
    ProductsCursor productsCursor;

    ListView listView;
    TextView tv_EmptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.lv_Products);
        tv_EmptyState = findViewById(R.id.tv_EmptyState);
        listView.setEmptyView(tv_EmptyState);
        productsCursor = new ProductsCursor(this, null);
        listView.setAdapter(productsCursor);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projections = {ProductsEntry._ID,
                ProductsEntry.COLUMN_PRODUCT_NAME,
                ProductsEntry.COLUMN_PRICE,
                ProductsEntry.COLUMN_QUANTITY,
                ProductsEntry.COLUMN_SUPPLIER};

        return new CursorLoader(this, ProductsEntry.CONTENT_URI,
                projections, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        productsCursor.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productsCursor.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_item_add) {

            Intent intent = new Intent(this, EditorActivity.class);
            startActivity(intent);
        }
        return true;
    }
}
