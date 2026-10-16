package vista;

import dao.ClienteDAO;
import dao.PagoDAO;
import dao.VentaDAO;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import modelo.Movimiento;

public class PanelConsulta extends JPanel implements PanelActualizable {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final VentaDAO ventaDAO = new VentaDAO();
    private final PagoDAO pagoDAO = new PagoDAO();

    private JTextField campoBusqueda;
    private final DefaultListModel<String> modeloLista = new DefaultListModel<>();
    private JList<String> listaClientes;
    private JLabel etiquetaDeuda;
    private DefaultTableModel modeloHistorial;

    public PanelConsulta() {
        setLayout(new BorderLayout());
        setBackground(Estilos.FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel norte = new JPanel();
        norte.setLayout(new javax.swing.BoxLayout(norte, javax.swing.BoxLayout.Y_AXIS));
        norte.setBackground(Estilos.FONDO);

        JLabel titulo = new JLabel("Consultar Cliente");
        titulo.setFont(Estilos.TITULO);
        titulo.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        norte.add(titulo);
        norte.add(javax.swing.Box.createRigidArea(new Dimension(0, 15)));

        campoBusqueda = new JTextField();
        campoBusqueda.setMaximumSize(new Dimension(400, 32));
        campoBusqueda.setAlignmentX(JTextField.CENTER_ALIGNMENT);
        norte.add(campoBusqueda);
        norte.add(javax.swing.Box.createRigidArea(new Dimension(0, 10)));

        listaClientes = new JList<>(modeloLista);
        listaClientes.setVisibleRowCount(5);
        JScrollPane scrollLista = new JScrollPane(listaClientes);
        scrollLista.setMaximumSize(new Dimension(400, 110));
        scrollLista.setAlignmentX(JScrollPane.CENTER_ALIGNMENT);
        norte.add(scrollLista);
        norte.add(javax.swing.Box.createRigidArea(new Dimension(0, 10)));

        etiquetaDeuda = new JLabel(" ");
        etiquetaDeuda.setFont(Estilos.SUBTITULO);
        etiquetaDeuda.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        norte.add(etiquetaDeuda);
        norte.add(javax.swing.Box.createRigidArea(new Dimension(0, 10)));

        JLabel tituloHistorial = new JLabel("Historial completo de movimientos");
        tituloHistorial.setFont(Estilos.SUBTITULO);
        tituloHistorial.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        norte.add(tituloHistorial);

        add(norte, BorderLayout.NORTH);

        modeloHistorial = new DefaultTableModel(new Object[]{"Fecha", "Tipo", "Detalle", "Monto"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tablaHistorial = new JTable(modeloHistorial);
        tablaHistorial.setRowHeight(24);
        add(new JScrollPane(tablaHistorial), BorderLayout.CENTER);

        campoBusqueda.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                buscar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                buscar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                buscar();
            }
        });

        listaClientes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDeudaYHistorial();
            }
        });
    }

    private void buscar() {
        String texto = campoBusqueda.getText().trim();
        modeloLista.clear();
        try {
            List<String> datos = clienteDAO.buscarNombresLike(texto);
            if (datos.isEmpty()) {
                modeloLista.addElement("No hay cliente");
            } else {
                for (String n : datos) {
                    modeloLista.addElement(n);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo buscar el cliente:\n" + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarDeudaYHistorial() {
        String cliente = listaClientes.getSelectedValue();
        if (cliente == null || cliente.equals("No hay cliente")) {
            return;
        }
        try {
            double totalVentas = ventaDAO.sumaPorCliente(cliente);
            double totalPagos = pagoDAO.sumaPorCliente(cliente);
            etiquetaDeuda.setText(String.format("💰 Deuda: $%.2f", totalVentas - totalPagos));

            List<Movimiento> movimientos = new ArrayList<>();
            for (modelo.Venta v : ventaDAO.listarPorCliente(cliente)) {
                movimientos.add(new Movimiento(cliente, v.getFecha(), "Venta", v.getProducto(), v.getTotal()));
            }
            for (modelo.Pago p : pagoDAO.listarPorCliente(cliente)) {
                movimientos.add(new Movimiento(cliente, p.getFecha(), "Pago", "", p.getMonto()));
            }
            movimientos.sort(Comparator.comparing(Movimiento::getFecha).reversed());

            modeloHistorial.setRowCount(0);
            if (movimientos.isEmpty()) {
                modeloHistorial.addRow(new Object[]{"", "Sin movimientos registrados", "", ""});
            } else {
                for (Movimiento m : movimientos) {
                    modeloHistorial.addRow(new Object[]{
                        m.getFecha(), m.getTipo(), m.getDetalle(), String.format("$%.2f", m.getMonto())
                    });
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "No se pudo calcular la información del cliente:\n" + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Permite que la barra de búsqueda superior de la ventana principal dispare una consulta directa. */
    public void buscarDesdeFuera(String textoCliente) {
        campoBusqueda.setText(textoCliente);
        buscar();
        for (int i = 0; i < modeloLista.size(); i++) {
            if (modeloLista.get(i).equalsIgnoreCase(textoCliente)) {
                listaClientes.setSelectedIndex(i);
                return;
            }
        }
        if (!modeloLista.isEmpty()) {
            listaClientes.setSelectedIndex(0);
        }
    }

    @Override
    public void actualizarDatos() {
        // Esta pantalla se actualiza sola cuando el usuario escribe en el buscador.
    }
}
