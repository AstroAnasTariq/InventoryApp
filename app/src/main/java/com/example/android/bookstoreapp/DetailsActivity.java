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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.InventoryContract.InventoryEntry;


public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int VIEW_PRODUCT_LOADER = 2;
    private Uri currentProductUri;

    private TextView nameTextView;
    private TextView quantityTextView;
    private TextView totalTextView;
    private TextView supplierNameTextView;
    private TextView supplierEmailTextView;
    private TextView supplierPhoneNumberTextView;

    private String productName;
    private int quantity;
    private int pricePerProduct;
    private String supplierEmail;
    private String supplierPhoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        currentProductUri = getIntent().getData();
        getLoaderManager().initLoader(VIEW_PRODUCT_LOADER, null, this);

        nameTextView = findViewById(R.id.textview_details_name);
        quantityTextView = findViewById(R.id.textview_details_quantity);
        Button decreaseQuantityButton = findViewById(R.id.button_details_decreasequantity);
        Button increaseQuantityButton = findViewById(R.id.button_details_increasequantity);
        totalTextView = findViewById(R.id.textview_details_total);
        supplierNameTextView = findViewById(R.id.textview_details_suppliername);
        ImageButton emailSupplierImageButton = findViewById(R.id.imagebutton_details_emailsupplier);
        supplierEmailTextView = findViewById(R.id.textview_details_supplieremail);
        ImageButton callSupplierImageButton = findViewById(R.id.imagebutton_details_callsupplier);
        supplierPhoneNumberTextView = findViewById(R.id.textview_details_supplierphonenumber);

        decreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    int newQuantity = quantity - 1;

                    ContentValues values = new ContentValues();
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);

                    int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);

                    if (rowsAffected == 0) {
                        Toast.makeText(DetailsActivity.this, "Error with updating quantity", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(DetailsActivity.this, "Quantity updated", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        increaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    int newQuantity = quantity + 1;

                    ContentValues values = new ContentValues();
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, newQuantity);

                    int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);

                    if (rowsAffected == 0) {
                        Toast.makeText(DetailsActivity.this, "Error with updating quantity", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(DetailsActivity.this, "Quantity updated", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        emailSupplierImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmailToSupplier();
            }
        });

        callSupplierImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + supplierPhoneNumber));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    private void sendEmailToSupplier() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + supplierEmail));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Books's customer: " + productName);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL,
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
            productName = data.getString(data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME));
            pricePerProduct = data.getInt(data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE));
            quantity = data.getInt(data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY));
            String supplierName = data.getString(data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NAME));
            supplierEmail = data.getString(data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_EMAIL));
            supplierPhoneNumber = data.getString(data.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER));

            nameTextView.setText(productName);
            quantityTextView.setText(String.valueOf(quantity));
            totalTextView.setText(String.valueOf(calculateTotalPrice()));
            supplierNameTextView.setText(supplierName);
            supplierEmailTextView.setText(supplierEmail);
            supplierPhoneNumberTextView.setText(supplierPhoneNumber);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameTextView.setText("");
        quantityTextView.setText("");
        totalTextView.setText(String.valueOf(0));
        supplierNameTextView.setText("");
        supplierEmailTextView.setText("");
        supplierPhoneNumberTextView.setText("");
    }

    private int calculateTotalPrice() {
        return quantity * pricePerProduct;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_details_edit:
                Log.i("CatalogActivity", "pressedItemUri: " + currentProductUri);
                Intent intent = new Intent(DetailsActivity.this, EditActivity.class);
                intent.setData(currentProductUri);
                startActivity(intent);
                return true;
            case R.id.action_details_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            Toast.makeText(this, "Error with deleting product", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
