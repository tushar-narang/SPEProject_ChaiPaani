package com.epikat.chaipaani.pojo;

public class CartItem {

        int id;
        int quantity;

        public CartItem(){   }

        public CartItem(int id, int quantity){
            this.id = id;
            this.quantity = quantity;
        }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
