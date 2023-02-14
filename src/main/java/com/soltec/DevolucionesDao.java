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

public class DevolucionesDao {

    Connection connection;
    double netoDevol;
    String fechaInicialString;
    String fechaFinalString;

    public DevolucionesDao(Connection connection, double netoDevol, String fechaInicialString,
            String fechaFinalString) {
        this.connection = connection;
        this.netoDevol = netoDevol;
        this.fechaInicialString = fechaInicialString;
        this.fechaFinalString = fechaFinalString;
    }

    void RecalcularDevoluciones() {
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

            Double totalAnularDays = 0.0;
            int totalDev = 0;

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
                        totalAnularDays += devolucionData.getTotal();
                        devolucionesAnular.add(devolucionData);
                    }
                }
                totalDev += devolucionesAnular.size();

                for (int j = 0; j < devolucionesAnular.size(); j++) {
                    int id = devolucionesAnular.get(j).getId();
                    AnularDevoluciones(id);
                    // System.out.println("Anulada devolucion: " + (j + 1));
                }
            }

            System.out.println("Total anular: " + totalAnularDays.intValue());
            System.out.println("Total devoluciones: " + totalDev);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private Double ObtenerDevoluciones(java.util.Date fechaInicial, java.util.Date fechaFinal) {
        try {

            PreparedStatement statement = null;
            ResultSet resultado = null;

            Double total = 0.0;

            String sql = "SELECT sum(d.devt_total) FROM DEVOLUCIONES_VENTAS d WHERE d.devt_fecha <= ? AND d.devt_fecha >= ? AND d.devt_anulado = 'N';";
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

    private ArrayList<DevolucionDataVo> ObtenerDevolucionesData(java.util.Date fechaInicial,
            java.util.Date fechaFinal) {
        try {

            PreparedStatement statement = null;
            ResultSet resultado = null;

            DevolucionDataVo devolucionData = null;

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

            return devoluciones;

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    private void AnularDevoluciones(int idDevolucion) {
        try {

            PreparedStatement statement = null;

            String sql = "UPDATE DEVOLUCIONES_VENTAS SET DEVT_ANULADO = 'S' WHERE DEVT_ID = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, idDevolucion);
            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
