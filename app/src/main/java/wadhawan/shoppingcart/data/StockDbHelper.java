package wadhawan.shoppingcart.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public final class StockDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = StockDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "store.db";
    private static final int DATABASE_VERSION = 1;
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + StockContract.ProductEntry.TABLE_NAME;
    public StockDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                StockContract.ProductEntry.TABLE_NAME + "(" +
                StockContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                StockContract.ProductEntry.PRODUCT_NAME + " TEXT NOT NULL, " +
                StockContract.ProductEntry.PRODUCT_CATEGORY + " TEXT, " +
                StockContract.ProductEntry.PRODUCT_WARRANTY + " INTEGER NOT NULL, " +
                StockContract.ProductEntry.PRODUCT_PRICE + " REAL NOT NULL, " +
                StockContract.ProductEntry.PRODUCT_SUPPLIER_NAME + " TEXT, " +
                StockContract.ProductEntry.PRODUCT_SUPPLIER_EMAIL + " TEXT NOT NULL, " +
                StockContract.ProductEntry.PRODUCT_IMAGE + " TEXT NOT NULL, " +
                StockContract.ProductEntry.PRODUCT_QUANTITY + " INTEGER DEFAULT 0);";
        Log.v(LOG_TAG, SQL_CREATE_PRODUCTS_TABLE);
        db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
