package com.mycompany.pruebaconnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class CatedraticoWindow extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtNombre, txtDireccion;
    private JButton btnAgregar, btnActualizar, btnEliminar, btnVolver;

    public CatedraticoWindow() {
        setTitle("Catedráticos");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        model = new DefaultTableModel();
        table = new JTable(model);

        // Columnas
        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Dirección");

        cargarDatos();

        // Panel para inputs y botones CRUD
        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Formulario"));

        panelFormulario.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("Dirección:"));
        txtDireccion = new JTextField();
        panelFormulario.add(txtDireccion);

        btnAgregar = new JButton("Agregar");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnVolver = new JButton("Volver al menú");

        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.add(btnAgregar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnVolver);

        // Layout general
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(panelFormulario, BorderLayout.NORTH);
        add(panelBotones, BorderLayout.SOUTH);

        // Eventos botones
        btnAgregar.addActionListener(e -> agregarCatedratico());
        btnActualizar.addActionListener(e -> actualizarCatedratico());
        btnEliminar.addActionListener(e -> eliminarCatedratico());
        btnVolver.addActionListener(e -> {
            new HomeMenu().setVisible(true);
            this.dispose();
        });

        // Al seleccionar fila, cargar datos en inputs para editar
        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int fila = table.getSelectedRow();
                txtNombre.setText(model.getValueAt(fila, 1).toString());
                txtDireccion.setText(model.getValueAt(fila, 2).toString());
            }
        });
    }

    private void cargarDatos() {
        try (Connection conexion = PruebaConnection.getConnection()) {
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM catedratico");

            model.setRowCount(0);

            while (rs.next()) {
                Vector<Object> fila = new Vector<>();
                fila.add(rs.getInt("id"));
                fila.add(rs.getString("nombre"));
                fila.add(rs.getString("direccion"));
                model.addRow(fila);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
        }
    }

    private void agregarCatedratico() {
        String nombre = txtNombre.getText().trim();
        String direccion = txtDireccion.getText().trim();

        if (nombre.isEmpty() || direccion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos.");
            return;
        }

        String sql = "INSERT INTO catedratico (nombre, direccion) VALUES (?, ?)";

        try (Connection conexion = PruebaConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, direccion);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Catedrático agregado con éxito.");
            limpiarCampos();
            cargarDatos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar catedrático: " + e.getMessage());
        }
    }

    private void actualizarCatedratico() {
        int fila = table.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un catedrático para actualizar.");
            return;
        }

        int id = (int) model.getValueAt(fila, 0);
        String nombre = txtNombre.getText().trim();
        String direccion = txtDireccion.getText().trim();

        if (nombre.isEmpty() || direccion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos.");
            return;
        }

        String sql = "UPDATE catedratico SET nombre = ?, direccion = ? WHERE id = ?";

        try (Connection conexion = PruebaConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, direccion);
            ps.setInt(3, id);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Catedrático actualizado con éxito.");
            limpiarCampos();
            cargarDatos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar catedrático: " + e.getMessage());
        }
    }

    private void eliminarCatedratico() {
        int fila = table.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un catedrático para eliminar.");
            return;
        }

        int id = (int) model.getValueAt(fila, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar este catedrático?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM catedratico WHERE id = ?";

        try (Connection conexion = PruebaConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Catedrático eliminado con éxito.");
            limpiarCampos();
            cargarDatos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar catedrático: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtNombre.setText("");
        txtDireccion.setText("");
        table.clearSelection();
    }
}
