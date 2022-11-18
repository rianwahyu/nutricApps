package com.rigadev.nutricapps.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SQLiteHelpers extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "nutric_apps";

    private static final String TABLE_DATA_CART ="cart";
    private static final String TABLE_DATA_NOTIFICATION ="notification";

    public static final String CART_ID = "id";
    public static final String CART_IDFOOD = "idFood";
    public static final String CART_NAME = "name";
    public static final String CART_PHOTO = "photo";
    public static final String CART_QTY = "qty";
    public static final String CART_PRICE = "price";

    public static final String NOTIFICATION_ID = "id";
    public static final String NOTIFICATION_NAME = "name";
    public static final String NOTIFICATION_CONTENT = "content";
    public static final String NOTIFICATION_DATE = "dateNotification";
    public static final String NOTIFICATION_READ = "readNotification";

    private static final String CREATE_TABLE_CART = " CREATE TABLE "
            + TABLE_DATA_CART + "(" + CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CART_IDFOOD + " TEXT, "
            + CART_NAME + " TEXT, "
            + CART_PHOTO + " TEXT, "
            + CART_QTY + " INTEGER,"
            + CART_PRICE + " INTEGER " + ")";

    private static final String CREATE_TABLE_NOTIFICATION = " CREATE TABLE "
            + TABLE_DATA_NOTIFICATION + "(" + NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + NOTIFICATION_NAME + " TEXT, "
            + NOTIFICATION_CONTENT + " TEXT, "
            + NOTIFICATION_DATE + " TEXT, "
            + NOTIFICATION_READ + " NUMERIC " + ")";

    public SQLiteHelpers(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CART);
        db.execSQL(CREATE_TABLE_NOTIFICATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_DATA_CART);
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_DATA_NOTIFICATION);
        onCreate(db);
    }

    public void insertCart(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_DATA_CART, null, values);
    }

    public void updateCart(ContentValues values, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_DATA_CART, values, CART_ID + "=" + id, null);
    }

    public void deleteCart(String cart_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = " DELETE FROM " + TABLE_DATA_CART + " " +
                " WHERE " + CART_ID + "='" + cart_id + "' ";
        db.execSQL(query);
        db.close();
    }

    public void deleteCartAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = " DELETE FROM " + TABLE_DATA_CART;
        db.execSQL(query);
        db.close();
    }

    public Cursor getAllCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_DATA_CART + " " +
                "WHERE 1 ", null);
        return cur;
    }

    public String countCart() {
        String nb = "";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT COUNT(*) as total FROM " + TABLE_DATA_CART + " " +
                "WHERE 1 " , null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        if (cursor.getCount() < 1) {
            return "0";
        }
        nb = cursor.getString(0);
        return nb;
    }

    public void insertNotification(ContentValues values) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_DATA_NOTIFICATION, null, values);
    }

    public Cursor getNotification() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_DATA_NOTIFICATION + " " +
                "WHERE 1 ", null);
        return cur;
    }
}
