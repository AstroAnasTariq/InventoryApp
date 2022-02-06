package com.example.android.bookstoreapp.adapter;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreapp.R;
import com.example.android.bookstoreapp.data.InventoryContract.InventoryEntry;

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.textview_catalog_name);
        TextView priceTextView = view.findViewById(R.id.textview_catalog_price);
        TextView quantityTextView = view.findViewById(R.id.textview_catalog_quantity);
        ImageButton addToCartImageButton = view.findViewById(R.id.imagebutton_catalog_addtocart);

        addToCartImageButton.setFocusable(false);
        addToCartImageButton.setFocusableInTouchMode(false);

        final int id = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));
        String name = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME));
        int price = cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE));
        final int quantity = cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY));

        nameTextView.setText(name);
        priceTextView.setText(String.valueOf(price));
        quantityTextView.setText(String.valueOf(quantity));

        addToCartImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri currentProductUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                addToCart(context, currentProductUri, quantity);
            }
        });
    }

    private void addToCart(Context context, Uri uri, int quantity) {
        if (quantity > 0) {
            int newAvailableQuantityValue = quantity - 1;

            ContentValues values = new ContentValues();
            values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, newAvailableQuantityValue);

            int rowsAffected = context.getContentResolver().update(uri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(context, "Error with adding product to " +
                        "the cart, please try again", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "Product added to the cart", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
