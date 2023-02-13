package com.soltec;

public class VentaVo {
    private Double total;
    private float porcentaje;
    private int mes;
    private Double totalNew;
    private Double totalAnular;

    public VentaVo(Double total, float porcentaje, int mes, Double totalNew, Double totalAnular) {
        this.total = total;
        this.porcentaje = porcentaje;
        this.mes = mes;
        this.totalNew = totalNew;
        this.totalAnular = totalAnular;
    }

    public Double getTotalAnular() {
        return totalAnular;
    }

    public void setTotalAnular(Double totalAnular) {
        this.totalAnular = totalAnular;
    }

    public Double getTotalNew() {
        return totalNew;
    }

    public void setTotalNew(Double totalNew) {
        this.totalNew = totalNew;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public float getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(float porcentaje) {
        this.porcentaje = porcentaje;
    }

    public int getMes() {
        return mes;
    }

    public void setMes(int mes) {
        this.mes = mes;
    }
}
