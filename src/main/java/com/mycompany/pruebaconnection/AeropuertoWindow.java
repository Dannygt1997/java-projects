package com.mycompany.pruebaconnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class AeropuertoWindow extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtNombre, txtCiudad, txtPais;
    private JButton btnAgregar, btnActualizar, btnEliminar, btnVolver;

    public AeropuertoWindow() {
        setTitle("Gestión de Aeropuertos");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        model = new DefaultTableModel();
        table = new JTable(model);

        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Ciudad");
        model.addColumn("País");

        cargarDatos();

        JPanel panelFormulario = new JPanel(new GridLayout(3, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Formulario Aeropuerto"));

        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Ciudad:"));
        txtCiudad = new JTextField();
        panelFormulario.add(txtCiudad);

        panelFormulario.add(new JLabel("País:"));
        txtPais = new JTextField();
        panelFormulario.add(txtPais);

        btnAgregar = new JButton("Agregar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnVolver = new JButton("Volver al menú");

        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnAgregar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnVolver);

        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(panelFormulario, BorderLayout.NORTH);
        add(panelBotones, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregarAeropuerto());
        btnActualizar.addActionListener(e -> actualizarAeropuerto());
        btnEliminar.addActionListener(e -> eliminarAeropuerto());
        btnVolver.addActionListener(e -> {
            new HomeMenu().setVisible(true);
            this.dispose();
        });

        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int fila = table.getSelectedRow();
                txtNombre.setText(model.getValueAt(fila, 1).toString());
                txtCiudad.setText(model.getValueAt(fila, 2).toString());
                txtPais.setText(model.getValueAt(fila, 3).toString());
            }
        });
    }

    private void cargarDatos() {
        String sql = "SELECT * FROM aeropuerto";
        try (Connection conexion = PruebaConnection.getConnection();
             Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            model.setRowCount(0);
            while (rs.next()) {
                Vector<Object> fila = new Vector<>();
                fila.add(rs.getInt("id"));
                fila.add(rs.getString("nombre"));
                fila.add(rs.getString("ciudad"));
                fila.add(rs.getString("pais"));
                model.addRow(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar aeropuertos: " + e.getMessage());
        }
    }

    private void agregarAeropuerto() {
        String nombre = txtNombre.getText().trim();
        String ciudad = txtCiudad.getText().trim();
        String pais = txtPais.getText().trim();

        if (nombre.isEmpty() || ciudad.isEmpty() || pais.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
            return;
        }

        String sql = "INSERT INTO aeropuerto (nombre, ciudad, pais) VALUES (?, ?, ?)";

        try (Connection conexion = PruebaConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, ciudad);
            ps.setString(3, pais);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Aeropuerto agregado con éxito.");
            limpiarCampos();
            cargarDatos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar aeropuerto: " + e.getMessage());
        }
    }

    private void actualizarAeropuerto() {
        int fila = table.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un aeropuerto para actualizar.");
            return;
        }

        String nombre = txtNombre.getText().trim();
        String ciudad = txtCiudad.getText().trim();
        String pais = txtPais.getText().trim();

        if (nombre.isEmpty() || ciudad.isEmpty() || pais.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
            return;
        }

        int id = (int) model.getValueAt(fila, 0);
        String sql = "UPDATE aeropuerto SET nombre = ?, ciudad = ?, pais = ? WHERE id = ?";

        try (Connection conexion = PruebaConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, ciudad);
            ps.setString(3, pais);
            ps.setInt(4, id);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Aeropuerto actualizado con éxito.");
            limpiarCampos();
            cargarDatos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar aeropuerto: " + e.getMessage());
        }
    }

    private void eliminarAeropuerto() {
        int fila = table.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un aeropuerto para eliminar.");
            return;
        }

        int id = (int) model.getValueAt(fila, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea eliminar este aeropuerto?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM aeropuerto WHERE id = ?";

        try (Connection conexion = PruebaConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Aeropuerto eliminado con éxito.");
            limpiarCampos();
            cargarDatos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar aeropuerto: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtCiudad.setText("");
        txtPais.setText("");
        table.clearSelection();
    }
}
