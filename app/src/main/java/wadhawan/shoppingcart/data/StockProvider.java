package wadhawan.shoppingcart.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class StockProvider extends ContentProvider {

    public static final String LOG_TAG = StockProvider.class.getSimpleName();
    private static final int PRODUCTS = 10;
    private static final int PRODUCT_ID = 11;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(StockContract.CONTENT_AUTHORITY, StockContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    private StockDbHelper mDbHelper;
    @Override
    public boolean onCreate() {
        mDbHelper = new StockDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(StockContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);

                break;
            case PRODUCT_ID:
                selection = StockContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(StockContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(StockContract.ProductEntry.PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Please Enter a Product Name");
        }

        Integer quantity = values.getAsInteger(StockContract.ProductEntry.PRODUCT_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Please Enter valid quantity");
        }

        Integer warranty = values.getAsInteger(StockContract.ProductEntry.PRODUCT_WARRANTY);
        if (warranty == null || !StockContract.ProductEntry.inWarranty(warranty)) {
            throw new IllegalArgumentException("Product requires valid warranty");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(StockContract.ProductEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG,"Failed to insert row for" + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = StockContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }

    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(StockContract.ProductEntry.PRODUCT_NAME)) {
            String name = values.getAsString(StockContract.ProductEntry.PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (values.containsKey(StockContract.ProductEntry.PRODUCT_WARRANTY)) {
            Integer warranty = values.getAsInteger(StockContract.ProductEntry.PRODUCT_WARRANTY);
            if (warranty == null || !StockContract.ProductEntry.inWarranty(warranty)) {
                throw new IllegalArgumentException("Please Enter Warranty Status ");
            }
        }

        if (values.containsKey(StockContract.ProductEntry.PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(StockContract.ProductEntry.PRODUCT_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Please Enter valid quantity");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(StockContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int deletedRows;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                deletedRows = database.delete(StockContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case PRODUCT_ID:
                selection = StockContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                deletedRows = database.delete(StockContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (deletedRows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deletedRows;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return StockContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return StockContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with " + match);
        }
    }
}