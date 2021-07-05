package me.rampo.trackingmypantry;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PantryProduct extends Product{
    private int quantity;
    private String categoria;
    private Date date = null;
    private String output = null;
    private String place;
    @Override
    public String toString() {
        return "Nome: "+ name + '\n' +
                "Id: " + id + '\n' +
                "Descrizione: " + description + '\n' +
                "Barcode: " + barcode + '\n' +
                "Quantitá: " + quantity + '\n' +
                "Categoria: "+ categoria + '\n' +
                "Data: "+ output + '\n' +
                "Luogo: " + place + '\n';
    }

    public PantryProduct(String id, String name, String description, String barcode, int quantity, String categoria, String date, String place) {
        super(id, name, description, barcode);
        this.quantity = quantity;
        this.categoria = categoria;
        if(!date.equals("---")){
            try {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                this.date = dateFormat.parse(date);
                output = dateFormat.format(this.date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        this.place = place;
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
