package me.rampo.trackingmypantry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PantryProduct extends Product{
    private int quantity;
    private String categoria;
    private Date date = null;
    @Override
    public String toString() {
        return "Nome: "+ name + '\n' +
                "Id: " + id + '\n' +
                "Descrizione: " + description + '\n' +
                "Barcode: " + barcode + '\n' +
                "Quantitá: " + quantity + '\n' +
                "Categoria: "+ categoria + '\n' +
                "Data: "+ date + '\n';
    }

    public PantryProduct(String id, String name, String description, String barcode, int quantity, String categoria, String date) {
        super(id, name, description, barcode);
        this.quantity = quantity;
        this.categoria = categoria;
        if(!date.equals("---")){
            try {
                this.date = new SimpleDateFormat("dd/MM/yyyy").parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

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
}
