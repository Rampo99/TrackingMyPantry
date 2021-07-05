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
    public static final String category = "category";
    public static final String date = "date";
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
                quantity + " INT," +
                category + " TEXT," +
                date + " TEXT)";
        String createfilters = "CREATE TABLE filters (filter TEXT PRIMARY KEY)";
        db.execSQL(createfilters);
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
            values.put(category,"---");
            values.put(date,"---");
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
            if(q >= 1) query = "UPDATE " + tablename + " SET "+ quantity + " = "+q+" WHERE id = '" + p.getId() + "'";
        }
        db.execSQL(query);
        c.close();

    }

    public List<PantryProduct> getProducts(){
        List<PantryProduct> p = new ArrayList<>();
        String query = "SELECT * FROM " + tablename;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query,null);
        if(c.moveToFirst()){
            do {
                PantryProduct product = new PantryProduct(c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getInt(4),c.getString(5),c.getString(6));
                p.add(product);
            } while (c.moveToNext());
        }
        c.close();
        return p;
    }
    public void addFilter(List<String> filter){
        SQLiteDatabase db = this.getWritableDatabase();
        String deletequery = "DELETE FROM filters";
        db.execSQL(deletequery);

        for(String x : filter){
            String query = "INSERT INTO filters VALUES('" + x + "')";

            db.execSQL(query);
        }

    }
    public List<String> getFilters(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM filters";
        Cursor c = db.rawQuery(query,null);
        List<String> filters = new ArrayList<>();
        if(c.moveToFirst()){
            do {
                String filter = c.getString(0);
                filters.add(filter);
            } while (c.moveToNext());
        }
        c.close();
        return filters;
    }
    public void addCategory(String id, String category){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + tablename + " SET category = '"+category+"' WHERE id = '" + id + "'";
        db.execSQL(query);
    }
    public void addDate(String id, String date){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + tablename + " SET date = '"+date+"' WHERE id = '" + id + "'";
        db.execSQL(query);
    }
}
