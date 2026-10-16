package vista;

import dao.ClienteDAO;
import dao.PagoDAO;
import dao.VentaDAO;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import modelo.Movimiento;

public class PanelInicio extends JPanel implements PanelActualizable {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final VentaDAO ventaDAO = new VentaDAO();
    private final PagoDAO pagoDAO = new PagoDAO();

    private JLabel valorDeuda;
    private JLabel valorPagosMes;
    private JLabel valorClientes;
    private DefaultTableModel modeloMovimientos;
    private DefaultTableModel modeloTopClientes;

    public PanelInicio() {
        setLayout(new BorderLayout());
        setBackground(Estilos.FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel encabezado = new JPanel();
        encabezado.setLayout(new javax.swing.BoxLayout(encabezado, javax.swing.BoxLayout.Y_AXIS));
        encabezado.setBackground(Estilos.FONDO);
        JLabel titulo = new JLabel("Panel de Control");
        titulo.setFont(Estilos.TITULO);
        JLabel subtitulo = new JLabel("Resumen General");
        subtitulo.setFont(Estilos.NORMAL);
        encabezado.add(titulo);
        encabezado.add(subtitulo);

        JPanel norte = new JPanel(new BorderLayout());
        norte.setBackground(Estilos.FONDO);
        norte.add(encabezado, BorderLayout.NORTH);
        norte.add(crearTarjetas(), BorderLayout.CENTER);
        add(norte, BorderLayout.NORTH);

        add(crearSeccionInferior(), BorderLayout.CENTER);
    }

    private JPanel crearTarjetas() {
        JPanel contenedor = new JPanel(new GridLayout(1, 3, 15, 0));
        contenedor.setBackground(Estilos.FONDO);
        contenedor.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        contenedor.setPreferredSize(new Dimension(700, 90));

        valorDeuda = new JLabel("$0.00");
        valorPagosMes = new JLabel("$0.00");
        valorClientes = new JLabel("0");

        contenedor.add(crearTarjeta("Total deuda acumulada", valorDeuda));
        contenedor.add(crearTarjeta("Pagos recibidos (Mes)", valorPagosMes));
        contenedor.add(crearTarjeta("Clientes con deuda", valorClientes));

        return contenedor;
    }

    private JPanel crearTarjeta(String titulo, JLabel valor) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new javax.swing.BoxLayout(tarjeta, javax.swing.BoxLayout.Y_AXIS));
        tarjeta.setBackground(Estilos.GRIS_TARJETA);
        tarjeta.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(Estilos.NEGRITA);

        valor.setFont(Estilos.SUBTITULO);

        tarjeta.add(lblTitulo);
        tarjeta.add(valor);
        return tarjeta;
    }

    private JPanel crearSeccionInferior() {
        JPanel abajo = new JPanel(new BorderLayout(15, 0));
        abajo.setBackground(Estilos.FONDO);

        // ----- Tabla: últimos movimientos -----
        JPanel panelMovimientos = new JPanel(new BorderLayout());
        panelMovimientos.setBackground(Estilos.GRIS_TARJETA);
        panelMovimientos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel tituloMov = new JLabel("Últimos movimientos de crédito", JLabel.CENTER);
        tituloMov.setFont(Estilos.SUBTITULO);
        panelMovimientos.add(tituloMov, BorderLayout.NORTH);

        modeloMovimientos = new DefaultTableModel(
                new Object[]{"Nombre", "Fecha", "Tipo", "Monto", "Nuevo saldo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tablaMovimientos = new JTable(modeloMovimientos);
        tablaMovimientos.setRowHeight(24);
        panelMovimientos.add(new JScrollPane(tablaMovimientos), BorderLayout.CENTER);

        // ----- Tabla: top clientes con deuda -----
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBackground(Estilos.GRIS_TARJETA);
        panelTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelTop.setPreferredSize(new Dimension(260, 0));

        JLabel tituloTop = new JLabel("Top clientes con deuda", JLabel.CENTER);
        tituloTop.setFont(Estilos.SUBTITULO);
        panelTop.add(tituloTop, BorderLayout.NORTH);

        modeloTopClientes = new DefaultTableModel(new Object[]{"Cliente", "Deuda"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tablaTop = new JTable(modeloTopClientes);
        tablaTop.setRowHeight(24);
        panelTop.add(new JScrollPane(tablaTop), BorderLayout.CENTER);

        abajo.add(panelMovimientos, BorderLayout.CENTER);
        abajo.add(panelTop, BorderLayout.EAST);

        return abajo;
    }

    @Override
    public void actualizarDatos() {
        try {
            double totalVentas = ventaDAO.sumaTotalGeneral();
            double totalPagos = pagoDAO.sumaTotalGeneral();
            double deuda = totalVentas - totalPagos;
            double pagosMes = pagoDAO.sumaMesActual();
            int totalClientes = clienteDAO.contarClientes();

            valorDeuda.setText(String.format("$%.2f", deuda));
            valorPagosMes.setText(String.format("$%.2f", pagosMes));
            valorClientes.setText(String.valueOf(totalClientes));

            cargarUltimosMovimientos();
            cargarTopClientes();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar los datos del panel:\n" + e.getMessage(),
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarUltimosMovimientos() throws SQLException {
        List<Movimiento> movimientos = new ArrayList<>();
        movimientos.addAll(ventaDAO.ultimosMovimientos(20));
        movimientos.addAll(pagoDAO.ultimosMovimientos(20));
        movimientos.sort(Comparator.comparing(Movimiento::getFecha).reversed());

        modeloMovimientos.setRowCount(0);
        int limite = Math.min(5, movimientos.size());
        for (int i = 0; i < limite; i++) {
            Movimiento m = movimientos.get(i);
            double saldo = ventaDAO.sumaPorClienteHasta(m.getCliente(), m.getFecha())
                    - pagoDAO.sumaPorClienteHasta(m.getCliente(), m.getFecha());
            modeloMovimientos.addRow(new Object[]{
                m.getCliente(), m.getFecha(), m.getTipo(),
                String.format("$%.2f", m.getMonto()),
                String.format("$%.2f", saldo)
            });
        }
    }

    private void cargarTopClientes() throws SQLException {
        List<String> clientes = ventaDAO.clientesConVentas();
        List<Object[]> filas = new ArrayList<>();
        for (String cliente : clientes) {
            double deuda = ventaDAO.sumaPorCliente(cliente) - pagoDAO.sumaPorCliente(cliente);
            filas.add(new Object[]{cliente, deuda});
        }
        filas.sort((a, b) -> Double.compare((double) b[1], (double) a[1]));

        modeloTopClientes.setRowCount(0);
        int limite = Math.min(5, filas.size());
        for (int i = 0; i < limite; i++) {
            Object[] fila = filas.get(i);
            modeloTopClientes.addRow(new Object[]{fila[0], String.format("$%.2f", (double) fila[1])});
        }
    }
}
