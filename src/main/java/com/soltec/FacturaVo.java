package com.soltec;

public class FacturaVo {

    int fact_id;
    Double fact_total;

    public FacturaVo(int fact_id, Double fact_total) {
        this.fact_id = fact_id;
        this.fact_total = fact_total;
    }

    public int getFact_id() {
        return fact_id;
    }

    public void setFact_id(int fact_id) {
        this.fact_id = fact_id;
    }

    public Double getFact_total() {
        return fact_total;
    }

    public void setFact_total(Double fact_total) {
        this.fact_total = fact_total;
    }

}
