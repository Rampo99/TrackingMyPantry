package me.rampo.trackingmypantry;

public class PantryProduct extends Product{
    int quantity;

    @Override
    public String toString() {
        return "Nome: "+ name + '\n' +
                "Id: " + id + '\n' +
                "Descrizione: " + description + '\n' +
                "Barcode: " + barcode + '\n' +
                "Quantitá: " + quantity + '\n';
    }

    public PantryProduct(String id, String name, String description, String barcode, int quantity) {
        super(id, name, description, barcode);
        this.quantity = quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }
}
