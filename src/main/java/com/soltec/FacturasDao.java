package com.soltec;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class FacturasDao {

    Connection connection;

    public FacturasDao(Connection connection) {
        this.connection = connection;
    }

    ArrayList<FacturaVo> ObtenerVentasAnuladas(java.util.Date fechaInicial, java.util.Date fechaFinal, double value) {

        try {

            PreparedStatement statement = null;
            ResultSet resultado = null;

            String sql = "SELECT FIRST 500 f.fact_id, f.fact_total FROM FACTURAS f WHERE f.fact_fecha  <= ? AND f.fact_fecha >= ? AND f.fact_anulado = 'S' AND f.fact_total<? ORDER BY f.fact_total desc";
            statement = connection.prepareStatement(sql);
            statement.setDate(1, new Date(fechaFinal.getTime()));
            statement.setDate(2, new Date(fechaInicial.getTime()));
            statement.setDouble(3, value);
            resultado = statement.executeQuery();
            ArrayList<FacturaVo> ventas = new ArrayList<FacturaVo>();
            if (resultado.next()) {
                FacturaVo facturaVo = new FacturaVo(resultado.getInt(1), resultado.getDouble(1));
                ventas.add(facturaVo);
            }

            return ventas;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    void AnularVentas(int idVenta) {
        try {
            PreparedStatement statement = null;
            String sql = "UPDATE FACTURAS SET FACT_ANULADO = 'S' WHERE FACT_ID = ?";

            statement = connection.prepareStatement(sql);
            statement.setInt(1, idVenta);
            statement.executeUpdate();

            /*
             * sql = "UPDATE FACTURAS_DETALLE SET FADE_ANULADO = 'S' WHERE FACT_ID = ?";
             * statement = connection.prepareStatement(sql);
             * statement.setInt(1, idVenta);
             * statement.executeUpdate();
             */

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    void ActivarVentas(int idVenta) {
        try {
            PreparedStatement statement = null;
            String sql = "UPDATE FACTURAS SET FACT_ANULADO = 'N' WHERE FACT_ID = ?";

            statement = connection.prepareStatement(sql);
            statement.setInt(1, idVenta);
            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    void UpdateIva19(int id) {
        try {
            PreparedStatement statement = null;
            String sql2 = "UPDATE FACTURAS_DETALLE SET fade_tiva = 1, fade_ivaporc=19, fade_ivamonto=fade_total/1.19  WHERE fact_id = ?";

            statement = connection.prepareStatement(sql2);
            statement.setDouble(1, id);
            statement.executeUpdate();

            sql2 = "select * from  RECALCULA_IVA_FACTURA(?)";
            statement = connection.prepareStatement(sql2);
            statement.setInt(1, id);
            statement.execute();

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
