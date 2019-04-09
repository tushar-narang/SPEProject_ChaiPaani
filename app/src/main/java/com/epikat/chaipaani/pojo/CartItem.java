package com.epikat.chaipaani.pojo;

public class CartItem {

        int id;
        String name;
        int quantity;

        public CartItem(){   }

        public CartItem(String name, int quantity){
            this.name = name;
            this.quantity = quantity;
        }

    public CartItem(int id, String name, int quantity){
            this.id = id;
        this.name = name;
        this.quantity = quantity;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
