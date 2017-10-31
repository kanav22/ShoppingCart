package wadhawan.shoppingcart.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class StockContract {

    public static final String LOG_TAG = StockProvider.class.getSimpleName();
    public static final String CONTENT_AUTHORITY = "wadhawan.shoppingcart.data";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";

    private StockContract() {
    }

    public static abstract class ProductEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public final static String TABLE_NAME = "products";

        public final static String _ID = BaseColumns._ID;

        public static final String PRODUCT_IMAGE = "picture";

        public final static String PRODUCT_PRICE = "price";
        public final static String PRODUCT_NAME = "name";

        public final static String PRODUCT_CATEGORY = "category";

        public final static String PRODUCT_WARRANTY = "warranty";

        public final static String PRODUCT_QUANTITY = "quantity";

        public final static String PRODUCT_SUPPLIER_NAME = "supplierName";

        public final static String PRODUCT_SUPPLIER_EMAIL = "supplierEmail";

        public static final int PRODUCT_WARRANTY_UNKNOWN = 0;
        public static final int PRODUCT_IN_WARRANTY = 1;
        public static final int PRODUCT_OUT_WARRANTY = 2;

        public static boolean inWarranty(int warranty) {
            if (warranty == PRODUCT_WARRANTY_UNKNOWN || warranty == PRODUCT_IN_WARRANTY || warranty == PRODUCT_OUT_WARRANTY) {
                return true;
            }
            return false;
        }
    }

}