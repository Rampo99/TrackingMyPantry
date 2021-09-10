package me.rampo.trackingmypantry;

public class WebProduct {
    String id;
    String name;
    String description;
    String barcode;

    @Override
    public String toString() {
        return "Nome: "+ name + '\n' +
                "Id: " + id + '\n' +
                "Descrizione: " + description + '\n' +
                "Barcode: " + barcode + '\n';
    }
    public WebProduct(String id, String name, String description, String barcode) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.barcode = barcode;
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

    Product toProduct(){
        return new Product(this.id,this.name,this.description,this.barcode,1,null,null,null,null);
    }
}
