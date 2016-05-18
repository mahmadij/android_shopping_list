package com.mehrnaz.shoplist;

/**
 * Created by Mehr on 11/15/2015.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
    //class-scoped variables
    static final String KEY_ROWID = "id";
    static final String KEY_ITEM = "itemname";
    static final String KEY_STORE = "storename";
    static final String KEY_COST = "cost";
    static final String KEY_SAVED = "saving";
    static final String TAG = "DBAdapter";

    static final String DATABASE_NAME = "ShopDB";
    static final String SHOP_TABLE = "shoplist";
    static final int DATABASE_VERSION = 1;

    static final String DATABASE_CREATE =
            "create table shoplist (id integer primary key autoincrement, "
                    + "itemname text, storename text, cost number(10,2), saving number(10,2));";

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try {
                db.execSQL(DATABASE_CREATE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS shoplist");
            onCreate(db);
        }
    }

    //---opens the database---
    public DBAdapter open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close()
    {
        DBHelper.close();
    }

    //---insert a saving item into the database---
    public long insertItem(String iname, String sname, double c,double s)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ITEM, iname);
        initialValues.put(KEY_STORE, sname);
        initialValues.put(KEY_COST, c);
        initialValues.put(KEY_SAVED, s);
        return db.insert(SHOP_TABLE, null, initialValues );
    }

    //---retrieves all the items for specific month---
    public Cursor getList()
    {
        return db.query(SHOP_TABLE, new String[]{KEY_ROWID, KEY_ITEM, KEY_STORE,
                KEY_COST, KEY_SAVED}, null, null, null, null, null, null);
    }

    //---retrieves a particular item---
    public long updateItem(int idnum,String iname, String sname, double c,double s) throws SQLException
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ITEM, iname);
        initialValues.put(KEY_STORE, sname);
        initialValues.put(KEY_COST, c);
        initialValues.put(KEY_SAVED, s);
        return db.insert(SHOP_TABLE, null, initialValues);
    }

    //adds up the savings for specific month
    public double getSavingSum ()
    {
        //Cursor c = db.rawQuery("SELECT Sum(amount) FROM savings where month = ?" , new String[]{ month});
        Cursor c = db.rawQuery("SELECT Sum(saving) FROM shoplist ",null);
        return c.getDouble(0);
    }

    //---deletes a particular item---
    public boolean deleteItem(long rowId)
    {
        return db.delete(SHOP_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    //---deletes all items from the table---
    public void deleteAll()
    {
        db.execSQL("delete from "+ SHOP_TABLE);
    }
}
