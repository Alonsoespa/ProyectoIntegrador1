package vista;

import dao.ClienteDAO;
import dao.VentaDAO;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import modelo.Venta;
import util.Validaciones;

public class PanelVenta extends JPanel implements PanelActualizable {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final VentaDAO ventaDAO = new VentaDAO();

    private JTextField campoCliente;
    private final DefaultListModel<String> modeloSugerencias = new DefaultListModel<>();
    private JList<String> listaSugerencias;

    private JTextField campoProducto;
    private JTextField campoTotal;
    private JTextField campoFecha;

    private final DefaultListModel<String> modeloVentas = new DefaultListModel<>();
    private JList<String> listaVentas;
    private List<Venta> ventasCache;

    private Integer idSeleccionado = null;
    private final String fechaHoy = LocalDate.now().toString();

    public PanelVenta() {
        setLayout(new BorderLayout());
        setBackground(Estilos.FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Gestión de Ventas");
        titulo.setFont(Estilos.TITULO);
        add(titulo, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(15, 0));
        cuerpo.setBackground(Estilos.FONDO);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        cuerpo.add(crearFormulario(), BorderLayout.WEST);
        cuerpo.add(crearListaVentas(), BorderLayout.CENTER);
        add(cuerpo, BorderLayout.CENTER);
    }

    private JPanel crearFormulario() {
        JPanel caja = new JPanel();
        caja.setLayout(new javax.swing.BoxLayout(caja, javax.swing.BoxLayout.Y_AXIS));
        caja.setBackground(Estilos.GRIS_FORMULARIO);
        caja.setPreferredSize(new Dimension(400, 500));
        caja.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblCliente = new JLabel("Cliente");
        lblCliente.setFont(Estilos.NEGRITA);
        lblCliente.setAlignmentX(JLabel.LEFT_ALIGNMENT);
        caja.add(lblCliente);

        campoCliente = new JTextField();
        campoCliente.setMaximumSize(new Dimension(360, 32));
        campoCliente.setAlignmentX(JTextField.LEFT_ALIGNMENT);
        caja.add(campoCliente);

        listaSugerencias = new JList<>(modeloSugerencias);
        listaSugerencias.setVisibleRowCount(4);
        JScrollPane scrollSugerencias = new JScrollPane(listaSugerencias);
        scrollSugerencias.setMaximumSize(new Dimension(360, 80));
        scrollSugerencias.setAlignmentX(JScrollPane.LEFT_ALIGNMENT);
        caja.add(scrollSugerencias);
        caja.add(javax.swing.Box.createRigidArea(new Dimension(0, 10)));

        campoCliente.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                buscarCliente();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                buscarCliente();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                buscarCliente();
            }
        });
        listaSugerencias.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && listaSugerencias.getSelectedValue() != null
                    && !listaSugerencias.getSelectedValue().equals("No hay cliente")) {
                campoCliente.setText(listaSugerencias.getSelectedValue());
            }
        });

        campoProducto = crearCampo(caja, "Producto");
        campoTotal = crearCampo(caja, "Total");
        campoFecha = crearCampo(caja, "Fecha (AAAA-MM-DD)");
        campoFecha.setText(fechaHoy);

        caja.add(javax.swing.Box.createRigidArea(new Dimension(0, 10)));

        JPanel botones = new JPanel(new GridLayout(2, 2, 6, 6));
        botones.setBackground(Estilos.GRIS_FORMULARIO);
        botones.setMaximumSize(new Dimension(320, 90));

        JButton btnGuardar = crearBoton("Guardar nueva", Estilos.VERDE_BOTON);
        JButton btnActualizar = crearBoton("Actualizar", Estilos.VERDE_BOTON);
        JButton btnEliminar = crearBoton("Eliminar", Estilos.ROJO_BOTON);
        JButton btnLimpiar = crearBoton("Limpiar", Estilos.GRIS_BOTON);

        btnGuardar.addActionListener(e -> guardar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        botones.add(btnGuardar);
        botones.add(btnActualizar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);
        caja.add(botones);

        return caja;
    }

    private void buscarCliente() {
        String texto = campoCliente.getText().trim();
        modeloSugerencias.clear();
        try {
            List<String> datos = clienteDAO.buscarNombresLike(texto);
            if (datos.isEmpty()) {
                modeloSugerencias.addElement("No hay cliente");
            } else {
                for (String n : datos) {
                    modeloSugerencias.addElement(n);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo buscar el cliente:\n" + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JTextField crearCampo(JPanel contenedor, String etiqueta) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(Estilos.NEGRITA);
        lbl.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        JTextField campo = new JTextField();
        campo.setMaximumSize(new Dimension(360, 32));
        campo.setAlignmentX(JTextField.LEFT_ALIGNMENT);
        campo.setBackground(Estilos.GRIS_INPUT);

        contenedor.add(lbl);
        contenedor.add(campo);
        contenedor.add(javax.swing.Box.createRigidArea(new Dimension(0, 10)));
        return campo;
    }

    private JButton crearBoton(String texto, java.awt.Color color) {
        JButton boton = new JButton(texto);
        boton.setBackground(color);
        boton.setFocusPainted(false);
        return boton;
    }

    private JPanel crearListaVentas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Estilos.GRIS_FORMULARIO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lbl = new JLabel("Ventas registradas (selecciona una para editar/eliminar)", JLabel.CENTER);
        lbl.setFont(Estilos.NEGRITA);
        panel.add(lbl, BorderLayout.NORTH);

        listaVentas = new JList<>(modeloVentas);
        listaVentas.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarVenta();
            }
        });
        panel.add(new JScrollPane(listaVentas), BorderLayout.CENTER);
        return panel;
    }

    private void seleccionarVenta() {
        int idx = listaVentas.getSelectedIndex();
        if (idx < 0 || ventasCache == null || idx >= ventasCache.size()) {
            return;
        }
        Venta v = ventasCache.get(idx);
        idSeleccionado = v.getId();
        campoCliente.setText(v.getCliente());
        campoProducto.setText(v.getProducto());
        campoTotal.setText(String.valueOf(v.getTotal()));
        campoFecha.setText(v.getFecha());
    }

    private void limpiarFormulario() {
        idSeleccionado = null;
        campoCliente.setText("");
        modeloSugerencias.clear();
        campoProducto.setText("");
        campoTotal.setText("");
        campoFecha.setText(fechaHoy);
        listaVentas.clearSelection();
    }

    private Venta validar() {
        String cliente = campoCliente.getText().trim();
        String producto = campoProducto.getText().trim();
        String total = campoTotal.getText().trim();
        String fecha = campoFecha.getText().trim();

        if (cliente.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        try {
            if (!clienteDAO.existeNombre(cliente)) {
                JOptionPane.showMessageDialog(this, "Cliente no existe", "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo verificar el cliente:\n" + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (producto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa el producto", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (total.isEmpty() || !Validaciones.esNumero(total)) {
            JOptionPane.showMessageDialog(this, "El total debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (fecha.isEmpty() || !Validaciones.fechaValida(fecha)) {
            JOptionPane.showMessageDialog(this, "La fecha debe tener el formato AAAA-MM-DD y ser una fecha válida", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return new Venta(cliente, producto, Double.parseDouble(total), fecha);
    }

    private void guardar() {
        Venta v = validar();
        if (v == null) {
            return;
        }
        try {
            ventaDAO.insertar(v);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar la venta:\n" + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Venta guardada ✅");
        limpiarFormulario();
        actualizarDatos();
    }

    private void actualizar() {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una venta de la lista para editar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Venta v = validar();
        if (v == null) {
            return;
        }
        v.setId(idSeleccionado);
        try {
            ventaDAO.actualizar(v);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar la venta:\n" + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Venta actualizada ✅");
        limpiarFormulario();
        actualizarDatos();
    }

    private void eliminar() {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona una venta de la lista para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this, "¿Seguro que quieres eliminar esta venta?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            ventaDAO.eliminar(idSeleccionado);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar la venta:\n" + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Venta eliminada ✅");
        limpiarFormulario();
        actualizarDatos();
    }

    @Override
    public void actualizarDatos() {
        try {
            ventasCache = ventaDAO.listarTodas();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar la lista de ventas:\n" + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        modeloVentas.clear();
        for (Venta v : ventasCache) {
            modeloVentas.addElement(v.getFecha() + " | " + v.getCliente() + " | " + v.getProducto()
                    + " | $" + String.format("%.2f", v.getTotal()));
        }
    }
}
