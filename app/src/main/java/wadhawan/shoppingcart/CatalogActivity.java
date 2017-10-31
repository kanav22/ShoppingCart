package wadhawan.shoppingcart;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import wadhawan.shoppingcart.data.StockContract.ProductEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    private static final int STOCK_LOADER = 1;
    ProductCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView stockListView = (ListView) findViewById(R.id.list);
        View emptyView = findViewById(R.id.empty_view);
        stockListView.setEmptyView(emptyView);
        mCursorAdapter = new ProductCursorAdapter(this, null);
        stockListView.setAdapter(mCursorAdapter);
        stockListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                Log.i(LOG_TAG, "Current URI: " + currentProductUri);
                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });

        // Kick off the loader
        getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    private void insertProduct() {
        Uri image = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.sample)
                + '/' + getResources().getResourceTypeName(R.drawable.sample) + '/' + getResources().getResourceEntryName(R.drawable.sample));

        Log.i(LOG_TAG, "Example photo uri: " + String.valueOf(image));

        ContentValues values = new ContentValues();
        values.put(ProductEntry.PRODUCT_NAME, "Iphone 7");
        values.put(ProductEntry.PRODUCT_CATEGORY, "APPLE");
        values.put(ProductEntry.PRODUCT_WARRANTY, ProductEntry.PRODUCT_IN_WARRANTY);
        values.put(ProductEntry.PRODUCT_QUANTITY, 13);
        values.put(ProductEntry.PRODUCT_IMAGE, String.valueOf(image));
        values.put(ProductEntry.PRODUCT_SUPPLIER_EMAIL, getString(R.string.steve_email));
        values.put(ProductEntry.PRODUCT_SUPPLIER_NAME, getString(R.string.apple_private));
        values.put(ProductEntry.PRODUCT_PRICE, 600);
        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertProduct();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // Define a projection that specifies the columns from the table we care about
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.PRODUCT_NAME,
                ProductEntry.PRODUCT_CATEGORY,
                ProductEntry.PRODUCT_QUANTITY,
                ProductEntry.PRODUCT_PRICE,
                ProductEntry.PRODUCT_IMAGE
        };

        return new CursorLoader(this,
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    private void deleteAllProducts() {
        int deletedRows = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", deletedRows + " rows deleted from database");
    }
}
