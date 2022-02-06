package com.example.android.bookstoreapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.InventoryContract;
import com.example.android.bookstoreapp.data.InventoryContract.InventoryEntry;


public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText nameEditText;
    private EditText priceEditText;
    private EditText availableQuantityEditText;
    private EditText supplierNameEditText;
    private EditText supplierPhoneNumberEditText;


    private static final int UPDATE_PRODUCT_LOADER = 1;
    private Uri currentProductUri;


    private boolean productHasChanged = false;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            productHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        currentProductUri = getIntent().getData();
        getLoaderManager().initLoader(UPDATE_PRODUCT_LOADER, null, this);

        nameEditText = findViewById(R.id.edittext_editors_name);
        priceEditText = findViewById(R.id.edittext_editors_price);
        availableQuantityEditText = findViewById(R.id.edittext_editors_quantity);
        supplierNameEditText = findViewById(R.id.edittext_editors_suppliername);
        supplierPhoneNumberEditText = findViewById(R.id.edittext_editors_supplierphonenumber);

        nameEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        availableQuantityEditText.setOnTouchListener(touchListener);
        supplierNameEditText.setOnTouchListener(touchListener);
        supplierPhoneNumberEditText.setOnTouchListener(touchListener);
    }

    private void updateProduct() {

        String nameString = nameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String availableQuantityString = availableQuantityEditText.getText().toString().trim();
        String supplierNameString = supplierNameEditText.getText().toString().trim();
        String supplierPhoneNumberString = supplierPhoneNumberEditText.getText().toString().trim();

        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString)
                || TextUtils.isEmpty(availableQuantityString) || TextUtils.isEmpty(supplierNameString)) {
            Toast.makeText(this, "Please fill all the starred fields to save the product",
                    Toast.LENGTH_LONG).show();

        } else {

            ContentValues values = new ContentValues();
            values.put(InventoryContract.InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
            values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, priceString);
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, availableQuantityString);
            values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierNameString);
            values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberString);

            int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, "Error with updating product", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Product updated", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_save:
                updateProduct();
                return true;
            case R.id.action_edit_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:

                if (!productHasChanged) {
                    Intent intent = NavUtils.getParentActivityIntent(this);
                    assert intent != null;
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    NavUtils.navigateUpTo(this, intent);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = NavUtils.getParentActivityIntent(EditActivity.this);
                                assert intent != null;
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                NavUtils.navigateUpTo(EditActivity.this, intent);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!productHasChanged) {
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

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER};

        return new CursorLoader(
                this,
                currentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            String name = data.getString(data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME));
            String price = String.valueOf(data.getInt(data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE)));
            String availableQuantity = String.valueOf(data.getInt(data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY)));
            String supplierName = data.getString(data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME));
            String supplierPhoneNumber = data.getString(data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER));

            nameEditText.setText(name);
            priceEditText.setText(price);
            availableQuantityEditText.setText(availableQuantity);
            supplierNameEditText.setText(supplierName);
            supplierPhoneNumberEditText.setText(supplierPhoneNumber);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.getText().clear();
        priceEditText.getText().clear();
        availableQuantityEditText.getText().clear();
        supplierNameEditText.getText().clear();
        supplierPhoneNumberEditText.getText().clear();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this product?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deletePet();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePet() {
        int rowsDeleted = getContentResolver().delete(currentProductUri, null, null);

        if (rowsDeleted == 0) {
            Toast.makeText(this, "Error with deleting product",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Product deleted",
                    Toast.LENGTH_SHORT).show();
        }
        finish();
        Intent intent = new Intent(EditActivity.this, InventoryActivity.class);
        startActivity(intent);
    }
}
