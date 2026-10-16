package vista;

import dao.ClienteDAO;
import dao.PagoDAO;
import dao.VentaDAO;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.DefaultListModel;
import modelo.Cliente;
import util.Validaciones;

public class PanelCliente extends JPanel implements PanelActualizable {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final VentaDAO ventaDAO = new VentaDAO();
    private final PagoDAO pagoDAO = new PagoDAO();

    private JTextField campoNombre;
    private JTextField campoTelefono;
    private JTextField campoDireccion;

    private final DefaultListModel<String> modeloLista = new DefaultListModel<>();
    private JList<String> listaClientes;
    private List<Cliente> clientesCache;

    private Integer idSeleccionado = null;

    public PanelCliente() {
        setLayout(new BorderLayout());
        setBackground(Estilos.FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Gestión de Clientes");
        titulo.setFont(Estilos.TITULO);
        add(titulo, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(15, 0));
        cuerpo.setBackground(Estilos.FONDO);
        cuerpo.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        cuerpo.add(crearFormulario(), BorderLayout.WEST);
        cuerpo.add(crearListaClientes(), BorderLayout.CENTER);

        add(cuerpo, BorderLayout.CENTER);
    }

    private JPanel crearFormulario() {
        JPanel caja = new JPanel();
        caja.setLayout(new javax.swing.BoxLayout(caja, javax.swing.BoxLayout.Y_AXIS));
        caja.setBackground(Estilos.GRIS_FORMULARIO);
        caja.setPreferredSize(new Dimension(380, 460));
        caja.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lbl = new JLabel("Datos del cliente");
        lbl.setFont(Estilos.SUBTITULO);
        lbl.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        caja.add(lbl);
        caja.add(javax.swing.Box.createRigidArea(new Dimension(0, 10)));

        campoNombre = crearCampo(caja, "Nombre completo");
        campoTelefono = crearCampo(caja, "Teléfono (10 dígitos)");
        campoDireccion = crearCampo(caja, "Dirección");

        caja.add(javax.swing.Box.createRigidArea(new Dimension(0, 15)));

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

    private JTextField crearCampo(JPanel contenedor, String etiqueta) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(Estilos.NEGRITA);
        lbl.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        JTextField campo = new JTextField();
        campo.setMaximumSize(new Dimension(340, 32));
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

    private JPanel crearListaClientes() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Estilos.GRIS_FORMULARIO);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lbl = new JLabel("Clientes registrados (selecciona uno para editar/eliminar)", JLabel.CENTER);
        lbl.setFont(Estilos.NEGRITA);
        panel.add(lbl, BorderLayout.NORTH);

        listaClientes = new JList<>(modeloLista);
        listaClientes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                seleccionarCliente();
            }
        });
        panel.add(new JScrollPane(listaClientes), BorderLayout.CENTER);
        return panel;
    }

    private void seleccionarCliente() {
        int idx = listaClientes.getSelectedIndex();
        if (idx < 0 || clientesCache == null || idx >= clientesCache.size()) {
            return;
        }
        Cliente c = clientesCache.get(idx);
        idSeleccionado = c.getId();
        campoNombre.setText(c.getNombre());
        campoTelefono.setText(c.getTelefono());
        campoDireccion.setText(c.getDireccion());
    }

    private void limpiarFormulario() {
        idSeleccionado = null;
        campoNombre.setText("");
        campoTelefono.setText("");
        campoDireccion.setText("");
        listaClientes.clearSelection();
    }

    /** Devuelve los datos validados, o null si hay un error (y ya mostró el mensaje). */
    private Cliente validarCampos() {
        String nombre = campoNombre.getText().trim();
        String telefono = campoTelefono.getText().trim();
        String direccion = campoDireccion.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa el nombre del cliente", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (!Validaciones.nombreValido(nombre)) {
            JOptionPane.showMessageDialog(this, "El nombre solo puede contener letras y espacios", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa el teléfono", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (!Validaciones.telefonoValido(telefono)) {
            JOptionPane.showMessageDialog(this, "El teléfono debe tener exactamente 10 dígitos numéricos", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        if (direccion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa la dirección", "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        return new Cliente(nombre, telefono, direccion);
    }

    private void guardar() {
        Cliente c = validarCampos();
        if (c == null) {
            return;
        }
        try {
            if (clienteDAO.existeNombre(c.getNombre())) {
                JOptionPane.showMessageDialog(this, "El cliente ya existe", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            clienteDAO.insertar(c);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar el cliente:\n" + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Cliente registrado correctamente ✅");
        limpiarFormulario();
        actualizarDatos();
    }

    private void actualizar() {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente de la lista para editar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Cliente c = validarCampos();
        if (c == null) {
            return;
        }
        try {
            if (clienteDAO.existeOtroConNombre(c.getNombre(), idSeleccionado)) {
                JOptionPane.showMessageDialog(this, "Ya existe otro cliente con ese nombre", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Cliente actual = clientesCache.stream()
                    .filter(cl -> cl.getId() == idSeleccionado)
                    .findFirst().orElse(null);
            if (actual == null) {
                JOptionPane.showMessageDialog(this, "El cliente ya no existe", "Error", JOptionPane.ERROR_MESSAGE);
                limpiarFormulario();
                actualizarDatos();
                return;
            }
            String nombreAnterior = actual.getNombre();

            c.setId(idSeleccionado);
            clienteDAO.actualizar(c);

            // Si el nombre cambió, mantener sincronizadas ventas y pagos ya registrados
            if (!nombreAnterior.equals(c.getNombre())) {
                ventaDAO.renombrarCliente(nombreAnterior, c.getNombre());
                pagoDAO.renombrarCliente(nombreAnterior, c.getNombre());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo actualizar el cliente:\n" + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Cliente actualizado correctamente ✅");
        limpiarFormulario();
        actualizarDatos();
    }

    private void eliminar() {
        if (idSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente de la lista para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String nombreCliente = campoNombre.getText().trim();

        try {
            int nVentas = ventaDAO.contarPorCliente(nombreCliente);
            int nPagos = pagoDAO.contarPorCliente(nombreCliente);

            StringBuilder aviso = new StringBuilder("¿Seguro que quieres eliminar a " + nombreCliente + "?");
            if (nVentas > 0 || nPagos > 0) {
                aviso.append("\n\nEste cliente tiene ").append(nVentas).append(" venta(s) y ")
                        .append(nPagos).append(" pago(s) registrados. También se eliminarán junto con el cliente.");
            }

            int opcion = JOptionPane.showConfirmDialog(this, aviso.toString(), "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);
            if (opcion != JOptionPane.YES_OPTION) {
                return;
            }

            clienteDAO.eliminar(idSeleccionado);
            ventaDAO.eliminarPorCliente(nombreCliente);
            pagoDAO.eliminarPorCliente(nombreCliente);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar el cliente:\n" + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente ✅");
        limpiarFormulario();
        actualizarDatos();
    }

    @Override
    public void actualizarDatos() {
        try {
            clientesCache = clienteDAO.listarTodos();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar la lista de clientes:\n" + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
            return;
        }
        modeloLista.clear();
        for (Cliente c : clientesCache) {
            modeloLista.addElement(c.getNombre() + "  (" + c.getTelefono() + ")");
        }
    }
}
