package vista;

import conexion.Conexion;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import javax.swing.Box;

/**
 * Ventana principal de la aplicación.
 * Estructura: menú lateral verde (oeste) + barra superior (buscar / cerrar
 * sesión) + panel central que cambia de pantalla con CardLayout.
 */
public class VentanaPrincipal extends JFrame {

    private static final String INICIO = "INICIO";
    private static final String CLIENTE = "CLIENTE";
    private static final String VENTA = "VENTA";
    private static final String PAGO = "PAGO";
    private static final String CONSULTA = "CONSULTA";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel panelTarjetas = new JPanel(cardLayout);

    private PanelInicio panelInicio;
    private PanelCliente panelCliente;
    private PanelVenta panelVenta;
    private PanelPago panelPago;
    private PanelConsulta panelConsulta;

    private String pantallaActual = INICIO;

    public VentanaPrincipal() {
        super("Sistema de Ventas");
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Estilos.FONDO);

        add(crearMenuLateral(), BorderLayout.WEST);

        JPanel derecha = new JPanel(new BorderLayout());
        derecha.setBackground(Estilos.FONDO);
        derecha.add(crearBarraSuperior(), BorderLayout.NORTH);
        derecha.add(crearPanelTarjetas(), BorderLayout.CENTER);
        add(derecha, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarAplicacion();
            }
        });

        mostrarPantalla(INICIO);
    }

    private JPanel crearMenuLateral() {
        JPanel menu = new JPanel();
        menu.setBackground(Estilos.VERDE_MENU);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setPreferredSize(new Dimension(220, 0));

        JLabel titulo = new JLabel("<html><center>Sistema de<br>control de ventas</center></html>");
        titulo.setFont(new Font("Arial", Font.BOLD, 14));
        titulo.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        menu.add(titulo);

        menu.add(crearBotonMenu("🏠  INICIO", INICIO));
        menu.add(Box.createRigidArea(new Dimension(0, 6)));
        menu.add(crearBotonMenu("👤  REGISTRAR CLIENTE", CLIENTE));
        menu.add(Box.createRigidArea(new Dimension(0, 6)));
        menu.add(crearBotonMenu("💰  REGISTRAR VENTA", VENTA));
        menu.add(Box.createRigidArea(new Dimension(0, 6)));
        menu.add(crearBotonMenu("💵  REGISTRAR PAGO", PAGO));
        menu.add(Box.createRigidArea(new Dimension(0, 6)));
        menu.add(crearBotonMenu("📊  CONSULTAR DEUDA", CONSULTA));

        menu.add(Box.createVerticalGlue());
        return menu;
    }

    private JButton crearBotonMenu(String texto, String pantalla) {
        JButton boton = new JButton(texto);
        boton.setFont(Estilos.MENU);
        boton.setBackground(Estilos.VERDE_MENU);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setAlignmentX(JButton.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(220, 40));
        boton.setPreferredSize(new Dimension(220, 40));
        boton.addActionListener(e -> mostrarPantalla(pantalla));
        return boton;
    }

    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Estilos.BLANCO);
        barra.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JTextField buscar = new JTextField("🔍 Buscar cliente...");
        buscar.setForeground(Color.GRAY);
        buscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String texto = buscar.getText().trim();
                    if (!texto.isEmpty() && !texto.startsWith("🔍")) {
                        mostrarPantalla(CONSULTA);
                        panelConsulta.buscarDesdeFuera(texto);
                    }
                }
            }
        });
        barra.add(buscar, BorderLayout.CENTER);

        JButton cerrarSesion = new JButton("Cerrar sesión");
        cerrarSesion.setFocusPainted(false);
        cerrarSesion.addActionListener(e -> cerrarSesion());

        JPanel derechaBarra = new JPanel();
        derechaBarra.setBackground(Estilos.BLANCO);
        derechaBarra.add(cerrarSesion);
        barra.add(derechaBarra, BorderLayout.EAST);

        return barra;
    }

    private JPanel crearPanelTarjetas() {
        panelInicio = new PanelInicio();
        panelCliente = new PanelCliente();
        panelVenta = new PanelVenta();
        panelPago = new PanelPago();
        panelConsulta = new PanelConsulta();

        panelTarjetas.setBackground(Estilos.FONDO);
        panelTarjetas.add(panelInicio, INICIO);
        panelTarjetas.add(panelCliente, CLIENTE);
        panelTarjetas.add(panelVenta, VENTA);
        panelTarjetas.add(panelPago, PAGO);
        panelTarjetas.add(panelConsulta, CONSULTA);

        return panelTarjetas;
    }

    private void mostrarPantalla(String pantalla) {
        pantallaActual = pantalla;
        cardLayout.show(panelTarjetas, pantalla);
        if (INICIO.equals(pantalla)) {
            panelInicio.actualizarDatos();
        } else if (CLIENTE.equals(pantalla)) {
            panelCliente.actualizarDatos();
        } else if (VENTA.equals(pantalla)) {
            panelVenta.actualizarDatos();
        } else if (PAGO.equals(pantalla)) {
            panelPago.actualizarDatos();
        } else if (CONSULTA.equals(pantalla)) {
            panelConsulta.actualizarDatos();
        }
    }

    private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Seguro que quieres cerrar sesión?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            dispose();
            new VentanaLogin().setVisible(true);
        }
    }

    private void cerrarAplicacion() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Seguro que quieres salir del sistema?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            Conexion.cerrarConexion();
            dispose();
            System.exit(0);
        }
    }
}
