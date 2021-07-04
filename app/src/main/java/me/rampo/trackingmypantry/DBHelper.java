package me.rampo.trackingmypantry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public static final String tablename = "Products";
    public static final String id = "id";
    public static final String name = "name";
    public static final String description = "description";
    public static final String barcode = "barcode";
    public static final String quantity = "quantity";
    public DBHelper(@Nullable Context context) {
        super(context, "products.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createtable = "CREATE TABLE "+ tablename + " (" +
                id + " TEXT PRIMARY KEY," +
                name + " TEXT,"+
                description + " TEXT,"+
                barcode + " TEXT," +
                quantity + " INT)";
        db.execSQL(createtable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addProduct(Product p){

        SQLiteDatabase earlycheck = this.getReadableDatabase();
        String query = "SELECT * FROM " + tablename + " WHERE id = '" + p.getId()+"'";
        Cursor c = earlycheck.rawQuery(query,null);
        int q = 1;
        SQLiteDatabase db = this.getWritableDatabase();

        if(c.moveToFirst()){
            q += c.getInt(4) ;
            String updatequery = "UPDATE " + tablename + " SET "+ quantity + " = "+q+" WHERE id = '" + p.getId() + "'";
            db.execSQL(updatequery);
        } else {
            ContentValues values = new ContentValues();
            values.put(id,p.getId());
            values.put(name,p.getName());
            values.put(description,p.getName());
            values.put(barcode,p.getBarcode());
            values.put(quantity,q);
            db.insert(tablename,null,values);
        }
        c.close();

    }

    public void removeProduct(PantryProduct p) {

        SQLiteDatabase earlycheck = this.getReadableDatabase();
        String earlyquery = "SELECT * FROM " + tablename + " WHERE id = '" + p.getId()+"'";
        Cursor c = earlycheck.rawQuery(earlyquery,null);
        String query = "DELETE FROM " + tablename + " WHERE " + id + " = '" + p.getId() + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        if(c.moveToFirst()){
            int q = c.getInt(4) - 1;
            query = "UPDATE " + tablename + " SET "+ quantity + " = "+q+" WHERE id = '" + p.getId() + "'";
        }
        db.execSQL(query);



    }

    public List<PantryProduct> getProducts(){
        List<PantryProduct> p = new ArrayList<>();
        String query = "SELECT * FROM " + tablename;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query,null);
        if(c.moveToFirst()){
            do {
                PantryProduct product = new PantryProduct(c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getInt(4));
                p.add(product);
            } while (c.moveToNext());
        }
        c.close();
        return p;
    }
}
