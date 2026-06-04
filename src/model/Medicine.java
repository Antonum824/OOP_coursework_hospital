package model;

public class Medicine {
    private int id;
    private String name;
    private double price;
    private int quantity;

    public Medicine(int id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    @Override
    public String toString() {
        return name + " (" + price + " руб.)";
    }
}
