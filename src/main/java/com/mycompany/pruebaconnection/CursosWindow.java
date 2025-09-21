package com.mycompany.pruebaconnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class CursosWindow extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtAnio, txtSemestre, txtNombreCurso;
    private JComboBox<String> comboCatedratico;
    private JButton btnAgregar, btnActualizar, btnEliminar, btnVolver;

    public CursosWindow() {
        setTitle("Cursos Impartidos");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        model = new DefaultTableModel();
        table = new JTable(model);

        model.addColumn("ID");
        model.addColumn("Año");
        model.addColumn("Semestre");
        model.addColumn("Nombre Curso");
        model.addColumn("ID Catedrático");
        model.addColumn("Nombre Catedrático");

        cargarDatos();

        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 10, 10));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Formulario"));

        panelFormulario.add(new JLabel("Año:"));
        txtAnio = new JTextField();
        panelFormulario.add(txtAnio);

        panelFormulario.add(new JLabel("Semestre:"));
        txtSemestre = new JTextField();
        panelFormulario.add(txtSemestre);

        panelFormulario.add(new JLabel("Nombre Curso:"));
        txtNombreCurso = new JTextField();
        panelFormulario.add(txtNombreCurso);

        panelFormulario.add(new JLabel("Catedrático:"));
        comboCatedratico = new JComboBox<>();
        cargarCatedraticos();
        panelFormulario.add(comboCatedratico);

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

        btnAgregar.addActionListener(e -> agregarCurso());
        btnActualizar.addActionListener(e -> actualizarCurso());
        btnEliminar.addActionListener(e -> eliminarCurso());
        btnVolver.addActionListener(e -> {
            new HomeMenu().setVisible(true);
            this.dispose();
        });

        table.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int fila = table.getSelectedRow();
                txtAnio.setText(model.getValueAt(fila, 1).toString());
                txtSemestre.setText(model.getValueAt(fila, 2).toString());
                txtNombreCurso.setText(model.getValueAt(fila, 3).toString());
                int idCat = (int) model.getValueAt(fila, 4);
                seleccionarCatedraticoEnCombo(idCat);
            }
        });
    }

    private void cargarDatos() {
        String sql = "SELECT c.id, c.anio, c.semestre, c.nombre_curso, c.id_catedratico, cat.nombre " +
                     "FROM cursos_impartidos c LEFT JOIN catedratico cat ON c.id_catedratico = cat.id";
        try (Connection conexion = PruebaConnection.getConnection();
             Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            model.setRowCount(0);
            while (rs.next()) {
                Vector<Object> fila = new Vector<>();
                fila.add(rs.getInt("id"));
                fila.add(rs.getInt("anio"));
                fila.add(rs.getString("semestre"));
                fila.add(rs.getString("nombre_curso"));
                fila.add(rs.getInt("id_catedratico"));
                fila.add(rs.getString("nombre"));
                model.addRow(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
        }
    }

    private void cargarCatedraticos() {
        comboCatedratico.removeAllItems();
        try (Connection conexion = PruebaConnection.getConnection();
             Statement stmt = conexion.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nombre FROM catedratico")) {

            while (rs.next()) {
                comboCatedratico.addItem(rs.getInt("id") + " - " + rs.getString("nombre"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar catedráticos: " + e.getMessage());
        }
    }

    private void seleccionarCatedraticoEnCombo(int id) {
        for (int i = 0; i < comboCatedratico.getItemCount(); i++) {
            String item = comboCatedratico.getItemAt(i);
            if (item.startsWith(id + " -")) {
                comboCatedratico.setSelectedIndex(i);
                break;
            }
        }
    }

    private int obtenerIdCatedraticoSeleccionado() {
        String selected = (String) comboCatedratico.getSelectedItem();
        if (selected == null) return -1;
        return Integer.parseInt(selected.split(" - ")[0]);
    }

    private void agregarCurso() {
        String anioStr = txtAnio.getText().trim();
        String semestre = txtSemestre.getText().trim();
        String nombreCurso = txtNombreCurso.getText().trim();
        int idCat = obtenerIdCatedraticoSeleccionado();

        if (anioStr.isEmpty() || semestre.isEmpty() || nombreCurso.isEmpty() || idCat == -1) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
            return;
        }

        try {
            int anio = Integer.parseInt(anioStr);
            String sql = "INSERT INTO cursos_impartidos (anio, semestre, nombre_curso, id_catedratico) VALUES (?, ?, ?, ?)";
            try (Connection conexion = PruebaConnection.getConnection();
                 PreparedStatement ps = conexion.prepareStatement(sql)) {

                ps.setInt(1, anio);
                ps.setString(2, semestre);
                ps.setString(3, nombreCurso);
                ps.setInt(4, idCat);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Curso agregado con éxito.");
                limpiarCampos();
                cargarDatos();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Año debe ser un número válido.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al agregar curso: " + e.getMessage());
        }
    }

    private void actualizarCurso() {
        int fila = table.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un curso para actualizar.");
            return;
        }

        String anioStr = txtAnio.getText().trim();
        String semestre = txtSemestre.getText().trim();
        String nombreCurso = txtNombreCurso.getText().trim();
        int idCat = obtenerIdCatedraticoSeleccionado();

        if (anioStr.isEmpty() || semestre.isEmpty() || nombreCurso.isEmpty() || idCat == -1) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
            return;
        }

        int id = (int) model.getValueAt(fila, 0);

        try {
            int anio = Integer.parseInt(anioStr);
            String sql = "UPDATE cursos_impartidos SET anio = ?, semestre = ?, nombre_curso = ?, id_catedratico = ? WHERE id = ?";
            try (Connection conexion = PruebaConnection.getConnection();
                 PreparedStatement ps = conexion.prepareStatement(sql)) {

                ps.setInt(1, anio);
                ps.setString(2, semestre);
                ps.setString(3, nombreCurso);
                ps.setInt(4, idCat);
                ps.setInt(5, id);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Curso actualizado con éxito.");
                limpiarCampos();
                cargarDatos();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Año debe ser un número válido.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar curso: " + e.getMessage());
        }
    }

    private void eliminarCurso() {
        int fila = table.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un curso para eliminar.");
            return;
        }

        int id = (int) model.getValueAt(fila, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro que desea eliminar este curso?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM cursos_impartidos WHERE id = ?";

        try (Connection conexion = PruebaConnection.getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Curso eliminado con éxito.");
            limpiarCampos();
            cargarDatos();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al eliminar curso: " + e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtAnio.setText("");
        txtSemestre.setText("");
        txtNombreCurso.setText("");
        comboCatedratico.setSelectedIndex(-1);
        table.clearSelection();
    }
}
