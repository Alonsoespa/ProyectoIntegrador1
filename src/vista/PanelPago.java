package vista;

import dao.ClienteDAO;
import dao.PagoDAO;
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
import modelo.Pago;
import util.Validaciones;

public class PanelPago extends JPanel implements PanelActualizable {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final PagoDAO pagoDAO = new PagoDAO();

    private JTextField campoCliente;
    private final DefaultListModel<String> modeloSugerencias = new DefaultListModel<>();
    private JList<String> listaSugerencias;

    private JTextField campoMonto;
    private JTextField campoFecha;

    private final DefaultListModel<String> modeloPagos = new DefaultListModel<>();
    private JList<String> listaPagos;
    private List<Pago> pagosCache;

    private Integer idSeleccionado = null;
    private final String fechaHoy = LocalDate.now().toString();

    public PanelPago() {
        setLayout(new BorderLayout());
        setBackground(Estilos.FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Gestión de Pagos");
        titulo.setFont(Estilos.TITULO);
        add(titulo, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(15, 0));
        cuerpo.setBackground(Estilos.FONDO);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        cuerpo.add(crearFormulario(), BorderLayout.WEST);
        cuerpo.add(crearListaPagos(), BorderLayout.CENTER);
        add(cuerpo, BorderLayout.CENTER);
    }

    private JPanel crearFormulario() {
        JPanel caja = new JPanel();
        caja.setLayout(new javax.swing.BoxLayout(caja, javax.swing.BoxLayout.Y_AXIS));
        caja.setBackground(Estilos.GRIS_FORMULARIO);
        caja.setPreferredSize(new Dimension(400, 460));
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

        campoMonto = crearCampo(caja, "Monto");
        campoFecha = crearCampo(caja, "Fecha (AAAA-MM-DD)");
        campoFecha.setText(fechaHoy);

        caja.add(javax.swing.Box.createRigidArea(new Dimension(0, 10)));

        JPanel botones = new JPanel(new GridLayout(2, 2, 6, 6));
        botones.setBackground(Estilos.GRIS_FORMULARIO);
        botones.setMaximumSize(new Dimension(320, 90));

        JButton btnGuardar = crearBoton("Guardar nuevo", Estilos.VERDE_BOTON);
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

    private JPanel crearListaPagos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Estilos.GRIS_FORMULARIO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lbl = new JLabel("Pagos registrados (selecciona uno para editar/eliminar)", JLabel.CENTER);
        lbl.setFont(Estilos.NEGRITA);
        panel.add(lbl, BorderLayout.NORTH);

        listaPagos = new JList<>(modeloPagos);
        listaPagos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarPago();
            }
        });
        panel.add(new JScrollPane(listaPagos), BorderLayout.CENTER);
        return panel;
    }

    private void seleccionarPago() {
        int idx = listaPagos.getSelectedIndex();
        if (idx < 0 || pagosCache == null || idx >= pagosCache.size()) {
            return;
        }
        Pago p = pagosCache.get(idx);
        idSeleccionado = p.getId();
        campoCliente.setText(p.getCliente());
        campoMonto.setText(String.valueOf(p.getMonto()));
        campoFecha.setText(p.getFecha());
    }

    private void limpiarFormulario() {
        idSeleccionado = null;
        campoCliente.setText("");
        modeloSugerencias.clear();
        campoMonto.setText("");
        campoFecha.setText(fechaHoy);
        listaPagos.clearSelection();
    }

    private Pago validar() {
        String cliente = campoCliente.getText().trim();
        String monto = campoMonto.getText().trim();
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
        if (monto.isEmpty() || !Validaciones.esNumero(monto)) {
            JOptionPane.showMessageDialog(this, "El monto debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (fecha.isEmpty() || !Validaciones.fechaValida(fecha)) {
            JOptionPane.showMessageDialog(this, "La fecha debe tener el formato AAAA-MM-DD y ser una fecha válida", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return new Pago(cliente, Double.parseDouble(monto), fecha);
    }

    private void guardar() {
        Pago p = validar();
        if (p == null) {
            return;
        }
        try {
            pagoDAO.insertar(p);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar el pago:\n" + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Pago guardado ✅");
        limpiarFormulario();
        actualizarDatos();
    }

    private void actualizar() {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un pago de la lista para editar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Pago p = validar();
        if (p == null) {
            return;
        }
        p.setId(idSeleccionado);
        try {
            pagoDAO.actualizar(p);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar el pago:\n" + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Pago actualizado ✅");
        limpiarFormulario();
        actualizarDatos();
    }

    private void eliminar() {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un pago de la lista para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this, "¿Seguro que quieres eliminar este pago?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            pagoDAO.eliminar(idSeleccionado);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el pago:\n" + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Pago eliminado ✅");
        limpiarFormulario();
        actualizarDatos();
    }

    @Override
    public void actualizarDatos() {
        try {
            pagosCache = pagoDAO.listarTodos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar la lista de pagos:\n" + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        modeloPagos.clear();
        for (Pago p : pagosCache) {
            modeloPagos.addElement(p.getFecha() + " | " + p.getCliente() + " | $"
                    + String.format("%.2f", p.getMonto()));
        }
    }
}
