package wadhawan.shoppingcart;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import wadhawan.shoppingcart.data.StockContract;

import static android.content.ContentValues.TAG;

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView productPriceTextView = (TextView) view.findViewById(R.id.item_price);
        TextView productNameTextview = (TextView) view.findViewById(R.id.image_name);
        TextView productModelTextView = (TextView) view.findViewById(R.id.item_model);
        TextView productQuantityTextView = (TextView) view.findViewById(R.id.item_quantity);
        ImageView productPicImageView = (ImageView) view.findViewById(R.id.image_image);
        ImageButton buyButton = (ImageButton) view.findViewById(R.id.item_buy_button);

        final int productIdColumnIndex = cursor.getInt(cursor.getColumnIndex(StockContract.ProductEntry._ID));
        int productNameColumnIndex = cursor.getColumnIndex(StockContract.ProductEntry.PRODUCT_NAME);
        int productModelColumnIndex = cursor.getColumnIndex(StockContract.ProductEntry.PRODUCT_CATEGORY);
        int productPriceColumnIndex = cursor.getColumnIndex(StockContract.ProductEntry.PRODUCT_PRICE);
        int productQuantityColumnIndex = cursor.getColumnIndex(StockContract.ProductEntry.PRODUCT_QUANTITY);
        int productpicColumnIndex = cursor.getColumnIndex(StockContract.ProductEntry.PRODUCT_IMAGE);

        String productName = cursor.getString(productNameColumnIndex);
        String productModel = cursor.getString(productModelColumnIndex);
        String productPrice = cursor.getString(productPriceColumnIndex);
        final int quantityProduct = cursor.getInt(productQuantityColumnIndex);
        String imageUri = cursor.getString(productpicColumnIndex);
        Uri productImageUri = Uri.parse(imageUri);

        if (TextUtils.isEmpty(productModel)) {
            productModelTextView.setVisibility(View.GONE);
        }

        productNameTextview.setText(productName);
        productModelTextView.setText(productModel);
        productPriceTextView.setText(productPrice);
        productQuantityTextView.setText(String.valueOf(quantityProduct));
        productPicImageView.setImageURI(productImageUri);

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri productUri = ContentUris.withAppendedId(StockContract.ProductEntry.CONTENT_URI, productIdColumnIndex);
                adjustQuantity(context, productUri, quantityProduct);
            }
        });
    }

    private void adjustQuantity(Context context, Uri productUri, int currentQuantity) {

        int newQuantityValue = (currentQuantity >= 1) ? currentQuantity - 1 : 0;

        if (currentQuantity == 0) {
            Toast.makeText(context.getApplicationContext(), R.string.toast_out_of_stock, Toast.LENGTH_SHORT).show();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(StockContract.ProductEntry.PRODUCT_QUANTITY, newQuantityValue);
        int numRowsUpdated = context.getContentResolver().update(productUri, contentValues, null, null);
        if (numRowsUpdated > 0) {

            Log.i(TAG, context.getString(R.string.confirm_message));
        } else {
            Toast.makeText(context.getApplicationContext(), R.string.no_Stock, Toast.LENGTH_SHORT).show();

            Log.e(TAG, context.getString(R.string.error_stock_update));
        }

    }
}