package com.soltec;

public class VentaDataVo {

    private int id;
    private double total;
    private double totaliva;
    private double iva5;
    private double iva19;

    public VentaDataVo(int id, double total, double totaliva) {
        this.id = id;
        this.total = total;
        this.totaliva = totaliva;
    }

    public VentaDataVo(int id, double total, double totaliva, double iva5) {
        this.id = id;
        this.total = total;
        this.totaliva = totaliva;
        this.iva5 = iva5;
    }

    public VentaDataVo(int id, double total, double totaliva, double iva5, double iva19) {
        this.id = id;
        this.total = total;
        this.totaliva = totaliva;
        this.iva5 = iva5;
        this.iva19 = iva19;
    }

    public double getIva19() {
        return iva19;
    }

    public void setIva19(double iva19) {
        this.iva19 = iva19;
    }

    public double getIva5() {
        return iva5;
    }

    public void setIva5(double iva5) {
        this.iva5 = iva5;
    }

    public double getTotaliva() {
        return totaliva;
    }

    public void setTotaliva(double totaliva) {
        this.totaliva = totaliva;
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
