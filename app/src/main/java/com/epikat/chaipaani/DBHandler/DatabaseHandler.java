package com.epikat.chaipaani.DBHandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.epikat.chaipaani.pojo.CartItem;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ChaiPani";
    private static final String TABLE_CART = "cart";
    private static final String KEY_ID = "id";
    private static final String KEY_QUAN = "quantity";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CART + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_QUAN + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);

        // Create tables again
        onCreate(db);
    }

    // code to add the new contact
    public void addItem(CartItem contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, contact.getId());
        values.put(KEY_QUAN, contact.getQuantity());

        // Inserting Row
        db.insert(TABLE_CART, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single contact
    public CartItem getItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CART, new String[] { KEY_ID, KEY_QUAN }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        CartItem item = new CartItem(
                Integer.parseInt(cursor.getString(0)),
                Integer.parseInt(cursor.getString(1)));
        // return contact
        return item;
    }

    // code to get all contacts in a list view
    public List<CartItem> getAllItems() {
        List<CartItem> cartItemsList = new ArrayList<CartItem>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CART;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CartItem contact = new CartItem();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setQuantity(Integer.parseInt(cursor.getString(1)));
                // Adding contact to list
                cartItemsList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return cartItemsList;
    }

    // code to update the single contact
    public int updateItem(CartItem contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, contact.getId());
        values.put(KEY_QUAN, contact.getQuantity());

        // updating row
        return db.update(TABLE_CART, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });
    }


    // Deleting single contact
    public void deleteItem(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, KEY_ID + " = ?",
                new String[] { String.valueOf(itemId) });
        db.close();
    }

    // Getting contacts Count
    public int getItemsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CART;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

}