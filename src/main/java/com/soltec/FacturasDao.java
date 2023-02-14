package com.soltec;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FacturasDao {

    Connection connection;

    public FacturasDao(Connection connection) {
        this.connection = connection;
    }

    void AnularVentas(int idVenta) {
        try {
            PreparedStatement statement = null;
            String sql = "UPDATE FACTURAS SET FACT_ANULADO = 'S' WHERE FACT_ID = ?";

            statement = connection.prepareStatement(sql);
            statement.setInt(1, idVenta);
            statement.executeUpdate();

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
