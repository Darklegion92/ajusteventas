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

/**
 * Hello world!
 */
public final class App {

    static double netoDevol = 5265000;
    static double netoVent5 = 228426000;
    static double netoVent19 = 510945000;
    static double netoVent0 = 775947000;
    static String fechaInicialString = "01/07/2020";
    static String fechaFinalString = "31/10/2020";

    private App() {
    }

    /**
     * Says hello to the world.
     *
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        RecalcularDevoluciones();
        RecalcularVentas5();
        // RecalcularVentas19();
        // RecalcularVentas0();
    }

    private static Double ObtenerDevoluciones(java.util.Date fechaInicial, java.util.Date fechaFinal) {
        try {
            ConexionFirebird conectFirebird = new ConexionFirebird();

            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultado = null;

            Double total = 0.0;

            connection = conectFirebird.getConnection();

            String sql = "SELECT sum(d.devt_total) FROM DEVOLUCIONES_VENTAS d WHERE d.devt_fecha <= ? AND d.devt_fecha >= ? AND d.devt_anulado = 'N';";
            statement = connection.prepareStatement(sql);
            statement.setDate(1, new Date(fechaFinal.getTime()));
            statement.setDate(2, new Date(fechaInicial.getTime()));
            resultado = statement.executeQuery();

            if (resultado.next()) {
                total = resultado.getDouble(1);
            }
            conectFirebird.desconectar();
            return total;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (ClassNotFoundException er) {
            System.out.println("Error: clsss" + er.getMessage());
        }
        return null;
    }

    private static ArrayList<DevolucionDataVo> ObtenerDevolucionesData(java.util.Date fechaInicial,
            java.util.Date fechaFinal) {
        try {
            ConexionFirebird conectFirebird = new ConexionFirebird();

            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultado = null;

            DevolucionDataVo devolucionData = null;

            connection = conectFirebird.getConnection();

            String sql = "SELECT d.devt_id, d.devt_total FROM DEVOLUCIONES_VENTAS d WHERE d.devt_fecha <= ? AND d.devt_fecha >= ? AND d.devt_anulado = 'N'";
            statement = connection.prepareStatement(sql);
            statement.setDate(1, new Date(fechaFinal.getTime()));
            statement.setDate(2, new Date(fechaInicial.getTime()));
            resultado = statement.executeQuery();

            ArrayList<DevolucionDataVo> devoluciones = new ArrayList<DevolucionDataVo>();

            while (resultado.next()) {
                devolucionData = new DevolucionDataVo(resultado.getInt(1), resultado.getDouble(2));
                devoluciones.add(devolucionData);
            }
            conectFirebird.desconectar();
            return devoluciones;

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (ClassNotFoundException er) {
            System.out.println("Error: clsss" + er.getMessage());
        }
        return null;
    }

    private static void AnularDevoluciones(int idDevolucion) {
        try {
            ConexionFirebird conectFirebird = new ConexionFirebird();

            Connection connection = null;
            PreparedStatement statement = null;

            connection = conectFirebird.getConnection();

            String sql = "UPDATE DEVOLUCIONES_VENTAS SET DEVT_ANULADO = 'S' WHERE DEVT_ID = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, idDevolucion);
            statement.executeUpdate();

            conectFirebird.desconectar();

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (ClassNotFoundException er) {
            System.out.println("Error: clsss" + er.getMessage());
        }
    }

    private static Double ObtenerVentas5(java.util.Date fechaInicial, java.util.Date fechaFinal) {
        try {
            ConexionFirebird conectFirebird = new ConexionFirebird();

            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultado = null;

            Double total = 0.0;

            connection = conectFirebird.getConnection();

            String sql = "SELECT sum(fd.fade_total) FROM FACTURAS_DETALLE fd, FACTURAS f WHERE fd.fact_id = f.fact_id AND f.fact_fecha  <= ? AND f.fact_fecha >= ? AND f.fact_anulado = 'N' AND fd.fade_ivaporc = 5;";
            statement = connection.prepareStatement(sql);
            statement.setDate(1, new Date(fechaFinal.getTime()));
            statement.setDate(2, new Date(fechaInicial.getTime()));
            resultado = statement.executeQuery();

            if (resultado.next()) {
                total = resultado.getDouble(1);
            }
            conectFirebird.desconectar();
            return total;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (ClassNotFoundException er) {
            System.out.println("Error: clsss" + er.getMessage());
        }
        return null;
    }

    private static ArrayList<VentaDataVo> ObtenerVentasData5(java.util.Date fechaInicial,
            java.util.Date fechaFinal) {
        try {
            ConexionFirebird conectFirebird = new ConexionFirebird();

            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultado = null;

            VentaDataVo ventaData = null;

            connection = conectFirebird.getConnection();

            String sql = "SELECT f.fact_id, f.fact_total FROM FACTURAS f WHERE f.fact_fecha  <= ? AND f.fact_fecha >= ? AND f.fact_anulado = 'N' AND (SELECT count(*) FROM FACTURAS_DETALLE fd WHERE fd.fact_id = f.fact_id AND fd.fade_ivaporc = 5)> 0";
            statement = connection.prepareStatement(sql);
            statement.setDate(1, new Date(fechaFinal.getTime()));
            statement.setDate(2, new Date(fechaInicial.getTime()));
            resultado = statement.executeQuery();

            ArrayList<VentaDataVo> ventas = new ArrayList<VentaDataVo>();

            while (resultado.next()) {
                ventaData = new VentaDataVo(resultado.getInt(1), resultado.getDouble(2));
                ventas.add(ventaData);
            }
            conectFirebird.desconectar();
            return ventas;

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (ClassNotFoundException er) {
            System.out.println("Error: clsss" + er.getMessage());
        }
        return null;
    }

    private static Double ObtenerVentas19(java.util.Date fechaInicial, java.util.Date fechaFinal) {
        try {
            ConexionFirebird conectFirebird = new ConexionFirebird();

            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultado = null;

            Double total = 0.0;

            connection = conectFirebird.getConnection();

            String sql = "SELECT sum(fd.fade_total) FROM FACTURAS_DETALLE fd, FACTURAS f WHERE fd.fact_id = f.fact_id AND f.fact_fecha  <= ? AND f.fact_fecha >= ? AND f.fact_anulado = 'N' AND fd.fade_ivaporc = 19 AND fd.fade_ivaporc <> 5;";
            statement = connection.prepareStatement(sql);
            statement.setDate(1, new Date(fechaFinal.getTime()));
            statement.setDate(2, new Date(fechaInicial.getTime()));
            resultado = statement.executeQuery();

            if (resultado.next()) {
                total = resultado.getDouble(1);
            }
            conectFirebird.desconectar();
            return total;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (ClassNotFoundException er) {
            System.out.println("Error: clsss" + er.getMessage());
        }
        return null;
    }

    private static ArrayList<VentaDataVo> ObtenerVentasData19(java.util.Date fechaInicial,
            java.util.Date fechaFinal) {
        try {
            ConexionFirebird conectFirebird = new ConexionFirebird();

            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultado = null;

            VentaDataVo ventaData = null;

            connection = conectFirebird.getConnection();

            String sql = "SELECT f.fact_id, f.fact_total FROM FACTURAS f WHERE f.fact_fecha  <= ? AND f.fact_fecha >= ? AND f.fact_anulado = 'N' AND (SELECT count(*) FROM FACTURAS_DETALLE fd WHERE fd.fact_id = f.fact_id AND fd.fade_ivaporc = 19 AND fd.fade_ivaporc <> 5)> 0";
            statement = connection.prepareStatement(sql);
            statement.setDate(1, new Date(fechaFinal.getTime()));
            statement.setDate(2, new Date(fechaInicial.getTime()));
            resultado = statement.executeQuery();

            ArrayList<VentaDataVo> ventas = new ArrayList<VentaDataVo>();

            while (resultado.next()) {
                ventaData = new VentaDataVo(resultado.getInt(1), resultado.getDouble(2));
                ventas.add(ventaData);
            }
            conectFirebird.desconectar();
            return ventas;

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (ClassNotFoundException er) {
            System.out.println("Error: clsss" + er.getMessage());
        }
        return null;
    }

    private static Double ObtenerVentas0(java.util.Date fechaInicial, java.util.Date fechaFinal) {
        try {
            ConexionFirebird conectFirebird = new ConexionFirebird();

            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultado = null;

            Double total = 0.0;

            connection = conectFirebird.getConnection();

            String sql = "SELECT f.fact_id, f.fact_total FROM FACTURAS f WHERE f.fact_fecha  <= ? AND f.fact_fecha >= ? AND f.fact_anulado = 'N' AND (SELECT count(*) FROM FACTURAS_DETALLE fd WHERE fd.fact_id = f.fact_id AND fd.fade_ivaporc = 0 AND fd.fade_ivaporc <> 5 AND fd.fade_ivaporc <> 19)> 0";

            statement = connection.prepareStatement(sql);
            statement.setDate(1, new Date(fechaFinal.getTime()));
            statement.setDate(2, new Date(fechaInicial.getTime()));
            resultado = statement.executeQuery();

            if (resultado.next()) {
                total = resultado.getDouble(1);
            }
            conectFirebird.desconectar();
            return total;
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (ClassNotFoundException er) {
            System.out.println("Error: clsss" + er.getMessage());
        }
        return null;
    }

    private static ArrayList<VentaDataVo> ObtenerVentasData0(java.util.Date fechaInicial,
            java.util.Date fechaFinal) {
        try {
            ConexionFirebird conectFirebird = new ConexionFirebird();

            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultado = null;

            VentaDataVo ventaData = null;

            connection = conectFirebird.getConnection();

            String sql = "SELECT f.fact_id, f.fact_total FROM FACTURAS_DETALLE fd, FACTURAS f WHERE fd.fact_id = f.fact_id AND f.fact_fecha  <= ? AND f.fact_fecha >= ? AND f.fact_anulado = 'N' AND fd.fade_ivaporc = 0 AND fd.fade_ivaporc <> 19 AND fd.fade_ivaporc <> 5;";
            statement = connection.prepareStatement(sql);
            statement.setDate(1, new Date(fechaFinal.getTime()));
            statement.setDate(2, new Date(fechaInicial.getTime()));
            resultado = statement.executeQuery();

            ArrayList<VentaDataVo> ventas = new ArrayList<VentaDataVo>();

            while (resultado.next()) {
                ventaData = new VentaDataVo(resultado.getInt(1), resultado.getDouble(2));
                ventas.add(ventaData);
            }
            conectFirebird.desconectar();
            return ventas;

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (ClassNotFoundException er) {
            System.out.println("Error: clsss" + er.getMessage());
        }
        return null;
    }

    private static void AnularVentas(int[] idsVenta) {
        try {
            ConexionFirebird conectFirebird = new ConexionFirebird();

            Connection connection = null;
            PreparedStatement statement = null;

            connection = conectFirebird.getConnection();

            String sql = "UPDATE FACTURAS SET FACT_ANULADO = 'S' WHERE FACT_ID IN (";

            for (int i = 0; i < idsVenta.length; i++) {
                sql += idsVenta[i];
                if (i < idsVenta.length - 1) {
                    sql += ",";
                }
            }
            sql += ");";

            statement = connection.prepareStatement(sql);

            statement.executeUpdate();

            conectFirebird.desconectar();

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (ClassNotFoundException er) {
            System.out.println("Error: clsss" + er.getMessage());
        }
    }

    private static void RecalcularDevoluciones() {
        try {

            Calendar fechaInicial = new GregorianCalendar();
            Calendar fechaFinal = new GregorianCalendar();
            fechaInicial.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(fechaInicialString));
            fechaFinal.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(fechaFinalString));
            System.out.println("Inicia proceso de consulta de devoluciones");
            int difA = fechaFinal.get(Calendar.YEAR) - fechaInicial.get(Calendar.YEAR);
            int cantMeses = difA * 12 + fechaFinal.get(Calendar.MONTH) - fechaInicial.get(Calendar.MONTH) + 1;
            Double totalDevoluciones = ObtenerDevoluciones(
                    fechaInicial.getTime(),
                    fechaFinal.getTime());
            DevolucionVo[] devoluciones = new DevolucionVo[cantMeses];

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

                double total = ObtenerDevoluciones(diaInicialMes.getTime(), diaFinalMes.getTime());

                float porcentaje = (float) (total / totalDevoluciones * 100);

                double totalNew = (netoDevol * porcentaje) / 100;

                DevolucionVo devolucion = new DevolucionVo(total, porcentaje, fechaInicialMes.get(Calendar.MONTH) + 1,
                        totalNew, total - totalNew);
                devoluciones[i] = devolucion;

                // obtener todas las devoluciones
                ArrayList<DevolucionDataVo> devolucionesData = ObtenerDevolucionesData(diaInicialMes.getTime(),
                        diaFinalMes.getTime());

                double totalAnular = 0;
                ArrayList<DevolucionDataVo> devolucionesAnular = new ArrayList<>();

                for (DevolucionDataVo devolucionData : devolucionesData) {
                    if (totalAnular + devolucionData.getTotal() <= devolucion.getTotalAnular()) {
                        totalAnular += devolucionData.getTotal();
                        devolucionesAnular.add(devolucionData);
                    }
                }

                for (int j = 0; j < devolucionesAnular.size(); j++) {
                    int id = devolucionesAnular.get(j).getId();
                    AnularDevoluciones(id);
                    System.out.println("Anulada devolucion: " + (j + 1));
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void RecalcularVentas5() {
        try {
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

                // obtener todas las devoluciones
                ArrayList<VentaDataVo> ventasData = ObtenerVentasData5(diaInicialMes.getTime(),
                        diaFinalMes.getTime());

                double totalAnular = 0;
                ArrayList<VentaDataVo> ventasAnular = new ArrayList<>();

                for (VentaDataVo ventaData : ventasData) {
                    if (totalAnular + ventaData.getTotal() <= venta.getTotalAnular()) {
                        totalAnular += ventaData.getTotal();
                        ventasAnular.add(ventaData);
                    }
                }

                for (int j = 0; j < ventasAnular.size(); j++) {
                    int id = ventasAnular.get(j).getId();
                    AnularDevoluciones(id);
                    System.out.println("Anulada venta: " + (j + 1) + " " + id);
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void RecalcularVentas19() {
        try {
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

                // obtener todas las devoluciones
                ArrayList<VentaDataVo> ventasData = ObtenerVentasData19(diaInicialMes.getTime(),
                        diaFinalMes.getTime());

                double totalAnular = 0;
                ArrayList<VentaDataVo> ventasAnular = new ArrayList<>();

                for (VentaDataVo ventaData : ventasData) {
                    if (totalAnular + ventaData.getTotal() <= venta.getTotalAnular()) {
                        totalAnular += ventaData.getTotal();
                        ventasAnular.add(ventaData);
                    }
                }

                for (int j = 0; j < ventasAnular.size(); j++) {
                    int id = ventasAnular.get(j).getId();
                    AnularDevoluciones(id);
                    System.out.println("Anulada venta: " + (j + 1));
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void RecalcularVentas0() {
        try {
            Calendar fechaInicial = new GregorianCalendar();
            Calendar fechaFinal = new GregorianCalendar();
            fechaInicial.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(fechaInicialString));
            fechaFinal.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(fechaFinalString));
            System.out.println("Inicia proceso de consulta de ventas exentas");
            int difA = fechaFinal.get(Calendar.YEAR) - fechaInicial.get(Calendar.YEAR);
            int cantMeses = difA * 12 + fechaFinal.get(Calendar.MONTH) - fechaInicial.get(Calendar.MONTH) + 1;
            Double totalVentas = ObtenerVentas0(
                    fechaInicial.getTime(),
                    fechaFinal.getTime());
            VentaVo[] ventas = new VentaVo[cantMeses];

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

                double total = ObtenerVentas0(diaInicialMes.getTime(), diaFinalMes.getTime());

                float porcentaje = (float) (total / totalVentas * 100);

                double totalNew = (netoVent0 * porcentaje) / 100;

                VentaVo venta = new VentaVo(total, porcentaje, fechaInicialMes.get(Calendar.MONTH) + 1,
                        totalNew, total - totalNew);
                ventas[i] = venta;

                // obtener todas las devoluciones
                ArrayList<VentaDataVo> ventasData = ObtenerVentasData0(diaInicialMes.getTime(),
                        diaFinalMes.getTime());

                double totalAnular = 0;
                ArrayList<VentaDataVo> ventasAnular = new ArrayList<>();

                for (VentaDataVo ventaData : ventasData) {
                    if (totalAnular + ventaData.getTotal() <= venta.getTotalAnular()) {
                        totalAnular += ventaData.getTotal();
                        ventasAnular.add(ventaData);
                    }
                }

                for (int j = 0; j < ventasAnular.size(); j++) {
                    int id = ventasAnular.get(j).getId();
                    AnularDevoluciones(id);
                    System.out.println("Anulada venta: " + (j + 1));
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
