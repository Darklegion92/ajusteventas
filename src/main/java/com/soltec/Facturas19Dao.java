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

public class Facturas19Dao {
    double netoVent19;
    Connection connection;
    String fechaInicialString;
    String fechaFinalString;

    public Facturas19Dao(Connection connection, double netoVent19, String fechaInicialString,
            String fechaFinalString) {
        this.connection = connection;
        this.netoVent19 = netoVent19;
        this.fechaInicialString = fechaInicialString;
        this.fechaFinalString = fechaFinalString;
    }

    Double ObtenerVentas19(java.util.Date fechaInicial, java.util.Date fechaFinal) {

        try {

            PreparedStatement statement = null;
            ResultSet resultado = null;

            Double total = 0.0;

            String sql = "SELECT sum(fd.fade_total-fd.fade_ivamonto) FROM FACTURAS_DETALLE fd, FACTURAS f WHERE fd.fact_id = f.fact_id AND f.fact_fecha  <= ? AND f.fact_fecha >= ? AND f.fact_anulado = 'N' AND  fd.fade_anulado = 'N' AND fd.fade_tiva = 1";
            statement = connection.prepareStatement(sql);
            statement.setDate(1, new Date(fechaFinal.getTime()));
            statement.setDate(2, new Date(fechaInicial.getTime()));
            resultado = statement.executeQuery();

            if (resultado.next()) {
                total = resultado.getDouble(1);
            }

            return total;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    private ArrayList<VentaDataVo> ObtenerVentasData19(java.util.Date fechaInicial,
            java.util.Date fechaFinal) {
        try {

            PreparedStatement statement = null;
            ResultSet resultado = null;

            VentaDataVo ventaData = null;

            String sql = "SELECT f.fact_id, f.fact_total, (SELECT sum(fd.fade_total-fd.fade_ivamonto) FROM FACTURAS_DETALLE fd WHERE fd.fact_id = f.fact_id AND fd.fade_tiva = 1 ),(SELECT sum(fd.fade_total) FROM FACTURAS_DETALLE fd WHERE fd.fact_id = f.fact_id AND fd.fade_tiva = 2 )  FROM FACTURAS_DETALLE fd, FACTURAS f WHERE fd.fact_id = f.fact_id AND f.fact_fecha  <= ? AND f.fact_fecha >= ? AND f.fact_anulado = 'N' AND (SELECT count(*) FROM FACTURAS_DETALLE fd WHERE fd.fact_id = f.fact_id AND fd.fade_tiva = 1) > 0 AND f.fact_cufe is null";
            statement = connection.prepareStatement(sql);
            statement.setDate(1, new Date(fechaFinal.getTime()));
            statement.setDate(2, new Date(fechaInicial.getTime()));
            resultado = statement.executeQuery();

            ArrayList<VentaDataVo> ventas = new ArrayList<VentaDataVo>();

            while (resultado.next()) {
                ventaData = new VentaDataVo(resultado.getInt(1), resultado.getDouble(2), resultado.getDouble(3),
                        resultado.getDouble(4));
                ventas.add(ventaData);
            }

            return ventas;

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    boolean RecalcularVentas19() {
        try {

            FacturasDao facturasDao = new FacturasDao(connection);

            Calendar fechaInicial = new GregorianCalendar();
            Calendar fechaFinal = new GregorianCalendar();
            fechaInicial.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(fechaInicialString));
            fechaFinal.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(fechaFinalString));
            System.out.println("Inicia proceso de consulta de ventas 19%");
            int difA = fechaFinal.get(Calendar.YEAR) - fechaInicial.get(Calendar.YEAR);
            int cantMeses = difA * 12 + fechaFinal.get(Calendar.MONTH) - fechaInicial.get(Calendar.MONTH) + 1;
            Double totalVentas = ObtenerVentas19(
                    fechaInicial.getTime(),
                    fechaFinal.getTime());
            VentaVo[] ventas = new VentaVo[cantMeses];
            int totalFacturas = 0;
            Double totalAnularDays = 0.0;

            if (totalVentas > netoVent19) {

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

                    double total = ObtenerVentas19(diaInicialMes.getTime(), diaFinalMes.getTime());

                    float porcentaje = (float) (total / totalVentas * 100);

                    double totalNew = (netoVent19 * porcentaje) / 100;

                    VentaVo venta = new VentaVo(total, porcentaje, fechaInicialMes.get(Calendar.MONTH) + 1,
                            totalNew, total - totalNew);
                    ventas[i] = venta;

                    ArrayList<VentaDataVo> ventasData = ObtenerVentasData19(diaInicialMes.getTime(),
                            diaFinalMes.getTime());

                    double totalAnular = 0;
                    ArrayList<VentaDataVo> ventasAnular = new ArrayList<>();

                    for (VentaDataVo ventaData : ventasData) {
                        if (totalAnular + ventaData.getTotaliva() <= venta.getTotalAnular()
                                && ventaData.getIva5() == 0) {

                            totalAnular += ventaData.getTotaliva();
                            ventasAnular.add(ventaData);
                            totalAnularDays += ventaData.getTotal();
                        }
                    }
                    totalFacturas += ventasAnular.size();

                    for (int j = 0; j < ventasAnular.size(); j++) {
                        int id = ventasAnular.get(j).getId();
                        facturasDao.AnularVentas(id);
                        // System.out.println("Anulada venta: " + (j + 1));
                    }

                    if (ventasAnular.size() == 0 && i == cantMeses) {
                        break;
                    }
                }

                if (totalFacturas == 0) {
                    return true;
                }

            } else {

                boolean dato = false;
                while (totalVentas < netoVent19) {
                    ArrayList<FacturaVo> ventasAnuladas = facturasDao.ObtenerVentasAnuladas(fechaInicial.getTime(),
                            fechaFinal.getTime(), netoVent19 - totalVentas);

                    if (ventasAnuladas.size() == 0) {
                        dato = true;
                        break;
                    }

                    for (FacturaVo facturaVo : ventasAnuladas) {
                        totalVentas = ObtenerVentas19(fechaInicial.getTime(), fechaFinal.getTime());
                        if (netoVent19 - totalVentas > facturaVo.getFact_total()) {
                            facturasDao.ActivarVentas(facturaVo.getFact_id());
                            facturasDao.UpdateIva19(facturaVo.getFact_id());
                        } else {
                            break;
                        }

                    }

                }
                totalVentas = ObtenerVentas19(fechaInicial.getTime(), fechaFinal.getTime());
                return dato;

            }

            System.out.println("Total anular: " + totalAnularDays.intValue());
            System.out.println("Total facturas: " + totalFacturas);
            return false;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

}
