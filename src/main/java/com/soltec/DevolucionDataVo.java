package com.soltec;

public class DevolucionDataVo {

    private int id;
    private double total;

    public DevolucionDataVo(int id, double total) {
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
