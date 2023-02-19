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

public class ProductosDao {

    Connection connection;
    double netoVent0;
    String fechaInicialString;
    String fechaFinalString;

    public ProductosDao(Connection connection, double netoVent0, String fechaInicialString,
            String fechaFinalString) {
        this.connection = connection;
        this.netoVent0 = netoVent0;
        this.fechaInicialString = fechaInicialString;
        this.fechaFinalString = fechaFinalString;
    }

    private void AnularProductos(int idProducto) {
        try {

            PreparedStatement statement = null;

            String sql = "UPDATE FACTURAS_DETALLE SET FADE_ANULADO = 'S' WHERE FADE_ITEM = ?";

            statement = connection.prepareStatement(sql);
            statement.setInt(1, idProducto);
            statement.executeUpdate();

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private ArrayList<ProductosDataVo> ObtenerProductosData0(java.util.Date fechaInicial,
            java.util.Date fechaFinal) {
        try {

            PreparedStatement statement = null;
            ResultSet resultado = null;

            ProductosDataVo productoData = null;

            String sql = "SELECT fd.fade_item, fd.fade_total FROM FACTURAS_DETALLE fd, FACTURAS f WHERE fd.fact_id = f.fact_id AND f.fact_fecha  <= ? AND f.fact_fecha >= ? AND f.fact_anulado = 'N' AND fd.fade_ivaporc = 0";
            statement = connection.prepareStatement(sql);
            statement.setDate(1, new Date(fechaFinal.getTime()));
            statement.setDate(2, new Date(fechaInicial.getTime()));
            resultado = statement.executeQuery();

            ArrayList<ProductosDataVo> productos = new ArrayList<ProductosDataVo>();

            while (resultado.next()) {
                productoData = new ProductosDataVo(resultado.getInt(1), resultado.getDouble(2));
                productos.add(productoData);
            }

            return productos;

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }

    void AnularProductos() {

        try {

            Facturas0Dao facturas0Dao = new Facturas0Dao(connection, netoVent0, fechaInicialString,
                    fechaFinalString);

            Calendar fechaInicial = new GregorianCalendar();
            Calendar fechaFinal = new GregorianCalendar();
            fechaInicial.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(fechaInicialString));
            fechaFinal.setTime(new SimpleDateFormat("dd/MM/yyyy").parse(fechaFinalString));
            System.out.println("Inicia proceso de consulta de productos exentos");
            int difA = fechaFinal.get(Calendar.YEAR) - fechaInicial.get(Calendar.YEAR);
            int cantMeses = difA * 12 + fechaFinal.get(Calendar.MONTH) - fechaInicial.get(Calendar.MONTH) + 1;
            Double totalVentas = facturas0Dao.ObtenerVentas0(
                    fechaInicial.getTime(),
                    fechaFinal.getTime());
            VentaVo[] ventas = new VentaVo[cantMeses];

            Double totalAnularDays = 0.0;
            int totalProductos = 0;
            do {
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

                    double total = facturas0Dao.ObtenerVentas0(diaInicialMes.getTime(), diaFinalMes.getTime());

                    float porcentaje = (float) (total / totalVentas * 100);

                    double totalNew = (netoVent0 * porcentaje) / 100;

                    VentaVo venta = new VentaVo(total, porcentaje, fechaInicialMes.get(Calendar.MONTH) + 1,
                            totalNew, total - totalNew);
                    ventas[i] = venta;

                    // obtener todas las devoluciones
                    ArrayList<ProductosDataVo> productosData = ObtenerProductosData0(diaInicialMes.getTime(),
                            diaFinalMes.getTime());

                    double totalAnular = 0;
                    ArrayList<ProductosDataVo> productosAnular = new ArrayList<>();

                    for (ProductosDataVo productoData : productosData) {
                        if (totalAnular + productoData.getTotal() <= venta.getTotalAnular()) {

                            totalAnular += productoData.getTotal();
                            productosAnular.add(productoData);
                            totalAnularDays += productoData.getTotal();
                        }
                    }
                    totalProductos += productosAnular.size();
                    System.out.println("productos a anular: " + productosAnular.size());

                    for (int j = 0; j < productosAnular.size(); j++) {
                        int id = productosAnular.get(j).getId();
                        AnularProductos(id);
                        System.out.println("Anulado producto: " + (j + 1));
                    }
                }
                System.out.println("Total anular: " + totalAnularDays.intValue());
                System.out.println("Total productos: " + totalProductos);
            } while (totalAnularDays.intValue() > 0);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
