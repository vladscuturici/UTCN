package Model;

public class Order {
    private int id;
    private int client_id;
    private int product_id;
    private int quantity;

    public Order(int id, int client_id, int product_id, int quantity) {
        this.id = id;
        this.client_id = client_id;
        this.product_id = product_id;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return client_id;
    }

    public void setClientId(int client_id) {
        this.client_id = client_id;
    }

    public int getProductId() {
        return product_id;
    }

    public void setProductId(int product_id) {
        this.product_id = product_id;
    }
    public String toString() {
        return this.id + " " + this.client_id + " "  + this.product_id;
    }
}