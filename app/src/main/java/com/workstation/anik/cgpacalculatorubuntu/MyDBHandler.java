package com.workstation.anik.cgpacalculatorubuntu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by anik on 1/5/18.
 */

public class MyDBHandler extends SQLiteOpenHelper {
    Context context;

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "products.db";

    public static final String TABLE_PRODUCTS = "product";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CREDIT = "columncredit";
    public static final String COLUMN_GPA = "columngpa";
    public static final String COLUMN_TOTAL_GPA = "columntotalgpa";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_PRODUCTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_CREDIT + " TEXT," + COLUMN_GPA + " TEXT," +
                COLUMN_TOTAL_GPA + " TEXT" +
                ");";

        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    public void addProduct(ArrayList<resultInfo> product) {
        ContentValues values = new ContentValues();
        int sz = product.size();
        for(int i = 0; i < sz; i++) {
            values.put(COLUMN_CREDIT, product.get(i).getCredit().toString());
            values.put(COLUMN_GPA, product.get(i).getGpa().toString());
            values.put(COLUMN_TOTAL_GPA, product.get(i).getTotalGpa().toString());
            SQLiteDatabase db = getWritableDatabase();
            db.insert(TABLE_PRODUCTS, null, values);
            db.close();
        }
        System.out.println(sz);
    }

    public void deleteDataBase() {
        boolean i = context.deleteDatabase("products.db");
        //System.out.println(i);
    }
    public void deleteProduct(String productID) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_ID + " =\"" + productID + "\";");
    }

    // print out the database as a string
    public ArrayList<resultInfo> databaseToList() {
        ArrayList<resultInfo> dbList = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS;

        // cursor point to a location in your result
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        String r = "";

        int cnt = 0;
        while(!c.isAfterLast()) {
            resultInfo myRes = new resultInfo();

            if(c.getString(c.getColumnIndex(COLUMN_GPA)) != null) {
                myRes.setId(Integer.parseInt(c.getString(c.getColumnIndex(COLUMN_ID))));
                myRes.setCredit(Double.parseDouble(c.getString(c.getColumnIndex(COLUMN_CREDIT))));
                myRes.setGpa(Double.parseDouble(c.getString(c.getColumnIndex(COLUMN_GPA))));
                myRes.setTotalGpa(Double.parseDouble(c.getString(c.getColumnIndex(COLUMN_TOTAL_GPA))));
                dbList.add(myRes);
                r += c.getString(c.getColumnIndex(COLUMN_ID));
                r += "\n";
                r += c.getString(c.getColumnIndex(COLUMN_CREDIT));
                r += "\n";
                r += c.getString(c.getColumnIndex(COLUMN_GPA));
                r += "\n";
                r += c.getString(c.getColumnIndex(COLUMN_TOTAL_GPA));

                System.out.println(r);
            }
            c.moveToNext();
        }
        db.close();
        return dbList;
    }
}
