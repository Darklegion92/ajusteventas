package com.soltec;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

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

            Facturas551 facturas551 = new Facturas551(connection, fechaInicialString, fechaFinalString);

            FacturasConsumo facturasConsumo = new FacturasConsumo(connection, fechaInicialString, fechaFinalString);

            Facturas0Dao myFacturas0Dao = new Facturas0Dao(connection, netoVent0, fechaInicialString,
                    fechaFinalString);

            myDevolucionesDao.RecalcularDevoluciones();

            myFacturas5Dao.RecalcularVentas5();

            facturas551.RecalcularVentas551();

            facturasConsumo.RecalcularVentasConsumo();

            Calendar fechaInicial = new GregorianCalendar();
            Calendar fechaFinal = new GregorianCalendar();
            fechaInicial.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(fechaInicialString));
            fechaFinal.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(fechaFinalString));
            Double ventas0 = myFacturas0Dao.ObtenerVentas0(fechaInicial.getTime(),
                    fechaFinal.getTime());

            while (ventas0 > netoVent0) {
                boolean notInvoices = myFacturas0Dao.RecalcularVentas0();
                ventas0 = myFacturas0Dao.ObtenerVentas0(fechaInicial.getTime(),
                        fechaFinal.getTime());

                if (notInvoices) {
                    break;
                }
                connection.close();
                conectFirebird = new ConexionFirebird();
                connection = conectFirebird.getConnection();

                myFacturas0Dao = new Facturas0Dao(connection, netoVent0, fechaInicialString,
                        fechaFinalString);
            }

            Facturas19Dao myFacturas19Dao = new Facturas19Dao(connection, netoVent19, fechaInicialString,
                    fechaFinalString);

            myFacturas19Dao.RecalcularVentas19();

            /*
             * myFacturas19Dao.RecalcularVentas19();
             * myProductosDao.AnularProductos();
             */

            conectFirebird.desconectar();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
