package com.soltec;

public class VentaDataVo {

    private int id;
    private double total;

    public VentaDataVo(int id, double total) {
        this.id = id;
        this.total = total;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

}
