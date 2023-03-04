package com.soltec;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Facturas5Dao {

    double netoVent5;
    Connection connection;
    String fechaInicialString;
    String fechaFinalString;

    public Facturas5Dao(Connection connection, double netoVent5, String fechaInicialString,
            String fechaFinalString) {
        this.connection = connection;
        this.netoVent5 = netoVent5;
        this.fechaInicialString = fechaInicialString;
        this.fechaFinalString = fechaFinalString;
    }

    private Double ObtenerVentas5(java.util.Date fechaInicial, java.util.Date fechaFinal) {
        try {

            PreparedStatement statement = null;
            ResultSet resultado = null;

            Double total = 0.0;

            String sql = "SELECT sum(fd.fade_total-fd.fade_ivamonto) FROM FACTURAS_DETALLE fd, FACTURAS f WHERE fd.fact_id = f.fact_id AND f.fact_fecha  <= ? AND f.fact_fecha >= ? AND f.fact_anulado = 'N' AND fd.fade_ivaporc = 5;";
            statement = connection.prepareStatement(sql);
            statement.setDate(1, new Date(fechaFinal.getTime()));
            statement.setDate(2, new Date(fechaInicial.getTime()));
            resultado = statement.executeQuery();

            if (resultado.next()) {
                total = resultado.getDouble(1);
            }

            return total;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<VentaDataVo> ObtenerVentasData5(java.util.Date fechaInicial,
            java.util.Date fechaFinal) {
        try {

            PreparedStatement statement = null;
            ResultSet resultado = null;

            VentaDataVo ventaData = null;

            String sql = "SELECT f.fact_id, f.fact_total, (SELECT sum(fd.fade_total-fd.fade_ivamonto) FROM FACTURAS_DETALLE fd WHERE fd.fact_id = f.fact_id AND fd.fade_ivaporc = 5 ) FROM FACTURAS f WHERE f.fact_fecha  <= ? AND f.fact_fecha >= ? AND f.fact_anulado = 'N' AND (SELECT count(*) FROM FACTURAS_DETALLE fd WHERE fd.fact_id = f.fact_id AND fd.fade_ivaporc = 5)> 0 AND f.fact_cufe is null;";
            statement = connection.prepareStatement(sql);
            statement.setDate(1, new Date(fechaFinal.getTime()));
            statement.setDate(2, new Date(fechaInicial.getTime()));
            resultado = statement.executeQuery();

            ArrayList<VentaDataVo> ventas = new ArrayList<VentaDataVo>();

            while (resultado.next()) {
                ventaData = new VentaDataVo(resultado.getInt(1), resultado.getDouble(2), resultado.getDouble(3));
                ventas.add(ventaData);
            }

            return ventas;

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    boolean RecalcularVentas5() {
        try {

            FacturasDao facturasDao = new FacturasDao(connection);

            Calendar fechaInicial = new GregorianCalendar();
            Calendar fechaFinal = new GregorianCalendar();
            fechaInicial.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(fechaInicialString));
            fechaFinal.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(fechaFinalString));
            System.out.println("Inicia proceso de consulta de ventas 5%");
            int difA = fechaFinal.get(Calendar.YEAR) - fechaInicial.get(Calendar.YEAR);
            int cantMeses = difA * 12 + fechaFinal.get(Calendar.MONTH) - fechaInicial.get(Calendar.MONTH) + 1;
            Double totalVentas = ObtenerVentas5(
                    fechaInicial.getTime(),
                    fechaFinal.getTime());
            VentaVo[] ventas = new VentaVo[cantMeses];

            Double totalAnularDays = 0.0;
            int totalFacturas = 0;
            if (totalVentas > netoVent5) {
                for (int i = 0; i < cantMeses; i++) {
                    Calendar fechaInicialMes = new GregorianCalendar();
                    fechaInicialMes.setTime(fechaInicial.getTime());
                    fechaInicialMes.add(Calendar.MONTH, i);

                    Calendar diaInicialMes = new GregorianCalendar();
                    diaInicialMes.setTime(fechaInicialMes.getTime());
                    diaInicialMes.set(Calendar.DAY_OF_MONTH, 1);

                    Calendar diaFinalMes = new GregorianCalendar();
                    diaFinalMes.setTime(fechaInicialMes.getTime());
                    diaFinalMes.set(Calendar.DAY_OF_MONTH,
                            diaFinalMes.getActualMaximum(Calendar.DAY_OF_MONTH));

                    double total = ObtenerVentas5(diaInicialMes.getTime(), diaFinalMes.getTime());

                    float porcentaje = (float) (total / totalVentas * 100);

                    double totalNew = (netoVent5 * porcentaje) / 100;

                    VentaVo venta = new VentaVo(total, porcentaje, fechaInicialMes.get(Calendar.MONTH) + 1,
                            totalNew, total - totalNew);
                    ventas[i] = venta;

                    ArrayList<VentaDataVo> ventasData = ObtenerVentasData5(diaInicialMes.getTime(),
                            diaFinalMes.getTime());

                    double totalAnular = 0;
                    ArrayList<VentaDataVo> ventasAnular = new ArrayList<>();

                    for (VentaDataVo ventaData : ventasData) {
                        if (totalAnular + ventaData.getTotaliva() <= venta.getTotalAnular()) {
                            totalAnular += ventaData.getTotaliva();
                            ventasAnular.add(ventaData);
                            totalAnularDays += ventaData.getTotal();
                        }
                    }

                    totalFacturas += ventasAnular.size();

                    for (int j = 0; j < ventasAnular.size(); j++) {
                        int id = ventasAnular.get(j).getId();
                        facturasDao.AnularVentas(id);
                        // System.out.println("Anulada venta: " + (j + 1) + " " + id);
                    }

                }
                if (totalFacturas == 0) {
                    return true;
                }
            } else {
                boolean dato = false;
                while (totalVentas < netoVent5) {
                    ArrayList<FacturaVo> ventasAnuladas = facturasDao.ObtenerVentasAnuladas(fechaInicial.getTime(),
                            fechaFinal.getTime(), netoVent5 - totalVentas);

                    if (ventasAnuladas.size() == 0) {
                        dato = true;
                        break;
                    }

                    for (FacturaVo facturaVo : ventasAnuladas) {
                        totalVentas = ObtenerVentas5(fechaInicial.getTime(), fechaFinal.getTime());
                        if (netoVent5 - totalVentas > facturaVo.getFact_total()) {
                            facturasDao.ActivarVentas(facturaVo.getFact_id());
                            facturasDao.UpdateIva5(facturaVo.getFact_id());
                        } else {
                            break;
                        }

                    }

                }
                totalVentas = ObtenerVentas5(fechaInicial.getTime(), fechaFinal.getTime());
                return dato;
            }
            System.out.println("Total anular: " + totalAnularDays.intValue());
            System.out.println("Total facturas: " + totalFacturas);
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
