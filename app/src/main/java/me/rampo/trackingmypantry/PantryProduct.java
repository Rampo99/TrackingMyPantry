package me.rampo.trackingmypantry;

public class PantryProduct extends Product{
    private int quantity;
    private String categoria;

    @Override
    public String toString() {
        return "Nome: "+ name + '\n' +
                "Id: " + id + '\n' +
                "Descrizione: " + description + '\n' +
                "Barcode: " + barcode + '\n' +
                "Quantitá: " + quantity + '\n' +
                "Categoria: "+ categoria + '\n';
    }

    public PantryProduct(String id, String name, String description, String barcode, int quantity,String categoria) {
        super(id, name, description, barcode);
        this.quantity = quantity;
        this.categoria = categoria;
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
}
