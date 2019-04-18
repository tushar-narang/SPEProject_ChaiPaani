package com.epikat.chaipaani.pojo;

public class OrderItem {
    int id;
    String date;
    String status;
    int amount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", status='" + status + '\'' +
                ", amount=" + amount +
                '}';
    }
}
