package me.rampo.trackingmypantry;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Product {
    @PrimaryKey
    @NonNull
    String id;
    @ColumnInfo(name = "name")
    String name;
    @ColumnInfo(name = "description")
    String description;
    @ColumnInfo(name = "barcode")
    String barcode;
    @ColumnInfo(name = "quantity")
    private int quantity;
    @ColumnInfo(name = "category")
    private String categoria;
    @TypeConverters({Converter.class})
    private Date date;

    @ColumnInfo(name = "dateoutput")
    private String dateoutput;
    @ColumnInfo(name = "place")
    private String place;

    @Override
    public String toString() {
        return "Nome: "+ name + '\n' +
                "Id: " + id + '\n' +
                "Descrizione: " + description + '\n' +
                "Barcode: " + barcode + '\n' +
                "Quantitá: " + quantity + '\n' +
                "Categoria: "+ categoria + '\n' +
                "Data: "+ dateoutput + '\n' +
                "Luogo: " + place + '\n';
    }

    public Product(String id, String name, String description, String barcode, int quantity, String categoria, Date date, String place,String dateoutput) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.barcode = barcode;
        this.quantity = quantity;
        this.categoria = categoria;
        this.date = date;
        this.place = place;
        this.dateoutput = dateoutput;
    }

    public String getDateoutput() {
        return dateoutput;
    }

    public void setDateoutput(String dateoutput) {
        this.dateoutput = dateoutput;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public int getQuantity() {
        return quantity;
    }
    public String getCategoria() {
        return categoria;
    }
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public String getPlace() {
        return place;
    }
    public void setPlace(String place) {
        this.place = place;
    }

}
