package com.mycompany.pruebaconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PruebaConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/universidad?useUnicode=true&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Método para obtener la conexión
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Connection conexion = getConnection()) {
            System.out.println("Conexion exitosa a la base de datos.");
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
        }

        // Aquí puedes llamar al HomeMenu para mostrar la interfaz gráfica
        javax.swing.SwingUtilities.invokeLater(() -> {
            new HomeMenu().setVisible(true);
        });
    }
}
