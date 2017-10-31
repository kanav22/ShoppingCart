package wadhawan.shoppingcart;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import wadhawan.shoppingcart.data.StockContract.ProductEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;
    boolean hasAllValues = false;
    private EditText nSupplierName;
    private EditText nNameEditText;
    private EditText nCategoryEditText;
    private Spinner nSpinner;
    private TextView nPictureText;
    private EditText nSupplierEmail;
    private EditText nQuantityEditText;
    private Uri nCurrentProductUri;
    private Button nAddProdButton;
    private Button nRejectProdButton;
    private Uri nPictureUri;
    private int nQuantity;
    private ImageView nPicture;
    private EditText nPrice;
    private int mWarranty = ProductEntry.PRODUCT_WARRANTY_UNKNOWN;
    private boolean mProductHasChanged = false;
    private int mCurrentQuantity = 0;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Intent intent = getIntent();
        nCurrentProductUri = intent.getData();
        nNameEditText = (EditText) findViewById(R.id.image_name);
        nCategoryEditText = (EditText) findViewById(R.id.product_category);
        nQuantityEditText = (EditText) findViewById(R.id.product_quantity);
        nSpinner = (Spinner) findViewById(R.id.spinner);
        nPicture = (ImageView) findViewById(R.id.image_image);
        nPrice = (EditText) findViewById(R.id.item_price);
        nSupplierName = (EditText) findViewById(R.id.supplier_name);
        nSupplierEmail = (EditText) findViewById(R.id.supplier_email);
        nAddProdButton = (Button) findViewById(R.id.addButton);
        nRejectProdButton = (Button) findViewById(R.id.rejectButton);
        nPictureText = (TextView) findViewById(R.id.image_hint);

        if (nCurrentProductUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_item));
            nPictureText.setText(getText(R.string.add_photo_hint));
            nSupplierName.setEnabled(true);
            nSupplierEmail.setEnabled(true);
            nQuantityEditText.setEnabled(true);
            nPicture.setImageResource(R.drawable.empty_cart);
            nAddProdButton.setVisibility(View.GONE);
            nRejectProdButton.setVisibility(View.GONE);
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.activity_title_edit));
            nPictureText.setText(getText(R.string.edit_photo_hint));
            nSupplierName.setEnabled(false);
            nSupplierEmail.setEnabled(false);
            nQuantityEditText.setEnabled(false);
            nRejectProdButton.setVisibility(View.VISIBLE);
            nAddProdButton.setVisibility(View.VISIBLE);
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }
        nNameEditText.setOnTouchListener(mTouchListener);
        nCategoryEditText.setOnTouchListener(mTouchListener);
        nQuantityEditText.setOnTouchListener(mTouchListener);
        nSpinner.setOnTouchListener(mTouchListener);
        nPrice.setOnTouchListener(mTouchListener);
        nSupplierName.setOnTouchListener(mTouchListener);
        nSupplierEmail.setOnTouchListener(mTouchListener);
        nAddProdButton.setOnTouchListener(mTouchListener);
        nRejectProdButton.setOnTouchListener(mTouchListener);
        nAddProdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem(v);
            }
        });
        nRejectProdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectItem(v);
            }
        });
        nPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySelector();
                mProductHasChanged = true;
            }
        });
        setupSpinner();

    }

    private void openSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType(getString(R.string.intent_type));
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), 0);
    }

    public void trySelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }
        openSelector();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] results) {
        switch (requestCode) {
            case 1:
                if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                    openSelector();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                nPictureUri = data.getData();
                nPicture.setImageURI(nPictureUri);
                nPicture.invalidate();
            }
        }
    }

    public void nextOrder() {
        Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:" + nSupplierEmail.getText().toString().trim()));
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "New order: " +
                nNameEditText.getText().toString().trim() +
                " " + nCategoryEditText.getText().toString().trim());
        String message = "Kindly make a new order of: " +
                nNameEditText.getText().toString().trim() +
                " " +
                nCategoryEditText.getText().toString().trim() + "." +
                "\n" +
                "Please confirm that you can send to us ___ pcs." +
                "\n" +
                "\n" +
                "Regards," + "\n" +
                "_____________";
        intent.putExtra(android.content.Intent.EXTRA_TEXT, message);
        startActivity(intent);
    }

    private void setupSpinner() {

        ArrayAdapter warrantySpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_warranty_options, android.R.layout.simple_spinner_item);
        warrantySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        nSpinner.setAdapter(warrantySpinnerAdapter);
        nSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals("in warranty")) {
                        mWarranty = ProductEntry.PRODUCT_IN_WARRANTY;
                    } else if (selection.equals("out of warranty")) {
                        mWarranty = ProductEntry.PRODUCT_OUT_WARRANTY;
                    } else {
                        mWarranty = ProductEntry.PRODUCT_WARRANTY_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mWarranty = ProductEntry.PRODUCT_WARRANTY_UNKNOWN;
            }
        });
    }

    private boolean saveProduct() {

        int quantity;
        String name = nNameEditText.getText().toString().trim();
        String supplierName = nSupplierName.getText().toString().trim();
        String category = nCategoryEditText.getText().toString().trim();
        String quantityString = nQuantityEditText.getText().toString().trim();
        String price = nPrice.getText().toString().trim();
        String supplierEmail = nSupplierEmail.getText().toString().trim();
        if (nCurrentProductUri == null &&
                TextUtils.isEmpty(name) &&
                TextUtils.isEmpty(category) &&
                TextUtils.isEmpty(quantityString) &&
                mWarranty == ProductEntry.PRODUCT_WARRANTY_UNKNOWN &&
                TextUtils.isEmpty(price) &&
                TextUtils.isEmpty(supplierName) &&
                TextUtils.isEmpty(supplierEmail) &&
                nPictureUri == null) {
            hasAllValues = true;
            return hasAllValues;
        }

        ContentValues values = new ContentValues();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, getString(R.string.validation_product_name), Toast.LENGTH_SHORT).show();
            return hasAllValues;
        } else {
            values.put(ProductEntry.PRODUCT_NAME, name);
        }

        if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, getString(R.string.validation_product_quantity), Toast.LENGTH_SHORT).show();
            return hasAllValues;
        } else {
            quantity = Integer.parseInt(quantityString);
            values.put(ProductEntry.PRODUCT_QUANTITY, quantity);
        }

        if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, getString(R.string.validation_product_price), Toast.LENGTH_SHORT).show();
            return hasAllValues;
        } else {
            values.put(ProductEntry.PRODUCT_PRICE, price);
        }

        if (nPictureUri == null) {
            Toast.makeText(this, getString(R.string.validation_msg_product_image), Toast.LENGTH_SHORT).show();
            return hasAllValues;
        } else {
            values.put(ProductEntry.PRODUCT_IMAGE, nPictureUri.toString());
        }
        values.put(ProductEntry.PRODUCT_CATEGORY, category);
        values.put(ProductEntry.PRODUCT_WARRANTY, mWarranty);
        values.put(ProductEntry.PRODUCT_SUPPLIER_NAME, supplierName);
        values.put(ProductEntry.PRODUCT_SUPPLIER_EMAIL, supplierEmail);

        if (nCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(nCurrentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, getString(R.string.update_product_successful),
                        Toast.LENGTH_SHORT).show();
            }

        }
        hasAllValues = true;
        return hasAllValues;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (nCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                if (hasAllValues == true) {

                    finish();
                }
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case R.id.action_order_more:
                nextOrder();
                return true;

            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {
                ProductEntry._ID,
                ProductEntry.PRODUCT_NAME,
                ProductEntry.PRODUCT_CATEGORY,
                ProductEntry.PRODUCT_WARRANTY,
                ProductEntry.PRODUCT_IMAGE,
                ProductEntry.PRODUCT_PRICE,
                ProductEntry.PRODUCT_SUPPLIER_NAME,
                ProductEntry.PRODUCT_SUPPLIER_EMAIL,
                ProductEntry.PRODUCT_QUANTITY
        };

        return new CursorLoader(this,
                nCurrentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_NAME);
            int modelColumnIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_CATEGORY);
            int warrantyColumnIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_WARRANTY);
            int pictureColumnIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_IMAGE);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_PRICE);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_SUPPLIER_NAME);
            int supplierEmailColumnIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_SUPPLIER_EMAIL);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.PRODUCT_QUANTITY);

            String imageUriString = cursor.getString(pictureColumnIndex);
            String priceString = cursor.getString(priceColumnIndex);
            String nameString = cursor.getString(nameColumnIndex);
            String modelString = cursor.getString(modelColumnIndex);
            int warranty = cursor.getInt(warrantyColumnIndex);
            String supplierNameString = cursor.getString(supplierNameColumnIndex);
            String supplierEmailString = cursor.getString(supplierEmailColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            nQuantity = quantity;
            nPictureUri = Uri.parse(imageUriString);
            nNameEditText.setText(nameString);
            nCategoryEditText.setText(modelString);
            nPicture.setImageURI(nPictureUri);
            nPrice.setText(priceString);
            nSupplierName.setText(supplierNameString);
            nSupplierEmail.setText(supplierEmailString);
            nQuantityEditText.setText(Integer.toString(quantity));

            switch (warranty) {
                case ProductEntry.PRODUCT_IN_WARRANTY:
                    nSpinner.setSelection(1);
                    break;
                case ProductEntry.PRODUCT_OUT_WARRANTY:
                    nSpinner.setSelection(2);
                    break;
                default:
                    nSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nNameEditText.setText("");
        nCategoryEditText.setText("");
        nPicture.setImageResource(R.drawable.empty_cart);
        nPrice.setText("");
        nSupplierName.setText("");
        nSupplierEmail.setText("");
        nQuantityEditText.setText("");
        nSpinner.setSelection(0);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (nCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(nCurrentProductUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    public void rejectItem(View view) {
        if (nQuantity == 0) {
            Toast.makeText(this, "Can't decrease quantity", Toast.LENGTH_SHORT).show();
        } else {
            nQuantity--;
            displayQuantity();
        }
    }

    public void addItem(View view) {
        nQuantity++;
        displayQuantity();
    }

    public void displayQuantity() {
        nQuantityEditText.setText(String.valueOf(nQuantity));
    }
}