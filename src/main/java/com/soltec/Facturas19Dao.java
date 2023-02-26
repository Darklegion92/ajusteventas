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

    private Double ObtenerVentas19(java.util.Date fechaInicial, java.util.Date fechaFinal) {

        try {

            PreparedStatement statement = null;
            ResultSet resultado = null;

            Double total = 0.0;

            String sql = "SELECT sum(fd.fade_total) FROM FACTURAS_DETALLE fd, FACTURAS f WHERE fd.fact_id = f.fact_id AND f.fact_fecha  <= ? AND f.fact_fecha >= ? AND f.fact_anulado = 'N' AND  fd.fade_anulado = 'N' AND fd.fade_tiva = 1";
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

    void RecalcularVentas19() {
        try {

            FacturasDao facturasDao = new FacturasDao(connection);

            Calendar fechaInicial = new GregorianCalendar();
            Calendar fechaFinal = new GregorianCalendar();
            fechaInicial.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(fechaInicialString));
            fechaFinal.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(fechaFinalString));
            System.out.println("Inicia proceso de consulta de ventas 19%");
            // consultar facturas anuladas hasta completar el monto de ventas 19%

            Double ventas = ObtenerVentas19(fechaInicial.getTime(), fechaFinal.getTime());

            while (ventas < netoVent19) {
                ArrayList<FacturaVo> ventasAnuladas = facturasDao.ObtenerVentasAnuladas(fechaInicial.getTime(),
                        fechaFinal.getTime(), netoVent19 - ventas);
                System.err.println("Ventas anuladas: " + ventasAnuladas.size());
                if (ventasAnuladas.size() <= 1) {
                    break;
                }

                for (FacturaVo facturaVo : ventasAnuladas) {
                    if (netoVent19 - ventas > facturaVo.getFact_total()) {
                        facturasDao.ActivarVentas(facturaVo.getFact_id());
                        facturasDao.UpdateIva19(facturaVo.getFact_id());
                    } else {
                        break;
                    }

                }

                ventas = ObtenerVentas19(fechaInicial.getTime(), fechaFinal.getTime());
            }

        } catch (

        Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

}
