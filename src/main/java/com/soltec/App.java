package com.soltec;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Hello world!
 */
public final class App {

    static double netoDevol = 5265000;
    static double netoVent5 = 228426000;
    static double netoVent19 = 510945000;
    static double netoVent0 = 775947000;
    static String fechaInicialString = "01/05/2020";
    static String fechaFinalString = "31/08/2020";

    private App() {
    }

    /**
     * Says hello to the world.
     *
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {

        ConexionFirebird conectFirebird;
        try {
            conectFirebird = new ConexionFirebird();

            Connection connection = conectFirebird.getConnection();

            DevolucionesDao myDevolucionesDao = new DevolucionesDao(connection, netoDevol, fechaInicialString,
                    fechaFinalString);
            Facturas5Dao myFacturas5Dao = new Facturas5Dao(connection, netoVent5, fechaInicialString,
                    fechaFinalString);
            Facturas19Dao myFacturas19Dao = new Facturas19Dao(connection, netoVent19, fechaInicialString,
                    fechaFinalString);
            Facturas0Dao myFacturas0Dao = new Facturas0Dao(connection, netoVent0, fechaInicialString,
                    fechaFinalString);
            ProductosDao myProductosDao = new ProductosDao(connection, netoVent0, fechaInicialString, fechaFinalString);

            myDevolucionesDao.RecalcularDevoluciones();

            myFacturas5Dao.RecalcularVentas5();
            /*
             * myFacturas19Dao.RecalcularVentas19();
             * myFacturas0Dao.RecalcularVentas0();
             * myProductosDao.AnularProductos();
             */

            conectFirebird.desconectar();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

}
