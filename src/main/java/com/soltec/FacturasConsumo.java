package com.soltec;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class FacturasConsumo {

    Connection connection;
    String fechaInicialString;
    String fechaFinalString;

    public FacturasConsumo(Connection connection, String fechaInicialString, String fechaFinalString) {
        this.connection = connection;
        this.fechaInicialString = fechaInicialString;
        this.fechaFinalString = fechaFinalString;
    }

    void RecalcularVentasConsumo() {
        try {
            System.err.println("Recalculando Ventas consumo");

            Calendar fechaInicial = new GregorianCalendar();
            Calendar fechaFinal = new GregorianCalendar();
            fechaInicial.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(fechaInicialString));
            fechaFinal.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(fechaFinalString));
            java.util.Date ff = fechaFinal.getTime();
            java.util.Date fi = fechaInicial.getTime();
            do {
                String sql = "SELECT first 500 fd.fact_id FROM FACTURAS_DETALLE fd, FACTURAS f WHERE fd.fact_id = f.fact_id AND f.fact_fecha  <= ? AND f.fact_fecha >= ? AND f.fact_anulado = 'N' AND fd.fade_consumo > 0";
                PreparedStatement statement = null;
                ResultSet resultado = null;
                statement = connection.prepareStatement(sql);
                statement.setDate(1, new Date(ff.getTime()));
                statement.setDate(2, new Date(fi.getTime()));

                resultado = statement.executeQuery();
                ArrayList<String> lista = new ArrayList<String>();
                while (resultado.next()) {
                    lista.add(resultado.getString(1));

                }
                resultado.close();

                if (lista.size() == 0) {
                    break;
                }

                for (String id : lista) {
                    String sql2 = "UPDATE FACTURAS_DETALLE SET fade_consumo=0  WHERE fact_id = ?";
                    statement = connection.prepareStatement(sql2);
                    statement.setString(1, id);
                    statement.executeUpdate();

                    sql2 = "select * from  RECALCULA_IVA_FACTURA(?)";
                    statement = connection.prepareStatement(sql2);
                    statement.setString(1, id);
                    statement.execute();
                }

            } while (true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
