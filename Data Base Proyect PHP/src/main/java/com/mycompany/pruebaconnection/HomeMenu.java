package com.mycompany.pruebaconnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class HomeMenu extends JFrame {

    private Connection conexion;

    public HomeMenu() {
        // Configuración ventana
        setTitle("Menú Principal - Universidad");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        // Panel título
        JLabel titulo = new JLabel("Sistema de Gestión Universitaria", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        add(titulo, BorderLayout.NORTH);

        // Panel botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 30));
        panelBotones.setBackground(Color.WHITE);

        // Botones con estilo
        JButton btnCatedratico = createStyledButton("Catedráticos");
        JButton btnAeropuerto = createStyledButton("Aeropuertos");
        JButton btnCursos = createStyledButton("Cursos");
        JButton btnTestConexion = createStyledButton("Test Conexión BD");

        // Agregar botones al panel
        panelBotones.add(btnCatedratico);
        panelBotones.add(btnAeropuerto);
        panelBotones.add(btnCursos);
        panelBotones.add(btnTestConexion);

        add(panelBotones, BorderLayout.CENTER);

        // Eventos botones
        btnCatedratico.addActionListener(e -> {
            CatedraticoWindow ventana = new CatedraticoWindow();
            ventana.setVisible(true);
            this.dispose();
        });

        btnAeropuerto.addActionListener(e -> {
            AeropuertoWindow ventana = new AeropuertoWindow();
            ventana.setVisible(true);
            this.dispose();
        });

        btnCursos.addActionListener(e -> {
            CursosWindow ventana = new CursosWindow();
            ventana.setVisible(true);
            this.dispose();
        });

        btnTestConexion.addActionListener(e -> {
            testConexion();
        });

        // Crear conexión (opcional, puedes llamarla antes en main)
        conexion = getConnection();
    }

    private JButton createStyledButton(String texto) {
        JButton btn = new JButton(texto);
        btn.setPreferredSize(new Dimension(140, 40));
        btn.setFont(new Font("Tahoma", Font.BOLD, 14));
        btn.setBackground(new Color(30, 144, 255));  // Dodger Blue
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Método para obtener conexión a BD
    public static Connection getConnection() {
        String url = "jdbc:mysql://localhost:3306/universidad?useUnicode=true&characterEncoding=UTF-8";
        String usuario = "root";
        String contraseña = ""; // Pon tu contraseña si tienes

        try {
            return DriverManager.getConnection(url, usuario, contraseña);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error de conexión a la base de datos:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    // Método para mostrar resultado test de conexión
    private void testConexion() {
        if (conexion != null) {
            JOptionPane.showMessageDialog(this,
                    "Conexión exitosa a la base de datos.\nPuerto: 3306\nURL: jdbc:mysql://localhost:3306/universidad",
                    "Conexión", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No se pudo establecer conexión a la base de datos.",
                    "Conexión", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Main para probar
    public static void main(String[] args) {
        // Aplicar look and feel Nimbus para todo el proyecto
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Mostrar ventana principal
        SwingUtilities.invokeLater(() -> {
            new HomeMenu().setVisible(true);
        });
    }
}
