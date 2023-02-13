package com.soltec;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionFirebird {
    String URL = "jdbc:firebirdsql://localhost:3050/c:/sysplus/Datos/PRU/sysplus.fdb?lc_ctype=ISO8859_1";
    String Usuario = "sysdba";
    String Contrasena = "masterkey";
    String Driver = "org.firebirdsql.jdbc.FBDriver";
    Connection con;

    public ConexionFirebird() throws ClassNotFoundException, SQLException {

        con = null;

        Class.forName(Driver);
        System.out.println("Conectando");
        con = DriverManager.getConnection(URL, Usuario, Contrasena);
    }

    public Connection getConnection() {
        return con;
    }

    public void desconectar() {
        con = null;
    }
}
